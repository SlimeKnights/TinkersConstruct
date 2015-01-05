package tconstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
  public static Logger getLogger(String type) {
    String log = TConstruct.modID;

    return LogManager.getLogger(log + type);
  }

  /**
   * Removes all whitespaces from the given string and makes it lowerspace.
   */
  public static String sanitizeLocalizationString(String string) {
    return string.toLowerCase().replaceAll(" ", "");
  }
}
