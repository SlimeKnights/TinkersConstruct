package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.smeltery.blocks.*;
import tconstruct.smeltery.logic.*;

public class WailaRegistrar
{
    public static void wailaCallback (IWailaRegistrar registrar)
    {
        TConstruct.logger.info("[Waila-Compat] Got registrar: " + registrar);

        // Configs
        registrar.addConfig("Tinkers' Construct", "tcon.searedtank");
        registrar.addConfig("Tinkers' Construct", "tcon.castingchannel");
        registrar.addConfig("Tinkers' Construct", "tcon.basin");
        registrar.addConfig("Tinkers' Construct", "tcon.table");
        registrar.addConfig("Tinkers' Construct", "tcon.smeltery");

        // Tanks
        registrar.registerBodyProvider(new SearedTankDataProvider(), LavaTankBlock.class);
        registrar.registerBodyProvider(new CastingChannelDataProvider(), CastingChannelLogic.class);
        //registrar.registerBodyProvider(new EssenceExtractorDataProvider(), EssenceExtractor.class);

        // Casting systems
        registrar.registerBodyProvider(new BasinDataProvider(), CastingBasinLogic.class);
        registrar.registerBodyProvider(new TableDataProvider(), CastingTableLogic.class);

        // Smeltery
        registrar.registerBodyProvider(new SmelteryDataProvider(), SmelteryBlock.class);
    }

    public static String fluidNameHelper (FluidStack f)
    {
        return f.getFluid().getLocalizedName();
    }

}
