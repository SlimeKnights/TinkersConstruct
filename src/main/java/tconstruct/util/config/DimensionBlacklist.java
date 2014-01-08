package tconstruct.util.config;

import java.util.ArrayList;

public class DimensionBlacklist
{
    public static ArrayList<Integer> blacklist = new ArrayList<Integer>();
    public static ArrayList<Integer> nopool = new ArrayList<Integer>();

    public static void getbaddimensions ()
    {
        blacklist.add(1);
        if (TwilightForestConfig.twilightForestDimensionID != -100)
        {
            blacklist.add(TwilightForestConfig.twilightForestDimensionID);
        }
        if (PHConstruct.cfgDimBlackList.length > 0)
        {
            for (int numdim = 0; numdim < PHConstruct.cfgDimBlackList.length; numdim++)
            {
                blacklist.add(PHConstruct.cfgDimBlackList[numdim]);
            }
        }
        if (BOPConfig.promisedLandDimensionID != -100)
        {
            nopool.add(BOPConfig.promisedLandDimensionID);
        }
    }

    public static boolean isDimInBlacklist (int dim)
    {
        if (dim < 0)
            return false;
        if (dim == 0)
            return PHConstruct.slimeIslGenDim0;
        if (PHConstruct.slimeIslGenDim0Only && dim != 0)
        {
            return false;
        }
        for (int len = 0; len < blacklist.size(); len++)
        {
            if (blacklist.get(len) == dim)
                return false;
            //TConstruct.logger.info("diminblist +" + blacklist.get(len));
        }
        return true;

    }

    public static boolean isDimNoPool (int dim)
    {
        for (int len = 0; len < nopool.size(); len++)
        {
            if (nopool.get(len) == dim)
                //TConstruct.logger.info("DimNoPool "+ nopool.get(len));
                return true;
        }
        return false;

    }
}
