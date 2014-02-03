package tconstruct.preloader.helpers;

import tconstruct.preloader.TConstructLoaderContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * TCon Preloader properties file helper.
 *
 * We need this as at an early stage as we don't have access to Forge's Configuration object.
 *
 * @author Sunstrike
 */
public class PropertyManager
{

    private PropertyManager()
    {
    } // Singleton

    public static final String propFileName = "TConPreloader.cfg";

    private static String[] vars = new String[]{"preloaderContainer_verboseLog", "asmInterfaceRepair_verboseLog"};
    public static boolean preloaderContainer_verboseLog = false;
    public static boolean asmInterfaceRepair_verboseLog = false;

    /**
     * Gets or creates the TCorestruct properties file.
     *
     * @throws PropAccessException Thrown if a properties file wasn't found and could not be created. Should be treated as a SEVERE log message.
     * @return True if loaded from disk, false if file created instead.
     */
    public static boolean getOrCreateProps () throws PropAccessException
    {
        File fp = new File("config/" + propFileName);
        Properties props = new Properties();
        if (fp.exists())
        {
            // Attempt read
            try
            {
                TConstructLoaderContainer.logger.info("Found a properties file. Attempting load...");
                props.load(new FileInputStream(fp));

                for (String var : vars)
                {
                    if (!props.containsKey(var))
                        throw new NullPointerException();
                }

                preloaderContainer_verboseLog = props.getProperty("preloaderContainer_verboseLog", "false").equalsIgnoreCase("true");
                asmInterfaceRepair_verboseLog = props.getProperty("asmInterfaceRepair_verboseLog", "false").equalsIgnoreCase("true");

                TConstructLoaderContainer.logger.info("Loaded properties successfully. Using specified settings.");
            }
            catch (IOException ex)
            {
                throw new PropAccessException();
            }
            catch (NullPointerException ex) {
                TConstructLoaderContainer.logger.warn("Preloader config structure has changed; attempting to recreate.");
                attemptCreate(fp, props);
            }
        }
        else
        {
            TConstructLoaderContainer.logger.info("Preloader config not found. Attempting to make a new one.");
            attemptCreate(fp, props);
        }

        return false;
    }

    private static void attemptCreate(File fp, Properties props) throws PropAccessException {
        // Attempt (re)creation
        try
        {
            if (fp.exists())
                fp.delete();
            
            if (fp.createNewFile())
            {
                TConstructLoaderContainer.logger.info("Creating new properties file, as none found...");

                props.setProperty("asmInterfaceRepair_verboseLog", "false");
                props.setProperty("preloaderContainer_verboseLog", "false");

                props.store(new FileOutputStream(fp), null);
                TConstructLoaderContainer.logger.info("Created properties file; using defaults this run.");
            }
            else
            {
                throw new PropAccessException();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            throw new PropAccessException();
        }
    }

    public static class PropAccessException extends Exception
    {
    }

}
