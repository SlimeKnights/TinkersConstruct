package slimeknights.tconstruct.library.exception;

import slimeknights.tconstruct.library.utils.Util;

// todo: make private, use static factory methods and move this to exception package
// TODO 1.19: reevaluate whether this is actually needed
public class TinkerAPIException extends RuntimeException {
  protected TinkerAPIException(String message) {
    super("[TCon API] " + message);
  }

  protected TinkerAPIException(String message, Throwable cause) {
    super("[TCon API] " + message, cause);
  }

  protected TinkerAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super("[TCon API] " + message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public String getMessage() {
    String modPrefix = Util.getCurrentlyActiveExternalMod().map(id -> "Caused by " + id + ": ").orElse("");
    return modPrefix + super.getMessage();
  }
}
