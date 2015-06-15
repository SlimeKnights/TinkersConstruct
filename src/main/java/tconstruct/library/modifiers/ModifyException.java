package tconstruct.library.modifiers;

/**
 * Thrown when a modifier cannot be applied. Contains the reason why.
 */
public class ModifyException extends Exception {

  public ModifyException() {
  }

  public ModifyException(String message) {
    super(message);
  }

  public ModifyException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModifyException(Throwable cause) {
    super(cause);
  }

  public ModifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
