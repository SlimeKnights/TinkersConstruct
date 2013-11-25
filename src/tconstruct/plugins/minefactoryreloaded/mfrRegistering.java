package tconstruct.plugins.minefactoryreloaded;

import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import tconstruct.common.TContent;
import tconstruct.plugins.minefactoryreloaded.drinkable.Drinkables;
import tconstruct.plugins.minefactoryreloaded.harvestables.GrindableHorse;
import tconstruct.plugins.minefactoryreloaded.harvestables.HarvestableOreBerry;

public class mfrRegistering
{
    public static void registerWithMFR ()
    {
        FactoryRegistry.registerHarvestable(new HarvestableOreBerry(TContent.oreBerry.blockID, TContent.oreBerries.itemID, 0));
        FactoryRegistry.registerHarvestable(new HarvestableOreBerry(TContent.oreBerrySecond.blockID, TContent.oreBerries.itemID, 4));
        FactoryRegistry.registerGrindable(new GrindableHorse());
        FactoryRegistry.registerLiquidDrinkHandler(TContent.bloodFluid.getName(), new Drinkables);
    }
}
