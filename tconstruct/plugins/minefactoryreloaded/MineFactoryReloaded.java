package tconstruct.plugins.minefactoryreloaded;

import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.FarmingRegistry;
import tconstruct.common.TContent;
import tconstruct.entity.BlueSlime;
import tconstruct.plugins.minefactoryreloaded.grindables.GrindableStandard;
import tconstruct.plugins.minefactoryreloaded.harvestables.HarvestableOreBerry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TConstruct|CompatMineFactoryReloaded", name = "TConstruct Compat: MFR", version = "0.1", dependencies = "after:MineFactoryReloaded;after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MineFactoryReloaded
{
    @Init
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

            FarmingRegistry.registerHarvestable(new HarvestableOreBerry(TContent.oreBerry.blockID, TContent.oreBerries.itemID, 0));
            FarmingRegistry.registerHarvestable(new HarvestableOreBerry(TContent.oreBerrySecond.blockID, TContent.oreBerries.itemID, 4));

            // A note: in 1.6, this method of registering grindables will no longer exist, once MFR moves to a more universal way of gathering drops.
            FarmingRegistry.registerGrindable(new GrindableStandard(BlueSlime.class, new ItemStack(TContent.strangeFood)));
            //FarmingRegistry.registerGrindable(new GrindableStandard(Crystal.class, new ItemStack(Item.gunpowder)));
            //FarmingRegistry.registerGrindable(new GrindableStandard(MetalSlime.class, new ItemStack(TContent.strangeFood)));
            //FarmingRegistry.registerGrindable(new GrindableStandard(NitroCreeper.class, new ItemStack(Item.gunpowder)));
            //FarmingRegistry.registerGrindable(new GrindableStandard(Skyla.class, new ItemStack(Item.gunpowder)));

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