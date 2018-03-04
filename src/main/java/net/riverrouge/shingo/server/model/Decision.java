package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A Decision which needs to be made to compute the next step in the workflow execution
 */
@Entity
public class Decision {

  @Id
  private Long id;

  private String name;

  private Ref<Execution> execution;

  public Decision() {}

  public Decision(String name, Execution execution) {
    this.name = name;
    this.execution = Ref.create(execution);
  }

  public String getName() {
    return name;
  }

  public Execution getWorkflow() {
    return execution.get();
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("name", name)
        .add("execution", getWorkflow())
        .toString();
  }
}
