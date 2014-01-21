package net.riverrouge.shingo.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import net.riverrouge.shingo.server.model.EventType;
import net.riverrouge.shingo.server.model.HistoryEvent;
import org.junit.Test;

import java.util.Date;

/**
 * Tests for HistoryEvent
 */
public class HistoryEventTest {

  private final String label = "X task done";
  HistoryEvent event = new HistoryEvent(EventType.TASK_COMPLETED, label);

  @Test
  public void testGetEventType() {
    assertEquals(EventType.TASK_COMPLETED, event.getEventType());
    event.setEventType(EventType.WORKFLOW_STARTED);
    assertEquals(EventType.WORKFLOW_STARTED, event.getEventType());
  }

  @Test
  public void testGetTimestamp() {
    Date oldTimestamp = event.getTimestamp();
    Date newTimestamp = new Date(oldTimestamp.getTime() + 100000);
    assertFalse(newTimestamp.equals(event.getTimestamp()));
    event.setTimestamp(newTimestamp);
    assertEquals(newTimestamp, event.getTimestamp());
  }

  @Test
  public void testGetLabel() {
    assertEquals(label, event.getLabel());
    String newLabel = label.toUpperCase();
    assertFalse(newLabel.equals(event.getLabel()));
    event.setLabel(newLabel);
    assertEquals(newLabel, event.getLabel());
  }
}
