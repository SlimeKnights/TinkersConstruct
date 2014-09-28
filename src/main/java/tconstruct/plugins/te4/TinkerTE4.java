package tconstruct.plugins.te4;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.*;
import tconstruct.TConstruct;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers TE4 Compatibility", description = "Tinkers Construct compatibility for Thermal Expansion", modsRequired = "ThermalExpansion")
public class TinkerTE4
{
    @Handler
    public void init ()
    {
        TConstruct.logger.info("Thermal Expansion detected. Adding Pulverizer & Induction Smelter recipes");
        ItemStack crystalCinnabar = OreDictionary.getOres("crystalCinnabar").get(0);

        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 11), new ItemStack(TinkerTools.materials, 1, 40), null, 0);
        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 3), new ItemStack(TinkerTools.materials, 1, 39), null, 0);
        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 4), new ItemStack(TinkerTools.materials, 1, 38), null, 0);

        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 1), new ItemStack(TinkerTools.materials, 2, 39), GameRegistry.findItemStack("ThermalExpansion", "dustIron", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 1), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, 3), new ItemStack(Items.iron_ingot), 100);
        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 2), new ItemStack(TinkerTools.materials, 2, 38), GameRegistry.findItemStack("ThermalExpansion", "dustGold", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 2), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, 4), new ItemStack(Items.gold_ingot), 100);

        TE4Helper.addSmelterRecipe(4000, new ItemStack(TinkerTools.materials, 1, 4), new ItemStack(TinkerTools.materials, 1, 3), new ItemStack(TinkerTools.materials, 1, 5), null, 0);
        TE4Helper.addSmelterRecipe(4000, new ItemStack(TinkerTools.materials, 1, 9), new ItemStack(TinkerTools.materials, 3, 11), new ItemStack(TinkerTools.materials, 4, 14), null, 0);

        // Dust Recipes
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 1, 41), "dustArdite", "dustCobalt"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 4, 42), "dustAluminum", "dustAluminum", "dustAluminum", "dustCopper"));
    }
}
