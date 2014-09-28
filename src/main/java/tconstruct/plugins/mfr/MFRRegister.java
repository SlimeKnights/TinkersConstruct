package tconstruct.plugins.mfr;

import powercrystals.minefactoryreloaded.api.*;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.world.TinkerWorld;

public class MFRRegister
{
    public static void registerWithMFR ()
    {
        FactoryRegistry.sendMessage("registerHarvestable", new HarvestableOreBerry(TinkerWorld.oreBerry, TinkerWorld.oreBerries, 0));
        FactoryRegistry.sendMessage("registerHarvestable", new HarvestableOreBerry(TinkerWorld.oreBerrySecond, TinkerWorld.oreBerries, 4));
        FactoryRegistry.sendMessage("registerGrindable", new GrindableHorse());
        FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem(TinkerSmeltery.bloodFluid.getName(), new Drinkables()));
    }
}
