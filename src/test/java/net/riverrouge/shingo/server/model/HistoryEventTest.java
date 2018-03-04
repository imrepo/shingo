package net.riverrouge.shingo.server.model;

import net.riverrouge.shingo.server.model.EventType;
import net.riverrouge.shingo.server.model.HistoryEvent;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Tests for HistoryEvent
 */
public class HistoryEventTest {

  private final String label = "X task done";
  HistoryEvent event = new HistoryEvent(EventType.TASK_COMPLETED, label);

  @Test
  public void testGetEventType() {
    assertEquals(EventType.TASK_COMPLETED, event.getEventType());
  }

  @Test
  public void testGetTimestamp() {
    Date oldTimestamp = event.getTimestamp();
    assertNotNull(oldTimestamp);
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
