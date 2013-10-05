package tconstruct.plugins.waila;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.LavaTankBlock;
import tconstruct.blocks.SmelteryBlock;
import tconstruct.blocks.logic.CastingChannelLogic;

import java.util.logging.Logger;

@Mod(modid = "TConstruct|CompatWaila", name = "TConstruct Compat: Waila", version = "0.0.1", dependencies = "after:Waila;required-after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Waila {

    public static Logger logger = Logger.getLogger("TConstruct Waila");

    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());

        if (!Loader.isModLoaded("Waila")) {
            logger.warning("Waila missing - TConstruct Compat: Waila not loading.");
            return;
        } try {
            logger.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");

        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.Waila.wailaCallback");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void wailaCallback(IWailaRegistrar registrar) {
        logger.info("Got registrar: " + registrar);

        // Tanks
        registrar.registerBodyProvider(new SearedTankDataProvider(), LavaTankBlock.class);
        registrar.registerBodyProvider(new CastingChannelDataProvider(), CastingChannelLogic.class);
        //registrar.registerBodyProvider(new EssenceExtractorDataProvider(), EssenceExtractor.class);

        // Smeltery
        registrar.registerBodyProvider(new SmelteryDataProvider(), SmelteryBlock.class);
    }

    public static String fluidNameHelper(FluidStack f) {
        return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
    }

}
