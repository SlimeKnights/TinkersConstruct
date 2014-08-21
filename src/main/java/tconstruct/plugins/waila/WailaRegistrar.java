package tconstruct.plugins.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.smeltery.blocks.LavaTankBlock;
import tconstruct.smeltery.blocks.SmelteryBlock;
import tconstruct.smeltery.logic.CastingBasinLogic;
import tconstruct.smeltery.logic.CastingChannelLogic;
import tconstruct.smeltery.logic.CastingTableLogic;

public class WailaRegistrar
{
    public static void wailaCallback (IWailaRegistrar registrar)
    {
        TConstruct.logger.info("[Waila-Compat] Got registrar: " + registrar);

        // Configs
        registrar.addConfig("Tinkers' Construct", "tcon.searedtank", "Seared Tank");
        registrar.addConfig("Tinkers' Construct", "tcon.castingchannel", "Casting Channel");
        registrar.addConfig("Tinkers' Construct", "tcon.basin", "Casting Basin");
        registrar.addConfig("Tinkers' Construct", "tcon.table", "Casting Table");
        registrar.addConfig("Tinkers' Construct", "tcon.smeltery", "Smeltery status");

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
