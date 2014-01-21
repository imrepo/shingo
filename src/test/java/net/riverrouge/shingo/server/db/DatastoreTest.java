package net.riverrouge.shingo.server.db;

import static org.junit.Assert.assertTrue;

import net.riverrouge.shingo.server.db.Datastore;
import net.riverrouge.shingo.server.model.Decision;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.WorkflowType;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Datastore
 */
public class DatastoreTest {

  private final String name = "Variant workflow";
  private final String version = "V1";
  private final String description = "Simple annotation and persistence of a vcf";
  private final int timeout = 3600;
  private WorkflowType workflowType = new WorkflowType(name, version, description, timeout);

  private final String executionId = "23423423";

  private Execution execution;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    Datastore.saveWorkflowType(workflowType);
    execution = new Execution(workflowType, executionId, new Memo());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testSaveExecution() throws Exception {
    Datastore.saveExecution(execution);
  }

  @Test
  public void testSaveDecision() throws Exception {
    Decision decision = new Decision("decision 1", execution);
    Datastore.saveDecision(decision);
    assertTrue(decision.getId() > 0);
  }

  @Test
  public void testSaveWorkflowType() throws Exception {
    Datastore.saveWorkflowType(workflowType);
    assertTrue(workflowType.getId() > 0);
  }

  @Test
  public void testFetchWorkflowType() throws Exception {

  }
}
