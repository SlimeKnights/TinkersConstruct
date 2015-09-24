package slimeknights.tconstruct.library;

public class TinkerAPIException extends RuntimeException {

  public TinkerAPIException() {
  }

  public TinkerAPIException(String message) {
    super("[TCon API] " + message);
  }

  public TinkerAPIException(String message, Throwable cause) {
    super("[TCon API] " + message, cause);
  }

  public TinkerAPIException(Throwable cause) {
    super(cause);
  }

  public TinkerAPIException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
    super("[TCon API] " + message, cause, enableSuppression, writableStackTrace);
  }
}
