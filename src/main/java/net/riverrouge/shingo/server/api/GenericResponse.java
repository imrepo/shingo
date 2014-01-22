package net.riverrouge.shingo.server.api;

import net.riverrouge.shingo.server.model.Decision;

import java.util.*;

/**
 *
 */
public class GenericResponse {

  private List<ErrorMessage> errors = new ArrayList<>();
  private Date lastUpdate;
  private Map<String, String> notes = new HashMap<>();
  private Decision decision;

  public GenericResponse() {
    lastUpdate = new Date();
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
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

  public void addErrorMessage(ErrorMessage error) {
    errors.add(error);
  }

  public List<ErrorMessage> getErrors() {
    return errors;
  }

  public Decision getDecision() {
    return decision;
  }

  public void setDecision(Decision decision) {
    this.decision = decision;
  }
}
