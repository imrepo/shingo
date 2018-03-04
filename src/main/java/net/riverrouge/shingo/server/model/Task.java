package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Task represents work to be done by an external program that is a client of shingo.
 */
@Entity
public class Task {

  @Id
  private Long id;
  private String taskType;
  private Ref<Execution> execution;

  public Task() {}

  public Task(String taskType, Execution execution) {
    this.taskType = taskType;
    this.setExecution(execution);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTaskType() {
    return taskType;
  }

  public Execution getExecution() {
    if (execution == null) {
      return null;
    }
    return execution.get();
  }

  public void setExecution(Execution execution) {
    this.execution = Ref.create(execution);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("taskType", taskType)
        .add("execution", execution)
        .toString();
  }
}
