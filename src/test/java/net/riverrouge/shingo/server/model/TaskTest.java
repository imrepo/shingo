package net.riverrouge.shingo.server.model;

import static org.junit.Assert.*;

import net.riverrouge.shingo.server.db.Datastore;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.Task;
import net.riverrouge.shingo.server.model.WorkflowType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Task
 */
public class TaskTest {
  private final String name = "Variant workflow";
  private final String version = "V1";
  private final String description = "Simple annotation and persistence of a vcf";
  private final int timeout = 3600;
  private WorkflowType workflowType = new WorkflowType(name, version, description, timeout);
  private Execution execution = new Execution(workflowType, "12345", new Memo());

  private final Task task = new Task("Upload file", execution);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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
  public void testGetId() {
    assertTrue(task.getId() == null);
    task.setId(132342l);
    assertEquals(new Long(132342l), task.getId());
  }

  @Test
  public void testGetTaskType() {
    assertTrue(task.getTaskType() == null);
    assertEquals("Upload file", task.getTaskType());
  }

  @Test
  public void testGetExecution() {
    assertTrue(task.getExecution() == null);
    String executionId = "23423423";
    Execution execution = new Execution(workflowType, executionId, new Memo());
    Datastore.saveExecution(execution);
    task.setExecution(execution);
    assertEquals(execution, task.getExecution());
  }
}
