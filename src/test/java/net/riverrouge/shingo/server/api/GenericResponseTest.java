package net.riverrouge.shingo.server.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for GenericResponse
 */
public class GenericResponseTest {

  private final GenericResponse response = new GenericResponse();

  @Test
  public void testHasErrors() throws Exception {
    assertFalse(response.hasErrors());
    response.addErrorMessage(new ErrorMessage("500", "Server error", "Woops"));
    assertTrue(response.hasErrors());
  }

  @Test
  public void testPutDetail() throws Exception {

  }

  @Test
  public void testGetCreationTimestamp() throws Exception {
    assertNotNull(response.getCreationTimestamp());
  }

  @Test
  public void testGetDetails() throws Exception {

  }

  @Test
  public void testAddErrorMessage() throws Exception {

  }

  @Test
  public void testGetDecision() throws Exception {

  }
}
