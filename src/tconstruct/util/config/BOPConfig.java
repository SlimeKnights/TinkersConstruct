package tconstruct.util.config;

import java.io.File;
import java.io.IOException;
import net.minecraftforge.common.Configuration;
import tconstruct.TConstruct;

public class BOPConfig
{
    public static void initProps (File location)
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */

        File newFile = new File(location + "/biomesoplenty/ids.cfg");
        if (newFile.exists())
        {

            /* [Forge] Configuration class, used as config method */
            Configuration config = new Configuration(newFile);

            /* Load the configuration file */
            config.load();

            promisedLandDimensionID = config.get("dimension settings", "Promised Land Dimension ID", -200).getInt();
            TConstruct.logger.info("PL Dim ID: " + promisedLandDimensionID);
        }
        else
        {
            promisedLandDimensionID = -100;
        }
    }

    public static int promisedLandDimensionID;
}
