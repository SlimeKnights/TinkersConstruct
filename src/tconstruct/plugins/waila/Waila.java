package tconstruct.plugins.waila;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;


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
            
        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
