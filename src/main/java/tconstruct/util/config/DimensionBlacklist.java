package tconstruct.util.config;

import cpw.mods.fml.common.Loader;
import java.io.File;
import java.util.ArrayList;
import net.minecraftforge.common.config.Configuration;
import tconstruct.TConstruct;

public class DimensionBlacklist
{
    public static ArrayList<Integer> blacklistedDims = new ArrayList<Integer>();
    public static ArrayList<Integer> noPoolDims = new ArrayList<Integer>();

    public static int promisedLandDimensionID = -100;
    public static int twilightForestDimensionID = -100;

    public static void getBadBimensions ()
    {
        updateModDimIDs();

        blacklistedDims.add(1);
        if (twilightForestDimensionID != -100)
        {
            blacklistedDims.add(twilightForestDimensionID);
        }
        if (PHConstruct.cfgDimBlackList.length > 0)
        {
            for (int numdim = 0; numdim < PHConstruct.cfgDimBlackList.length; numdim++)
            {
                blacklistedDims.add(PHConstruct.cfgDimBlackList[numdim]);
            }
        }
        if (promisedLandDimensionID != -100)
        {
            noPoolDims.add(promisedLandDimensionID);
        }
    }

    public static boolean isDimInBlacklist (int dim)
    {
        if (dim < 0)
            return false;
        if (dim == 0)
            return PHConstruct.slimeIslGenDim0;
        if (PHConstruct.slimeIslGenDim0Only)
        {
            return false;
        }
        for (int len = 0; len < blacklistedDims.size(); len++)
        {
            if (blacklistedDims.get(len) == dim)
                return false;
        }
        return true;

    }

    public static boolean isDimNoPool (int dim)
    {
        return noPoolDims.contains(dim);
    }

    private static void updateModDimIDs ()
    {
        updateTwiForestID();
        updateBoPID();
    }

    private static void updateTwiForestID ()
    {
        String location = Loader.instance().getConfigDir().getPath();
        File newFile = new File(location + File.separator + "TwilightForest.cfg");
        if (newFile.exists())
        {
            Configuration config = new Configuration(newFile);

            config.load();

            twilightForestDimensionID = config.get("dimension", "dimensionID", -100).getInt();
            TConstruct.logger.trace("Twilight Forest Dim ID: " + twilightForestDimensionID);
        }
        else
            twilightForestDimensionID = -100;
    }

    private static void updateBoPID ()
    {
        String location = Loader.instance().getConfigDir().getPath();
        File newFile = new File(location + File.separator + "biomesoplenty" + File.separator + "ids.cfg");
        if (newFile.exists())
        {
            Configuration config = new Configuration(newFile);

            config.load();

            promisedLandDimensionID = config.get("dimension settings", "Promised Land Dimension ID", -200).getInt();
            TConstruct.logger.trace("Promised Lands Dim ID: " + promisedLandDimensionID);
        }
        else
            promisedLandDimensionID = -100;
    }

}
