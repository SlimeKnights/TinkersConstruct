package slimeknights.tconstruct.library.modifiers;

/**
 * Thrown to convey information to the user.
 */
public class TinkerGuiException extends Exception {

  public TinkerGuiException() {
  }

  public TinkerGuiException(String message) {
    super(message);
  }

  public TinkerGuiException(String message, Throwable cause) {
    super(message, cause);
  }

  public TinkerGuiException(Throwable cause) {
    super(cause);
  }

  public TinkerGuiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
