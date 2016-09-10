package eu.toolchain.scribe;

import lombok.Getter;

public class ContextException extends RuntimeException {
  private static final long serialVersionUID = 6991019600165017678L;

  @Getter
  private final Context path;
  @Getter
  private final String originalMessage;

  public ContextException(final Context path, final Throwable cause) {
    super(path.path() + ": " + cause.getMessage());
    this.path = path;
    this.originalMessage = cause.getMessage();
  }

  public ContextException(final Context path, final String message) {
    super(path.path() + ": " + message);
    this.path = path;
    this.originalMessage = message;
  }

  public ContextException(final Context path, final String message, final Throwable cause) {
    super(path.path() + ": " + message, cause);
    this.path = path;
    this.originalMessage = message;
  }
}
