package tconstruct.plugins.minefactoryreloaded;

import tconstruct.TConstruct;
import tconstruct.plugins.ICompatPlugin;

public class MineFactoryReloaded implements ICompatPlugin
{
    @Override
    public String getModId() {
        return "MineFactoryReloaded";
    }

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init()
    {
        TConstruct.logger.info("MineFactoryReloaded detected. Registering TConstruct farmables/grindables with MFR's Farming Registry.");
        MFRRegistering.registerWithMFR();
        /*
         * Perhaps TC ores should be registered as drops from the MFR Laser Drill here, but I don't know which things would be suitable for that.
         * Syntax: FarmingRegistry.registerLaserOre(int weight, ItemStack droppedStack));
         * Currently used weights are from about 50 (emerald) to 175 (coal).
         */
    }

    @Override
    public void postInit() {
        // Nothing
    }

}