package tconstruct.library.crafting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;

public class FluidType
{

    public final int renderBlockID;
    public final int renderMeta;
    public final int baseTemperature;
    public final Fluid fluid;
    public final boolean isToolpart;
    
    public static HashMap<String, FluidType> fluidTypes = new HashMap<String, FluidType>();

    public FluidType(int blockID, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        this.renderBlockID = blockID;
        this.renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
        this.isToolpart = isToolpart;
    }
    
    public static void registerFluidType(String name, FluidType type)
    {
        fluidTypes.put(name, type);
    }
    
    public static void registerFluidType(String name, int blockID, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        FluidType type = new FluidType(blockID, meta, baseTemperature, fluid, isToolpart);
        registerFluidType(name, type);
    }
    
    public static FluidType getFluidType(String typeName)
    {
        return fluidTypes.get(typeName);
    }

    public static FluidType getFluidType (Fluid searchedFluid)
    {
        Iterator iter = fluidTypes.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            if (((FluidType)pairs.getValue()).fluid.equals(searchedFluid))
                return (FluidType) pairs.getValue();
        }
        return null;
    }

    public static int getTemperatureByFluid (Fluid searchedFluid)
    {
        Iterator iter = fluidTypes.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            if (((FluidType)pairs.getValue()).fluid.equals(searchedFluid))
                return ((FluidType) pairs.getValue()).baseTemperature;
        }
        return 800;
    }
}