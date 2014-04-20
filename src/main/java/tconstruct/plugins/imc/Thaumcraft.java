package tconstruct.plugins.imc;

import mantle.module.ILoadableModule;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.plugins.ICompatPlugin;
import cpw.mods.fml.common.event.FMLInterModComms;

public class Thaumcraft implements ILoadableModule
{

    @SuppressWarnings("unused")
    public static String modId = "Thaumcraft";

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[Thaumcraft] Registering harvestables.");
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TRepo.oreBerrySecond, 1, 13));
    }

    @Override
    public void postInit ()
    {

    }

}
