package net.riverrouge.shingo.server.api;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import net.riverrouge.shingo.server.model.Decision;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for GenericResponse
 */
public class GenericResponseTest {

  private final GenericResponse response = new GenericResponse();

  @Test
  public void testHasErrors() throws Exception {
    assertTrue(response.getErrors().isEmpty());
    assertFalse(response.hasErrors());
    response.addErrorMessage(new ErrorMessage("500", "Server error", "Woops"));
    assertTrue(response.hasErrors());
    assertFalse(response.getErrors().isEmpty());
  }

  @Test
  public void testPutDetail() throws Exception {
    assertTrue(response.getDetails().isEmpty());
    response.putDetail("Key", "Val");
    assertEquals("Val", response.getDetail("Key"));
    assertFalse(response.getDetails().isEmpty());
  }

  @Test
  public void testGetCreationTimestamp() throws Exception {
    Date oldTimestamp = response.getCreationTimestamp();
    assertNotNull(oldTimestamp);

    Calendar calendar = GregorianCalendar.getInstance();
    calendar.add(Calendar.HOUR, 3);
    Date newTimestamp = calendar.getTime();
    assertFalse(oldTimestamp.equals(newTimestamp));
    response.setCreationTimestamp(newTimestamp);
    assertEquals(newTimestamp, response.getCreationTimestamp());
  }

  @Test
  public void testSetDetails() throws Exception {
    assertTrue(response.getDetails().isEmpty());
    Map<String, String> newMap = new HashMap<>();
    newMap.put("here", "there");
    response.setDetails(newMap);
    assertEquals("there", response.getDetail("here"));
  }

  @Test
  public void testGetDecision() throws Exception {
    assertEquals(null, response.getDecision());
    Decision decision = new Decision();
    response.setDecision(decision);
    assertEquals(decision, response.getDecision());
  }
}
