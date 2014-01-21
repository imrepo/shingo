package net.riverrouge.shingo.server.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Tests for Memo
 */
public class MemoTest {

  private static final Logger LOG = Logger.getLogger(MemoTest.class.getName());

  private Memo memo = new Memo();

  @Test
  public void testPut_and_GetNote() throws Exception {
    assertTrue(memo.getNotes().isEmpty());

    // change the original date so we test for an update without worrying that the code is too fast
    Date oldDate = memo.getLastUpdate();
    Date newDate = new Date(oldDate.getTime() - 20000);
    assertFalse(newDate.equals(oldDate));
    memo.setLastUpdate(newDate);

    memo.putNote("key", "value");
    assertEquals("value", memo.getNote("key"));
    assertFalse(memo.getLastUpdate().equals(newDate));
  }

  @Test
  public void testGetLastUpdate() throws Exception {
    assertFalse(memo.getLastUpdate() == null);
  }

  @Test
  public void testSetLastUpdate() {
    Date then = memo.getLastUpdate();
    Date now = new Date(then.getTime() - 20000);
    memo.setLastUpdate(now);
    assertFalse(then.equals(memo.getLastUpdate()));
  }

  @Test
  public void testPrintString() {
    LOG.info(memo.printString());
    System.out.print(memo.printString());
  }

  @Test
  public void testSetNotes() throws Exception {
    assertTrue(memo.getNotes().isEmpty());
    Map<String, String> newMap = new HashMap<>();
    newMap.put("here", "there");
    memo.setNotes(newMap);
    assertEquals("there", memo.getNote("here"));
  }

  @Test
  public void testEquals() {
    memo.putNote("here", "there");
    Memo newMemo = new Memo();
    newMemo.putNote("here", "there");
    newMemo.setLastUpdate(memo.getLastUpdate());
    assertEquals(memo, newMemo);
  }

}
