package net.riverrouge.shingo.server.api;
import net.riverrouge.shingo.server.db.Datastore;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.Task;
import net.riverrouge.shingo.server.model.WorkflowType;

import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Defines v1 of the Shingo API.
 */
@Api(
    name = "shingo",
    version = "v1",
    scopes = {Constants.EMAIL_SCOPE},
    clientIds = {Constants.WEB_CLIENT_ID}
)
public class Shingo {

  private static final Logger LOG = Logger.getLogger(Shingo.class.getName());

  // Decision API
  @ApiMethod(httpMethod = "get", path = "shingo/get_decision/authed")
  public GenericResponse getDecision(User user,
                              @Named("workflowTypeName") String workflowTypeName,
                              @Named("version") String version) {

    if (verifyUser(user)) {
      return Facade.getDecision(workflowTypeName, version);
    }
    return forbidden();
  }

  @ApiMethod(name = "decision.scheduleTask", httpMethod = "post",
      path = "shingo/schedule_task/authed")
  public GenericResponse scheduleTask(User user, Task task, Decision decision) {

    if (verifyUser(user)) {
      return Facade.scheduleTask(task, decision);
    }
    return forbidden();
  }

  // Task API
  @ApiMethod(name = "task.getTask", httpMethod = "get", path = "shingo/get_task/authed")
  public Task getTask(User user,
                      @Named("workflowTypeName") String workflowTypeName,
                      @Named("version") String version,
                      @Named("tag") String tag) {
    return Facade.getTask(workflowTypeName, version, tag);
  }

  @ApiMethod(name = "task.complete", httpMethod = "post", path = "shingo/complete_task/authed")
  public GenericResponse completeTask(User user,
                               @Named("taskName") Long taskId) {
    Task newTask = Datastore.fetchTask(taskId);
    return Facade.completeTask(newTask, "done with something");
  }

  // WORKFLOW TYPE API

  @ApiMethod(name = "workflowtype.register", httpMethod = "post",
      path = "shingo/register_workflow/authed")
  public GenericResponse registerWorkflowType(User user,
                                   @Named("name") String name,
                                   @Named("version") String version,
                                   @Named("description") String description,
                                   @Named("defaultActivityTimeout") int defaultTimeout) {
    LOG.fine("Received workflow type registration request");
    if (verifyUser(user)) {
      return Facade.registerWorkflowType(name, version, description, defaultTimeout);
    }
    return forbidden();
  }

  @ApiMethod(name = "workflowtype.deprecate", httpMethod = "post",
      path = "shingo/deprecate_workflow/authed")
  public WorkflowType deprecateWorkflowType(User user,
                                            @Named("name") String name,
                                            @Named("version") String version) {

    WorkflowType workflowType = Datastore.fetchWorkflowType(name, version);
    return Facade.deprecateWorkflowType(workflowType);
  }

  // WORKFLOW API

  @ApiMethod(name = "execution.start", httpMethod = "post",
      path = "shingo/start_execution/authed")
  public GenericResponse startNewExecution(User user,
                                    @Named("executionId") String executionId,
                                    @Named("workflowTypeName") String typeName,
                                    @Named("version") String version,
                                    @Named("initiateExecutionDecision") String
                                        initiateExecutionDecision,
                                    Memo memo) {

    LOG.fine("Handling start workflow request: " + typeName + " " + "version [" + executionId +"]");
    return Facade.startWorkflow(executionId, typeName, version, initiateExecutionDecision, memo);
  }

  @ApiMethod(name = "execution.complete", httpMethod = "post",
      path = "shingo/complete_execution/authed")
  public GenericResponse completeExecution(User user,
                                    Decision decision) {
    return Facade.completeWorkflow(decision);
  }

  @ApiMethod(name = "execution.cancel", httpMethod = "post",
      path = "shingo/cancel_execution/authed")
  public GenericResponse cancelExecution(User user,
                                  Decision decision) {
    return Facade.cancelWorkflow(decision);
  }

  @ApiMethod(name = "execution.fail", httpMethod = "post", path = "shingo/fail_execution/authed")
  public GenericResponse failExecution(User user,
                                Decision decision) {
    return Facade.failWorkflow(decision);
  }

  private boolean verifyUser(User user) {
    return true;
  }

  private GenericResponse forbidden() {
    ErrorMessage error = new ErrorMessage("403", "Forbidden", "The client requested a resource " +
        "for which it was not authorized");
    GenericResponse response = new GenericResponse();
    response.addErrorMessage(error);
    return response;
  }
}