package tconstruct.plugins.te3;

import tconstruct.common.TRepo;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.module.ILoadableModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ThermalExpansion implements ILoadableModule
{

    @SuppressWarnings("unused")
    public static String modId = "ThermalExpansion";

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init()
    {
    ItemStack crystalCinnabar = OreDictionary.getOres("crystalCinnabar").get(0);

    TE3Helper.addPulveriserRecipe(1000, new ItemStack(TRepo.materials, 1, 11), new ItemStack(TRepo.materials, 1, 40), null, 0);
    TE3Helper.addPulveriserRecipe(1000, new ItemStack(TRepo.materials, 1, 3), new ItemStack(TRepo.materials, 1, 39), null, 0);
    TE3Helper.addPulveriserRecipe(1000, new ItemStack(TRepo.materials, 1, 4), new ItemStack(TRepo.materials, 1, 38), null, 0);

    TE3Helper.addPulveriserRecipe(12000, new ItemStack(TRepo.oreSlag, 1, 1), new ItemStack(TRepo.materials, 2, 39), GameRegistry.findItemStack("ThermalExpansion", "dustIron", 1), 10);
    TE3Helper.addInductionSmelterRecipe(12000, new ItemStack(TRepo.oreSlag, 1, 1), crystalCinnabar.copy(), new ItemStack(TRepo.materials, 3, 3), new ItemStack(Items.iron_ingot), 100);
    TE3Helper.addPulveriserRecipe(12000, new ItemStack(TRepo.oreSlag, 1, 2), new ItemStack(TRepo.materials, 2, 38), GameRegistry.findItemStack("ThermalExpansion", "dustGold", 1), 10);
    TE3Helper.addInductionSmelterRecipe(12000, new ItemStack(TRepo.oreSlag, 1, 2), crystalCinnabar.copy(), new ItemStack(TRepo.materials, 3, 4), new ItemStack(Items.gold_ingot), 100);

    TE3Helper.addInductionSmelterRecipe(4000, new ItemStack(TRepo.materials, 1, 4), new ItemStack(TRepo.materials, 1, 3), new ItemStack(TRepo.materials, 1, 5), null, 0);
    TE3Helper.addInductionSmelterRecipe(4000, new ItemStack(TRepo.materials, 1, 9), new ItemStack(TRepo.materials, 3, 11), new ItemStack(TRepo.materials, 4, 14), null, 0);
    }    @Override
    public void postInit() {
        // Nothing
    }

    
}
