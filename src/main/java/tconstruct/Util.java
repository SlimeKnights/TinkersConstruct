package tconstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {

  public static final String RESOURCE = "tinker";

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

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead
   * of hardcoding resource locations.
   */
  public static String resource(String res) {
    return String.format("%s:%s", RESOURCE, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized
   * names for a uniform namespace.
   */
  public static String prefix(String name) {
    return String.format("tconstruct.%s", name);
  }
}
