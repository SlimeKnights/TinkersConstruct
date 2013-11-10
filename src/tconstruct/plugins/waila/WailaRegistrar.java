package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.LavaTankBlock;
import tconstruct.blocks.SmelteryBlock;
import tconstruct.blocks.logic.CastingChannelLogic;

public class WailaRegistrar
{
    public static void wailaCallback (IWailaRegistrar registrar)
    {
        Waila.logger.info("Got registrar: " + registrar);

        // Tanks
        registrar.registerBodyProvider(new SearedTankDataProvider(), LavaTankBlock.class);
        registrar.registerBodyProvider(new CastingChannelDataProvider(), CastingChannelLogic.class);
        //registrar.registerBodyProvider(new EssenceExtractorDataProvider(), EssenceExtractor.class);

        // Smeltery
        registrar.registerBodyProvider(new SmelteryDataProvider(), SmelteryBlock.class);
    }

    public static String fluidNameHelper (FluidStack f)
    {
        return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
    }

}
