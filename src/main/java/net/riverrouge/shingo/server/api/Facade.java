package net.riverrouge.shingo.server.api;

import net.riverrouge.shingo.server.db.Datastore;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import net.riverrouge.shingo.server.model.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Facade implements the logic behind the restful api.
 */
public class Facade {

  private static final Logger LOG = Logger.getLogger(Facade.class.getName());

  // Define Queue name constants
  static final String DECISION_QUEUE_NAME = "decision-queue";
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
   * @return
   */
  public static Memo registerWorkflowType(String name, String version, String description,
                                          int defaultTimeout) {

    WorkflowType workflowType = new WorkflowType(name, version, description, defaultTimeout);
    Datastore.saveWorkflowType(workflowType);
    Memo memo = new Memo();
    memo.putNote("name", workflowType.getName());
    memo.putNote("version", workflowType.getVersion());
    memo.putNote("description", workflowType.getDescription());
    memo.putNote("status", workflowType.getStatus().name());
    memo.putNote("timeout", "" + workflowType.getDefaultActivityTaskTimeout());
    return memo;
  }

  public static WorkflowType deprecateWorkflowType(WorkflowType workflowType) {
    workflowType.deprecate();
    Datastore.saveWorkflowType(workflowType);
    return workflowType;
  }

  // WORKFLOW API
  public static Response startWorkflow(String executionId, String typeName, String version,
                                       String initiateExecutionDecision, Memo memo) {
    WorkflowType workflowType = Datastore.fetchWorkflowType(typeName, version);
    if(workflowType == null) {
      LOG.severe("Unable to retrieve workflow type: " + typeName + " " + version);
    }
    Execution execution = new Execution(workflowType, executionId, memo);
    if (execution.getWorkflowType().isDeprecated()) {
      return new Response(); //TODO(ljw1001): Notify client that workflow is deprecated
    }
    Decision decision = new Decision(initiateExecutionDecision, execution);
    execution.addNewEvent(EventType.WORKFLOW_STARTED, execution.getExecutionId());
    Datastore.saveExecution(execution);
    Datastore.saveDecision(decision);
    execution.addNewEvent(EventType.DECISION_SCHEDULED, decision.getName());
    Datastore.saveExecution(execution);
    decisionQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .taskName(Long.toString(decision.getId()))
        .payload(execution.getExecutionId())
        .tag(decisionTag(typeName, version)));
    return new Response();
  }

  public static Response completeWorkflow(Decision decision) {
    Execution execution = decision.getExecution();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_COMPLETED, execution.getExecutionId());
    Datastore.saveExecution(execution);
    return new Response();
  }

  public static Response failWorkflow(Decision decision) {
    Execution execution = decision.getExecution();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_FAILED, execution.toString());
    Datastore.saveExecution(execution);
    return new Response();
  }

  public static Response cancelWorkflow(Decision decision) {
    Execution execution = decision.getExecution();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    execution.addNewEvent(EventType.WORKFLOW_CANCELED, execution.toString());
    Datastore.saveExecution(execution);
    return new Response();
  }

  // DECISION API
  public static Decision getDecision(String workflowTypeName, String version) {
    List<TaskHandle> tasks =
        decisionQueue().leaseTasksByTag(
            1,
            TimeUnit.SECONDS, 120,
            decisionTag(workflowTypeName, version));
    if (tasks.isEmpty()) {
      LOG.info("No decision found in the decision queue");
      return null;
    } else {
      Decision decision = Datastore.fetchDecision(Long.parseLong(tasks.get(0).getName()));
      if (decision == null) {
        LOG.info("Decision found in decision queue, but it is NOT in the datastore");
      }
      Execution execution = decision.getExecution();
      execution.addNewEvent(EventType.DECISION_STARTED, decision.getName());
      return decision;
    }
  }

  // TASK API
  public static Response scheduleTask(Task task, Decision decision) {

    Execution execution = decision.getExecution();
    execution.addNewEvent(EventType.DECISION_COMPLETED, decision.getName());
    WorkflowType workflowType = execution.getWorkflowType();

    //schedule the task
    String taskTag = taskTag(workflowType.getName(), workflowType.getVersion(),
        task.getTaskType());
    LOG.info("Task tag for new task is " + taskTag);

    Datastore.saveTask(task);

    taskQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .taskName(Long.toString(task.getId()))
        .payload(task.getExecution().getExecutionId())
        .tag(taskTag.getBytes())
    );

    // now add the event
    execution.addNewEvent(EventType.TASK_SCHEDULED, Long.toString(task.getId()));
    Datastore.saveExecution(execution);
    return new Response();
  }

  public static Response completeTask(Task task, String comment) {
    Execution execution = task.getExecution();

    //record the completed activity in the events list
    execution.addNewEvent(EventType.TASK_COMPLETED, task.toString());

    //schedule a decision
    Decision decision = new Decision(task.getTaskType() + " completed", execution);

    addDecision(decision);

    execution.addNewEvent(EventType.DECISION_SCHEDULED, decision.toString());
    return new Response();
  }

  public static Task getTask(String workflowTypeName, String workflowTypeVersion, String tag) {
    String taskTag = taskTag(workflowTypeName, workflowTypeVersion, tag);
    List<TaskHandle> tasks =
        taskQueue().leaseTasksByTag(
            1,
            TimeUnit.SECONDS, 120,
            taskTag);
    if (tasks.isEmpty()) {
      LOG.info("No tasks in queue found that match the tag " + taskTag);

      List<TaskHandle> all = taskQueue().leaseTasks(100, TimeUnit.SECONDS, 300);
      try {
        if (all.get(0).getTag().equals(taskTag)) {
          LOG.severe("Match");
        }
        else {
          LOG.severe("NO MATCH");
        }
        for (TaskHandle task: all) {
          LOG.info(task.toString());
        }
      } catch (UnsupportedEncodingException e) {
        LOG.severe(e.getMessage());
      }
      return null;
    } else {
      TaskHandle taskHandle = tasks.get(0);
      LOG.info("The taskhandle found is " + taskHandle.toString());
      Task task = Datastore.fetchTask(Long.parseLong(taskHandle.getName()));
      Execution execution = task.getExecution();
      execution.addNewEvent(EventType.TASK_STARTED, task.toString());
      return task;
    }
  }

  private static void addDecision(Decision decision) {
    decisionQueue().add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
        .payload(decision.getExecution().toString()) //TODO(ljw1001): FIX THIS LINE
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
