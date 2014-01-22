package net.riverrouge.shingo.server.api;

import net.riverrouge.shingo.server.db.Datastore;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import net.riverrouge.shingo.server.model.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Facade implements the logic behind the restful api. It is designed to be easily testable
 * without the complication of Oauth.
 */
public class Facade {

  private static final Logger LOG = Logger.getLogger(Facade.class.getName());

  // Define Queue name constants
  /*
   * A queue of decision tasks, each representing a decision that needs to be made to control the
   * workflow
   */
  static final String DECISION_QUEUE_NAME = "decision-queue";
  /*
   * A queue of generic tasks, which in total comprise all the work pending or in process.
   */
  static final String TASK_QUEUE_NAME = "task-queue";

  // WORKFLOW TYPE API
  /**
   * Registers a new kind of workflow with the system.
   * @param name            The name of the workflow type
   * @param version         The version of the workflow type. They can evolve over time
   * @param description     A text String describing the workflow
   * @param defaultTimeout  The default number of seconds that a given task can run before we
   *                        timeout the lease on the task, and try again.
   *
   * @return A generic response annotated with details about the new workflow type
   */
  public static GenericResponse registerWorkflowType(String name, String version,
                                                    String description,
                                          int defaultTimeout) {

    WorkflowType workflowType = new WorkflowType(name, version, description, defaultTimeout);
    Datastore.saveWorkflowType(workflowType);
    GenericResponse response = new GenericResponse();
    response.putDetail("name", workflowType.getName());
    response.putDetail("version", workflowType.getVersion());
    response.putDetail("description", workflowType.getDescription());
    response.putDetail("status", workflowType.getStatus().name());
    response.putDetail("timeout", "" + workflowType.getDefaultActivityTaskTimeout());
    return response;
  }

  /**
   * Marks the workflow identified by the given name and version as deprecated
   */
  public static GenericResponse deprecateWorkflowType(String name, String version) {
    GenericResponse response = new GenericResponse();
    WorkflowType workflowType = Datastore.fetchWorkflowType(name, version);
    workflowType.deprecate();
    Datastore.saveWorkflowType(workflowType);
    return response;
  }

  // WORKFLOW API

  /**
   * Starts a workflow execution of the given workflow type and version
   * @param executionId                 A unique, client provided id for the execution
   * @param typeName                    The name of the type of workflow to start
   * @param version                     The version of the type of workflow to start
   * @param memo                        A memo object containing data for use later in the flow
   * @return A generic response object indicating success or containing one or more errors
   */
  public static GenericResponse startWorkflow(String executionId, String typeName, String version,
                                        Memo memo) {
    GenericResponse response = new GenericResponse();
    WorkflowType workflowType = Datastore.fetchWorkflowType(typeName, version);
    if(workflowType == null) {
      LOG.severe("Unable to retrieve workflow type: " + typeName + " " + version);
      ErrorMessage message = new ErrorMessage("404", "Not Found", "There is no workflow type with" +
          " the given name and version number.");
      response.addErrorMessage(message);
      return response;
    }
    if (workflowType.isDeprecated()) {
      LOG.severe("Deprecated workflow: " + typeName + " " + version + " was requested.");
      ErrorMessage message = new ErrorMessage("422", "Unprocessable Entity",
          "The workflow type and version requested has been deprecated.");
      response.addErrorMessage(message);
      return response;
    }
    // Todo(ljw1001): Add exception handling for non unique execution ids
    Execution execution = new Execution(workflowType, executionId, memo);
    Decision decision = new Decision("initiate workflow", execution);
    execution.addNewEvent(EventType.WORKFLOW_STARTED, execution.getExecutionId());
    Datastore.saveExecution(execution);
    Datastore.saveDecision(decision);
    execution.addNewEvent(EventType.DECISION_SCHEDULED, decision.getName());
    Datastore.saveExecution(execution);
    decisionQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .taskName(Long.toString(decision.getId()))
        .payload(execution.getExecutionId())
        .tag(decisionTag(typeName, version)));
    response.putDetail("action", "start workflow");
    response.putDetail("result", "success");
    return response;
  }

  public static GenericResponse completeWorkflow(Decision decision) {
    Execution execution = decision.getWorkflow();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_COMPLETED, execution.getExecutionId());
    Datastore.saveExecution(execution);
    return new GenericResponse();
  }

  public static GenericResponse failWorkflow(Decision decision) {
    Execution execution = decision.getWorkflow();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_FAILED, execution.toString());
    Datastore.saveExecution(execution);
    return new GenericResponse();
  }

  public static GenericResponse cancelWorkflow(Decision decision) {
    Execution execution = decision.getWorkflow();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_CANCELED, execution.toString());
    Datastore.saveExecution(execution);
    return new GenericResponse();
  }

  // DECISION API
  public static GenericResponse getDecision(String workflowTypeName, String version) {
    GenericResponse response = new GenericResponse();

    List<TaskHandle> tasks = decisionQueue().leaseTasksByTag(
            1,
            TimeUnit.SECONDS,
            120,
            decisionTag(workflowTypeName, version));
    if (tasks.isEmpty()) {
      LOG.info("No decision found in the decision queue");
      return response;
    } else {
      Decision decision = Datastore.fetchDecision(Long.parseLong(tasks.get(0).getName()));
      if (decision == null) {
        LOG.info("Decision found in decision queue, but it is NOT in the datastore");
      } else {
        Execution execution = decision.getWorkflow();
        execution.addNewEvent(EventType.DECISION_STARTED, decision.getName());
        response.setDecision(decision);
        Datastore.saveExecution(execution);
      }
      return response;
    }
  }

  // TASK API
  public static GenericResponse scheduleTask(String taskType, Decision decision) {

    // Record the decision completed event
    Execution execution = decision.getWorkflow();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());

    //schedule the task
    Task task = new Task(taskType, execution);
    WorkflowType workflowType = execution.getWorkflowType();
    String taskTag = taskTag(workflowType.getName(), workflowType.getVersion(),
        task.getTaskType());
    Datastore.saveTask(task);

    taskQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .taskName(Long.toString(task.getId()))
        .payload(task.getExecution().getExecutionId())
        .tag(taskTag.getBytes())
    );

    // now add the event
    execution.addNewEvent(EventType.TASK_SCHEDULED, Long.toString(task.getId()));
    Datastore.saveExecution(execution);
    return new GenericResponse();
  }

  /**
   * Marks the task identifed by the given Id as completed, and queues a decision about what to
   * do next
   */
  public static GenericResponse completeTask(Long taskId) {

    GenericResponse response = new GenericResponse();
    Task task = Datastore.fetchTask(taskId);
    Execution execution = task.getExecution();

    //record the completed activity in the events list
    execution.addNewEvent(EventType.TASK_COMPLETED, task.toString());

    //schedule a decision
    Decision decision = new Decision(task.getTaskType() + " completed", execution);

    addDecision(decision);

    execution.addNewEvent(EventType.DECISION_SCHEDULED, decision.toString());
    Datastore.saveExecution(execution);
    response.putDetail("action", "start workflow");
    response.putDetail("result", "success");

    return response;
  }

  public static GenericResponse getTask(String workflowTypeName, String workflowTypeVersion, String tag) {
    GenericResponse response = new GenericResponse();
    String taskTag = taskTag(workflowTypeName, workflowTypeVersion, tag);
    List<TaskHandle> tasks =
        taskQueue().leaseTasksByTag(
            1,
            TimeUnit.SECONDS, 120,
            taskTag);
    if (tasks.isEmpty()) {
      LOG.info("No tasks in queue found that match the tag " + taskTag);
    } else {
      TaskHandle taskHandle = tasks.get(0);
      LOG.info("The taskhandle found is " + taskHandle.toString());
      Task task = Datastore.fetchTask(Long.parseLong(taskHandle.getName()));
      Execution execution = task.getExecution();
      execution.addNewEvent(EventType.TASK_STARTED, task.toString());
    }
    return response;
  }

  private static void addDecision(Decision decision) {
    decisionQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .payload(decision.getWorkflow().toString()) //TODO(ljw1001): FIX THIS LINE
    );
  }

  private static Queue decisionQueue() {
    return QueueFactory.getQueue(DECISION_QUEUE_NAME);
  }

  private static Queue taskQueue() {
    return QueueFactory.getQueue(TASK_QUEUE_NAME);
  }

  private static String decisionTag(String name, String version) {
    return name + ":" + version;
  }

  private static String taskTag(String name, String version, String tag) {
    return name + ":" + version + "#" + tag;
  }
}
