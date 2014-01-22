package net.riverrouge.shingo.server.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for ErrorMessage
 */

public class ErrorMessageTest {

  private final ErrorMessage forbidden = ErrorMessage.forbidden();

  @Test
  public void testGetCode() throws Exception {
    assertEquals("403", forbidden.getCode());
    forbidden.setCode("401");
    assertEquals("401", forbidden.getCode());
  }

  @Test
  public void testGetMessage() throws Exception {
    assertEquals("Forbidden", forbidden.getMessage());
    forbidden.setMessage("Not found");
    assertEquals("Not found", forbidden.getMessage());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals(
        "The client requested a resource or operation for which it was not authorized.",
        forbidden.getDescription());
    String desc = "The resource you are looking for could not be found.";
    forbidden.setDescription(desc);
    assertEquals(desc, forbidden.getDescription());
  }
}
