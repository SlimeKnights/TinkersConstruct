package tconstruct.util;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import tconstruct.library.util.CoordTuple;

public class EnderWarpCoords
{
    public static HashMap<Integer, EnderWarpDimensionCoords> dimensions = new HashMap<Integer, EnderWarpDimensionCoords>();

    public static void registerEnderporter (TileEntity te, int frequency)
    {
        EnderWarpDimensionCoords ed = getEnderporter(te);
        ed.registerCoord(new CoordTuple(te.xCoord, te.yCoord, te.zCoord), frequency);
    }

    public static void unregisterEnderporter (TileEntity te, int frequency)
    {
        EnderWarpDimensionCoords ed = getEnderporter(te);
        ed.removeCoord(new CoordTuple(te.xCoord, te.yCoord, te.zCoord), frequency);
    }

    public static void changeFrequency (TileEntity te, int newFrequency, int prevFrequency)
    {
        EnderWarpDimensionCoords ed = getEnderporter(te);
        ed.changeFrequency(new CoordTuple(te.xCoord, te.yCoord, te.zCoord), newFrequency, prevFrequency);
    }
    
    public static CoordTuple getClosestPortal(TileEntity te, int frequency, int rangeLimit)
    {
        EnderWarpDimensionCoords ed = getEnderporter(te);
        return ed.getClosestPortal(new CoordTuple(te.xCoord, te.yCoord, te.zCoord), frequency, rangeLimit );
    }

    private static EnderWarpDimensionCoords getEnderporter (TileEntity te)
    {
        int dimension = te.worldObj.provider.dimensionId;
        EnderWarpDimensionCoords ed = dimensions.get(dimension);
        if (ed == null)
        {
            ed = new EnderWarpDimensionCoords();
            dimensions.put(dimension, ed);
        }
        return ed;
    }
}
