package tconstruct.plugins.minefactoryreloaded;

import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import tconstruct.common.TRepo;
import tconstruct.plugins.minefactoryreloaded.drinkable.Drinkables;
import tconstruct.plugins.minefactoryreloaded.harvestables.GrindableHorse;
import tconstruct.plugins.minefactoryreloaded.harvestables.HarvestableOreBerry;

public class mfrRegistering
{
    public static void registerWithMFR ()
    {
        FactoryRegistry.registerHarvestable(new HarvestableOreBerry(TRepo.oreBerry.blockID, TRepo.oreBerries.itemID, 0));
        FactoryRegistry.registerHarvestable(new HarvestableOreBerry(TRepo.oreBerrySecond.blockID, TRepo.oreBerries.itemID, 4));
        FactoryRegistry.registerGrindable(new GrindableHorse());
        FactoryRegistry.registerLiquidDrinkHandler(TRepo.bloodFluid.getName(), new Drinkables());
    }
}
