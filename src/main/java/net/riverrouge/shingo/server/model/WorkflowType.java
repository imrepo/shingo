package net.riverrouge.shingo.server.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Represents a Workflow definition
 */
@Entity
public class WorkflowType {
  @Id
  private Long id;
  @Index
  private String name;
  @Index
  private String version;
  private String description;
  private WorkflowStatus status;
  private Date creationDate;
  private Date deprecationDate;
  private int defaultActivityTaskTimeout;

  public WorkflowType(){}

  public WorkflowType(String name, String version, String description,
                      int defaultActivityTaskTimeout) {
    this.name = name;
    this.version = version;
    this.description = description;
    this.defaultActivityTaskTimeout = defaultActivityTaskTimeout;
    this.creationDate = new Date();
    this.status = WorkflowStatus.REGISTERED;
  }

  public void deprecate() {
    this.deprecationDate = new Date();
    this.status = WorkflowStatus.DEPRECATED;
  }

  public boolean isDeprecated() {
    return status == WorkflowStatus.DEPRECATED;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public WorkflowStatus getStatus() {
    return status;
  }

  public void setStatus(WorkflowStatus status) {
    this.status = status;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getDeprecationDate() {
    return deprecationDate;
  }

  public void setDeprecationDate(Date deprecationDate) {
    this.deprecationDate = deprecationDate;
  }

  public int getDefaultActivityTaskTimeout() {
    return defaultActivityTaskTimeout;
  }

  public void setDefaultActivityTaskTimeout(int defaultActivityTaskTimeout) {
    this.defaultActivityTaskTimeout = defaultActivityTaskTimeout;
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
        .add("version", version)
        .add("description", description)
        .add("status", status)
        .add("creationDate", creationDate)
        .add("deprecationDate", deprecationDate)
        .add("defaultActivityTaskTimeout", defaultActivityTaskTimeout)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WorkflowType that = (WorkflowType) o;

    if (defaultActivityTaskTimeout != that.defaultActivityTaskTimeout) return false;
    if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null)
      return false;
    if (deprecationDate != null ? !deprecationDate.equals(that.deprecationDate) : that.deprecationDate != null)
      return false;
    if (description != null ? !description.equals(that.description) : that.description != null)
      return false;
    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (status != that.status) return false;
    if (version != null ? !version.equals(that.version) : that.version != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (status != null ? status.hashCode() : 0);
    result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
    result = 31 * result + (deprecationDate != null ? deprecationDate.hashCode() : 0);
    result = 31 * result + defaultActivityTaskTimeout;
    return result;
  }
}
