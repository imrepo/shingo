package net.riverrouge.shingo.server.api;

import net.riverrouge.shingo.server.model.Decision;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A standard response to a workflow request from a client. It provides some information that is
 * consistent across requests, but may also contain other information that is unique to the
 * request type.
 */
public class GenericResponse {

  private List<ErrorMessage> errors = new ArrayList<>();
  private Date creationTimestamp;
  private Map<String, String> details = new HashMap<>();
  private Decision decision;

  /**
   * Constructs a generic response with a timestamp
   */
  public GenericResponse() {
    creationTimestamp = new Date();
  }

  /**
   * Returns {@code:true} if any errors were encountered. Clients should inspect the error(s) and
   * decide what to do.
   */
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  /**
   * Adds an annotation or 'note' to the response, in the form of a key-value pair
   */
  public void putDetail(String key, String value) {
    details.put(key, value);
  }

  /**
   * Returns the details value associated with the given key. May return {@code:null}.
   */
  public String getDetail(String key){
    return details.get(key);
  }

  public Date getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(Date creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public Map<String, String> getDetails() {
    return details;
  }

  public void setDetails(Map<String, String> details) {
    this.details = details;
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
