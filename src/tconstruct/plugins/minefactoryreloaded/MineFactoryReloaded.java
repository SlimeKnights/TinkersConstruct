package tconstruct.plugins.minefactoryreloaded;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TConstruct|CompatMineFactoryReloaded", name = "TConstruct Compat: MFR", version = "0.2", dependencies = "after:MineFactoryReloaded;required-after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MineFactoryReloaded
{
    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        if (!Loader.isModLoaded("MineFactoryReloaded"))
        {
            FMLLog.warning("MineFactoryReloaded missing - TConstruct Compat: MFR not loading.");
            return;
        }
        try
        {
            FMLLog.fine("MineFactoryReloaded detected. Registering TConstruct farmables/grindables with MFR's Farming Registry.");
            mfrRegistering.registerWithMFR();
            /*
             * Perhaps TC ores should be registered as drops from the MFR Laser Drill here, but I don't know which things would be suitable for that.
             * Syntax: FarmingRegistry.registerLaserOre(int weight, ItemStack droppedStack));
             * Currently used weights are from about 50 (emerald) to 175 (coal). 
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}