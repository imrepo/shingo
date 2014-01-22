package net.riverrouge.shingo.server.api;

/**
 * Represents an individual error message to be returned to the client. When possible the error
 * code and message string should be consistent with HTTP error codes.
 */

public class ErrorMessage {
  private String code;
  private String message;
  private String description;

  public ErrorMessage(String code, String message, String description) {
    this.code = code;
    this.message = message;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static ErrorMessage forbidden() {
    return new ErrorMessage(
        "403",
        "Forbidden",
        "The client requested a resource or operation for which it was not authorized.");
  }
}
