package net.riverrouge.shingo.server.api;

/**
 *
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
}
