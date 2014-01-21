package net.riverrouge.shingo.server.model;

import static org.junit.Assert.*;

import net.riverrouge.shingo.server.db.Datastore;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import net.riverrouge.shingo.server.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for Execution
 */
public class ExecutionTest {

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
  public void testConstruction() {
    assertEquals(executionId, execution.getExecutionId());
    assertTrue(execution.getEvents().isEmpty());
    assertTrue(execution.getMemo().getNotes().isEmpty());
    assertEquals(workflowType, execution.getWorkflowType());
  }

  @Test
  public void testSetGetWorkflowType() {
    assertEquals(workflowType, execution.getWorkflowType());
    WorkflowType workflowType2 = new WorkflowType("Name2", "Vers2", "Desc2", 2);
    Datastore.saveWorkflowType(workflowType2);
    assertFalse(workflowType2.equals(execution.getWorkflowType()));
    execution.setWorkflowType(workflowType2);
    assertEquals(workflowType2, execution.getWorkflowType());
  }

  @Test
  public void testSetGetExecutionId() {
    assertEquals(executionId, execution.getExecutionId());
    String newExecutionId = "NEW ID";
    assertFalse(newExecutionId.equals(execution.getExecutionId()));
    execution.setExecutionId(newExecutionId);
    assertEquals(newExecutionId, execution.getExecutionId());
  }

  @Test
  public void testSetGetEvents() {
    List<HistoryEvent> events = new ArrayList<>();
    HistoryEvent event = new HistoryEvent(EventType.DECISION_FAILED, "failed");
    events.add(event);
    assertTrue(execution.getEvents().isEmpty());
    execution.setEvents(events);
    assertFalse(execution.getEvents().isEmpty());
    assertEquals(event, execution.getEvents().get(0));
  }

  @Test
  public void testSetGetMemo() {
    Memo memo = new Memo();
    memo.putNote("key", "value");
    assertTrue(execution.getMemo().getNotes().isEmpty());
    execution.setMemo(memo);
    assertFalse(execution.getMemo().getNotes().isEmpty());
    assertEquals("value", execution.getMemo().getNote("key"));
  }

  @Test
  public void testAddNewEvent() {
    assertTrue(execution.getEvents().isEmpty());

    String label = "this is a new event label";

    execution.addNewEvent(EventType.TASK_STARTED, label);
    assertFalse(execution.getEvents().isEmpty());

  }

  @Test
  public void testToJsonString() {
    System.out.println();
    System.out.println(execution.toJsonString());
  }
}
