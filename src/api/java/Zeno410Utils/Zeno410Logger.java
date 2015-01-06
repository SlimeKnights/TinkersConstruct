package Zeno410Utils;

/*
 * author Zeno410
 * using code by Lars Vogel
 * This class currently serves to turn on and off logging by editing code here rather
 * than everywhere
 */

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Zeno410Logger {
  static private FileHandler fileTxt;
  static private SimpleFormatter formatterTxt;

  public static final boolean suppress  = true;

  public static void crashIfRecording(RuntimeException toThrow) {
      if (suppress) return;
      throw toThrow;
  }

  private Logger logger;

  static public Logger globalLogger() {

    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    logger.setLevel(Level.ALL);
    if (!suppress) {
        try {
            fileTxt = new FileHandler("/Zeno410Logging.txt");
             // Create txt Formatter
            formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (SecurityException ex) {
            ex.printStackTrace(System.err);
        }
    }
    logger.info("Starting");
    return logger;
  }

  public Logger logger() {return logger;}

  public Zeno410Logger(String name) {
       logger = Logger.getLogger(name);
       // if logging is off make the loggers do nothing
       if (suppress) return;
        try {
            fileTxt = new FileHandler("/"+name+".txt");
            formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (SecurityException ex) {
            ex.printStackTrace(System.err);
        }
  }
}