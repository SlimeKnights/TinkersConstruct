package tconstruct.util.config;

import java.io.File;
import java.io.IOException;
import net.minecraftforge.common.Configuration;
import tconstruct.TConstruct;

public class TwilightForestConfig
{
    public static void initProps (File location)
    {
        File newFile = new File(location + "/TwilightForest.cfg");
        if (newFile.exists())
        {
            Configuration config = new Configuration(newFile);

            config.load();

            twilightForestDimensionID = config.get("dimension", "dimensionID", -100).getInt();
            TConstruct.logger.info("TF Dim ID: " + twilightForestDimensionID);
        }
        else
        {
            twilightForestDimensionID = -100;
        }
    }

    public static int twilightForestDimensionID;
}
