package tconstruct.library.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {

  private static final Logger logger = LogManager.getLogger("TCon");

  public static void error(String text, Object... params) {
    logger.error(text, params);
  }

  public static void info(String text, Object... params) {
    logger.info(text, params);
  }

  public static void warn(String text, Object... params) {
    logger.warn(text, params);
  }

  public static void debug(String text, Object... params) {
    logger.debug(text, params);
  }
}
