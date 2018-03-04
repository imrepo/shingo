package net.riverrouge.shingo.server.model;

/**
 * The types of events that we record in the event history
 */
public enum EventType {

  DECISION_STARTED,
  DECISION_COMPLETED,
  DECISION_FAILED,
  DECISION_SCHEDULED,

  WORKFLOW_STARTED,
  WORKFLOW_CANCELED,
  WORKFLOW_FAILED,
  WORKFLOW_COMPLETED,

  TASK_STARTED,
  TASK_COMPLETED,
  TASK_FAILED,
  TASK_SCHEDULED,
  TASK_TIMED_OUT
}