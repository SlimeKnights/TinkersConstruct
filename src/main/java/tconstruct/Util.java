package tconstruct;

import org.apache.logging.log4j.Logger;

public class Util {
    public static Logger logger;

    /**
     * Removes all whitespaces from the given string and makes it lowerspace.
     */
    public static String sanitizeLocalizationString(String string) {
        return string.toLowerCase().replaceAll(" ", "");
    }
}
