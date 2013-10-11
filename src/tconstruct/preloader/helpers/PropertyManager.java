package tconstruct.preloader.helpers;

import tconstruct.preloader.TConstructLoaderContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * TCon Preloader properties file helper.
 *
 * We need this as at an early stage as we don't have access to Forge's Configuration object.
 *
 * @author Sunstrike
 */
public class PropertyManager {

    private PropertyManager() {} // Singleton

    public static final String propFileName = "TConPreloader.cfg";

    public static boolean asmInterfaceRepair_verboseLog = false;

    /**
     * Gets or creates the TCorestruct properties file.
     *
     * @throws PropAccessException Thrown if a properties file wasn't found and could not be created. Should be treated as a SEVERE log message.
     * @return True if loaded from disk, false if file created instead.
     */
    public static boolean getOrCreateProps() throws PropAccessException {
        File fp = new File("config/" + propFileName);
        Properties props = new Properties();
        if (fp.exists()) {
            // Attempt read
            try {
                TConstructLoaderContainer.logger.info("Found a properties file. Attempting load...");
                props.load(new FileInputStream(fp));

                asmInterfaceRepair_verboseLog = props.getProperty("asmInterfaceRepair_verboseLog", "false").equalsIgnoreCase("true");

                TConstructLoaderContainer.logger.info("Loaded properties successfully. Using specified settings.");
            } catch (IOException ex) {
                throw new PropAccessException();
            }
        } else {
            // Attempt creation
            try {
                if (fp.createNewFile()) {
                    TConstructLoaderContainer.logger.info("Creating new properties file, as none found...");

                    props.setProperty("asmInterfaceRepair_verboseLog", "false");

                    props.store(new FileOutputStream(fp), null);
                    TConstructLoaderContainer.logger.info("Created properties file; using defaults this run.");
                } else {
                    throw new PropAccessException();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new PropAccessException();
            }
        }

        return false;
    }

    public static class PropAccessException extends Exception {}

}
