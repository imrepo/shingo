package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EmbedMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A memorandum added to a workflow execution to store state that might be of use in later
 * processing steps. Clients should add and remove notes using putNote() and
 * getNote().
 */
@Embed
public class Memo {

  private Date lastUpdate;
  @EmbedMap
  private Map<String, String> notes = new HashMap<>();

  public Memo(){
    lastUpdate = new Date();
  }

  public void putNote(String key, String value) {
    notes.put(key, value);
    lastUpdate = new Date();
  }

  public String getNote(String key){
    return notes.get(key);
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public Map<String, String> getNotes() {
    return notes;
  }

  public void setNotes(Map<String, String> notes) {
    this.notes = notes;
  }

  public String printString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Memo:")
        .append("\\r")
        .append("Last update: ")
        .append(getLastUpdate());
    for (String note : notes.keySet()){
      builder.append(note)
          .append(": ")
          .append(notes.get(note))
          .append('\r');
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("lastUpdate", lastUpdate)
        .add("notes", notes)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Memo memo = (Memo) o;

    if (lastUpdate != null ? !lastUpdate.equals(memo.lastUpdate) : memo.lastUpdate != null)
      return false;
    if (notes != null ? !notes.equals(memo.notes) : memo.notes != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = lastUpdate != null ? lastUpdate.hashCode() : 0;
    result = 31 * result + (notes != null ? notes.hashCode() : 0);
    return result;
  }
}
