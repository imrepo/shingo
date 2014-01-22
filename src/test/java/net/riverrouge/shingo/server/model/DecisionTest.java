package net.riverrouge.shingo.server.model;

import static org.junit.Assert.assertEquals;

import net.riverrouge.shingo.server.db.Datastore;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Decision
 */
public class DecisionTest {

  private final String name = "Decision 1";
  private final String workflowName = "Variant workflow";
  private final String version = "V1";
  private final String description = "Simple annotation and persistence of a vcf";
  private final int timeout = 3600;
  private final WorkflowType workflowType = new WorkflowType(workflowName, version, description,
      timeout);

  private Execution execution;

  private Decision decision;
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
    String executionId = "23423423";
    execution = new Execution(workflowType, executionId, new Memo());
    Datastore.saveExecution(execution);
    decision = new Decision(name, execution);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }


  @Test
  public void testGetName() throws Exception {
    assertEquals(name, decision.getName());
  }

  @Test
  public void testGetExecution() throws Exception {
    assertEquals(execution, decision.getWorkflow());
  }
}
