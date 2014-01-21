package net.riverrouge.shingo.server;

import static net.riverrouge.shingo.server.TestConstants.*;

import net.riverrouge.shingo.server.api.Facade;
import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Task;

/**
 * Implements a test decider that polls for decisions that need to be made
 */
public class TestDecider implements Runnable {

  private String workflowTypeName;
  private String version;

  /**
   * Constructs a decider, passing it the workflow type name and version it needs to fetch
   * decisions
   */
  TestDecider(String workflowTypeName, String version) {
    this.workflowTypeName = workflowTypeName;
    this.version = version;
  }

  /**
   * Decide handles a decision task and decides what to do.
   * The decisions take the form of actions: schedule an activity, cancel the workflow,
   * mark it complete, etc.
   */
  private void decide(Decision decision) {

    Execution execution = decision.getExecution();

    if (decision.getName().equals(INITIATE_EXECUTION_DECISION)) {
      // schedule an annotation task
      Task annotationTask = new Task(ANNOTATION_TASK_TAG, execution);
      Facade.scheduleTask(annotationTask, decision);
      return;
    }

    if (decision.getName().equals(ANNOTATION_COMPLETE_DECISION)) {
      // schedule an upload task
      Task uploadTask = new Task(UPLOAD_TASK_TAG, execution);
      Facade.scheduleTask(uploadTask, decision);
      return;
    }

    if (decision.getName().equals(UPLOAD_COMPLETE_DECISION)) {
      // end the workflow
      execution.getMemo().putNote("result", "successful completion of workflow");
      Facade.completeWorkflow(decision);
      return;
    }
    // There were no handlers to make the necessary decision.  Fail.
    decision.getExecution().getMemo().putNote("workflow result",
        "Workflow failed with Reason: no appropriate handler for this decision: " +
            decision.getName());
    Facade.failWorkflow(decision);
  }

  private Decision fetch(String workflowTypeName, String version) {
    return Facade.getDecision(workflowTypeName, version);
  }

  Decision handleDecision(String workflowTypeName, String version) {
    Decision decision = fetch(workflowTypeName, version);
    if (decision != null) {
      decide(decision);
    }
    return decision;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    handleDecision(workflowTypeName, version);
  }
}
