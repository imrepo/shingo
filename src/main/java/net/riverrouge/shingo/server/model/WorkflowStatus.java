package net.riverrouge.shingo.server.model;

/**
 * Status codes of a WorkflowType. The workflowType accepts new executions when the status is
 * REGISTERED and stops accepting new executions when it is deprecated.
 */
public enum WorkflowStatus {
  REGISTERED,
  DEPRECATED
}
