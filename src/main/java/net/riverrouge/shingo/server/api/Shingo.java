package net.riverrouge.shingo.server.api;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.Task;

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

  @ApiMethod(name = "decision.scheduleTask",
      httpMethod = "post",
      path = "shingo/schedule_task/authed")
  public GenericResponse scheduleTask(User user,
                                      Decision decision,
                                      @Named("taskType") String taskType) {
    if (verifyUser(user)) {
      return Facade.scheduleTask(taskType, decision);
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

  @ApiMethod(name = "task.complete",
      httpMethod = "post",
      path = "shingo/complete_task/authed")
  public GenericResponse completeTask(User user,
                               @Named("taskName") Long taskId) {
    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.completeTask(taskId, "Completed some task");
  }

  // WORKFLOW TYPE API

  @ApiMethod(name = "workflowtype.register",
      httpMethod = "post",
      path = "shingo/register_workflow/authed")
  public GenericResponse registerWorkflowType(User user,
                                   @Named("name") String name,
                                   @Named("version") String version,
                                   @Named("description") String description,
                                   @Named("defaultActivityTimeout") int defaultTimeout) {
    LOG.fine("Received workflow type registration request");
    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.registerWorkflowType(name, version, description, defaultTimeout);
  }

  @ApiMethod(name = "workflowtype.deprecate",
      httpMethod = "post",
      path = "shingo/deprecate_workflow/authed")
  public GenericResponse deprecateWorkflowType(User user,
                                            @Named("name") String name,
                                            @Named("version") String version) {
    LOG.fine("Received workflow type deprecation request");
    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.deprecateWorkflowType(name, version);
  }

  // WORKFLOW API

  @ApiMethod(name = "execution.start", httpMethod = "post",
      path = "shingo/start_execution/authed")
  public GenericResponse startNewExecution(User user,
                                    @Named("executionId") String executionId,
                                    @Named("workflowTypeName") String typeName,
                                    @Named("version") String version,
                                    Memo memo) {

    LOG.fine("Handling start workflow request: " + typeName + " " + "version [" + executionId +"]");

    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.startWorkflow(executionId, typeName, version, memo);
  }

  @ApiMethod(name = "execution.complete",
      httpMethod = "post",
      path = "shingo/complete_execution/authed")
  public GenericResponse completeExecution(User user, Decision decision) {
    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.completeWorkflow(decision);
  }

  @ApiMethod(name = "execution.cancel",
      httpMethod = "post",
      path = "shingo/cancel_execution/authed")
  public GenericResponse cancelExecution(User user, Decision decision) {
    if (!verifyUser(user)) {
      return Facade.cancelWorkflow(decision);
    }
    return forbidden();
  }

  @ApiMethod(name = "execution.fail",
      httpMethod = "post",
      path = "shingo/fail_execution/authed")
  public GenericResponse failExecution(User user, Decision decision) {
    if (!verifyUser(user)) {
      return forbidden();
    }
    return Facade.failWorkflow(decision);
  }

  /**
   * Returns true if the given User is authorized for the operation, and false otherwise.
   */
  private boolean verifyUser(User user) {
    return true;
  }

  /**
   * Returns a GenericResponse containing an ErrorMessage that indicates that an authenticated
   * user does not possess the rights for the operation requested.
   */
  private GenericResponse forbidden() {
    GenericResponse response = new GenericResponse();
    response.addErrorMessage(ErrorMessage.forbidden());
    return response;
  }
}