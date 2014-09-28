package tconstruct.plugins.mfr;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import tconstruct.TConstruct;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers MFR Compatibility", description = "Tinkers Construct compatibility for MineFactory Reloaded", modsRequired = "MineFactoryReloaded")
public class TinkerMFR
{
    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("MineFactoryReloaded detected. Registering TConstruct farmables/grindables with MFR's Farming Registry.");
        MFRRegister.registerWithMFR();
        /*
         * Perhaps TC ores should be registered as drops from the MFR Laser Drill here, but I don't know which things would be suitable for that.
         * Syntax: FarmingRegistry.registerLaserOre(int weight, ItemStack droppedStack));
         * Currently used weights are from about 50 (emerald) to 175 (coal).
         */
    }
}