package net.riverrouge.shingo.server.api;

import static org.junit.Assert.assertTrue;

import net.riverrouge.shingo.server.api.Facade;
import net.riverrouge.shingo.server.db.Datastore;
import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.WorkflowType;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Facade
 */
public class FacadeTest {

  private final WorkflowType workflowType = new WorkflowType("Variant pipeline", "V1",
      "Annotates variants and stores them", 3600);

  private final static String INITIATE_EXECUTION_DECISION = "initiate workflow";

  private static String dir;

  static {
    dir = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/queue.xml";
  }

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig(),
          new LocalTaskQueueTestConfig().setQueueXmlPath(dir));

  @Before
  public void setUp() {
    helper.setUp();
    Datastore.saveWorkflowType(workflowType);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDeprecateWorkflowType() throws Exception {
    GenericResponse response =
        Facade.deprecateWorkflowType(workflowType.getName(), workflowType.getVersion());
    // Todo(ljw1001): Add the workflow type data to the response using notes
    // assertTrue(workflowType1.isDeprecated());
  }

  @Test
  public void testStartWorkflow() throws Exception {
    Facade.startWorkflow("12345", workflowType.getName(),
        workflowType.getVersion(), INITIATE_EXECUTION_DECISION, new Memo());
  }

  @Test
  public void testCompleteWorkflow() throws Exception {
    Execution execution = new Execution(workflowType, "2424", new Memo());
    Facade.startWorkflow("2424", workflowType.getName(),
        workflowType.getVersion(), INITIATE_EXECUTION_DECISION, new Memo());
    Decision decision = new Decision("Complete?", execution);
    Datastore.saveDecision(decision);
    Facade.completeWorkflow(decision);
  }

  @Test
  public void testFailWorkflow() throws Exception {

  }

  @Test
  public void testCancelWorkflow() throws Exception {

  }

  @Test
  public void testGetDecision() throws Exception {

  }

  @Test
  public void testScheduleTask() throws Exception {

  }

  @Test
  public void testCompleteTask() throws Exception {

  }

  @Test
  public void testGetTask() throws Exception {

  }
}
