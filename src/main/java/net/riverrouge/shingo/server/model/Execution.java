package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains the state of a workflow execution
 */
@Entity
public class Execution {

  @Id
  private String executionId;
  private List<HistoryEvent> events = new ArrayList<>();
  private Memo memo;
  @Load
  private Ref<WorkflowType> workflowType;

  public Execution(){}

  public Execution(WorkflowType workflowType, String executionId, Memo memo) {
    this.workflowType =  Ref.create(workflowType);
    this.executionId = executionId;
    this.memo = memo;
  }

  public WorkflowType getWorkflowType() {
    if (workflowType == null) {
      return null;
    }
    return workflowType.get();
  }

  public String getExecutionId() {
    return executionId;
  }

  public List<HistoryEvent> getEvents() {
    return events;
  }

  public Memo getMemo() {
    return memo;
  }

  public void setWorkflowType(WorkflowType workflowType) {
    this.workflowType = Ref.create(workflowType);
  }

  public void setMemo(Memo memo) {
    this.memo = memo;
  }

  public void addNewEvent(EventType eventType, String label) {
    HistoryEvent event = new HistoryEvent(eventType, label);
    events.add(event);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("executionId", executionId)
        .add("events", events)
        .add("memo", memo)
        .add("workflowType", getWorkflowType())
        .toString();
  }

  public String toJsonString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Execution execution = (Execution) o;

    if (events != null ? !events.equals(execution.events) : execution.events != null) return false;
    if (executionId != null ? !executionId.equals(execution.executionId) : execution.executionId != null)
      return false;
    if (memo != null ? !memo.equals(execution.memo) : execution.memo != null) return false;
    if (workflowType != null ? !workflowType.equals(execution.workflowType) : execution.workflowType != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = executionId != null ? executionId.hashCode() : 0;
    result = 31 * result + (events != null ? events.hashCode() : 0);
    result = 31 * result + (memo != null ? memo.hashCode() : 0);
    result = 31 * result + (workflowType != null ? workflowType.hashCode() : 0);
    return result;
  }
}
