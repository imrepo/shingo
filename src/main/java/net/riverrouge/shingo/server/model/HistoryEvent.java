package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Embed;

import java.util.Date;

/**
 * Records an occurrance or event in the life cycle of a workflow. Events include state
 * transitions for Workflows, Decisions and Tasks.
 */
@Embed
public class HistoryEvent {

  private EventType eventType;
  private Date timestamp;
  private String label;

  public HistoryEvent(){}

  public HistoryEvent(EventType eventType, String label){
    this.eventType = eventType;
    this.label = label;
    this.timestamp = new Date();
  }

  public EventType getEventType() {
    return eventType;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("eventType", eventType)
        .add("timestamp", timestamp)
        .add("label", label)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HistoryEvent that = (HistoryEvent) o;

    if (eventType != that.eventType) return false;
    if (label != null ? !label.equals(that.label) : that.label != null) return false;
    if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = eventType != null ? eventType.hashCode() : 0;
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    result = 31 * result + (label != null ? label.hashCode() : 0);
    return result;
  }
}
