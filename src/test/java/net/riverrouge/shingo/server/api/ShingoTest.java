package net.riverrouge.shingo.server.api;

import static org.junit.Assert.assertTrue;

import net.riverrouge.shingo.server.db.Datastore;
import net.riverrouge.shingo.server.model.WorkflowType;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ShingoTest {

  private final WorkflowType workflowType = new WorkflowType("Variant pipeline", "V1",
      "Annotates variants and stores them", 3600);

  private Shingo api;


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
    api = new Shingo();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }


  @Test
  public void testGetDecision() throws Exception {

  }

  @Test
  public void testScheduleTask() throws Exception {

  }

  @Test
  public void testGetTask() throws Exception {

  }

  @Test
  public void testCompleteTask() throws Exception {

  }

  @Test
  public void testRegisterWorkflowType() throws Exception {
/*
    api.registerWorkflowType(workflowType.getName(), workflowType.getVersion(),
        workflowType.getDescription(), workflowType.getDefaultActivityTaskTimeout());

    WorkflowType fromDB = Datastore.fetchWorkflowType(workflowType.getName(),
        workflowType.getVersion());
    assertTrue(fromDB != null);
*/
  }

  @Test
  public void testDeprecateWorkflowType() throws Exception {
    GenericResponse genericResponse =
        api.deprecateWorkflowType(null, workflowType.getName(), workflowType.getVersion());

    WorkflowType fromDB = Datastore.fetchWorkflowType(workflowType.getName(),
        workflowType.getVersion());
    assertTrue(fromDB.isDeprecated());
  }

  @Test
  public void testStartNewExecution() throws Exception {

  }

  @Test
  public void testCompleteExecution() throws Exception {

  }

  @Test
  public void testCancelExecution() throws Exception {

  }
}
