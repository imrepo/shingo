package net.riverrouge.shingo.server.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

/**
 *  Tests for WorkflowType
 */
public class WorkflowTypeTest {

  private final String name = "Variant workflow";
  private final String version = "V1";
  private final String description = "Simple annotation and persistence of a vcf";
  private final int timeout = 3600;
  private WorkflowType workflowType = new WorkflowType(name, version, description, timeout);

  @Test
  public void testConstruction() {
    assertEquals(name, workflowType.getName());
    assertEquals(version, workflowType.getVersion());
    assertEquals(description, workflowType.getDescription());
    assertEquals(timeout, workflowType.getDefaultActivityTaskTimeout());
  }

  @Test
  public void testDeprecate() {
    assertFalse(workflowType.isDeprecated());
    workflowType.deprecate();
    assertTrue(workflowType.isDeprecated());
  }

  @Test
  public void testSetName() {
    String newName = workflowType.getName().toUpperCase();
    assertFalse(newName.equals(workflowType.getName()));
    workflowType.setName(newName);
    assertTrue((newName.equals(workflowType.getName())));
  }

  @Test
  public void testSetVersion() {
    workflowType.setVersion("999");
    assertEquals("999", workflowType.getVersion());
  }

  @Test
  public void testSetDescription() {
    workflowType.setDescription("new description!");
    assertEquals("new description!", workflowType.getDescription());
  }

  @Test
  public void testSetStatus() {
    assertEquals(WorkflowStatus.REGISTERED, workflowType.getStatus());
    workflowType.setStatus(WorkflowStatus.DEPRECATED);
    assertEquals(WorkflowStatus.DEPRECATED, workflowType.getStatus());
  }

  @Test
  public void testGetCreationDate() {
    Date creationDate = workflowType.getCreationDate();
    assertNotNull(creationDate);
  }

  @Test
  public void testGetDeprecationDate() {
    assertEquals(null, workflowType.getDeprecationDate());
    assertNotNull(workflowType.getDeprecationDate());
  }

  @Test
  public void testSetDefaultActivityTaskTimeout() {
    workflowType.setDefaultActivityTaskTimeout(234243);
    assertEquals(234243, workflowType.getDefaultActivityTaskTimeout());
  }

  @Test
  public void testSetId() {
    Long newId = 1331313l;
    workflowType.setId(newId);
    assertEquals(newId, workflowType.getId());
  }
}
