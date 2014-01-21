package net.riverrouge.shingo.server.db;

import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Task;
import net.riverrouge.shingo.server.model.WorkflowType;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 *
 */
public class Datastore {
  static {
      ObjectifyService.register(Decision.class);
      ObjectifyService.register(Execution.class);
      ObjectifyService.register(WorkflowType.class);
      ObjectifyService.register(Task.class);
    }

  public static void saveDecision(Decision decision) {
    ofy().save().entity(decision).now();
  }

  public static void saveExecution(Execution execution) {
    ofy().save().entity(execution).now();
  }

  public static void saveTask(Task task) {
    ofy().save().entity(task).now();
  }

  public static void saveWorkflowType(WorkflowType workflowType) {
    ofy().save().entity(workflowType).now();
  }

  public static Decision fetchDecision(Long id) {
    return ofy().load().type(Decision.class).id(id).now();
  }

  public static WorkflowType fetchWorkflowType(String name, String version) {
    return ofy().load().type(WorkflowType.class).filter("name",
        name).filter("version", version).first().now();
  }

  public static Task fetchTask(Long id) {
    return ofy().load().type(Task.class).id(id).now();
  }

  public static Execution fetchExecution(String executionName) {
    return ofy().load().type(Execution.class).id(executionName).now();
  }
}

