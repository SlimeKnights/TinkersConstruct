package slimeknights.tconstruct.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.block.BlockToolTable;

public class TableRecipeMaker {
	public static List createRecipes(IJeiHelpers jeiHelpers) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
		List<ShapedOreRecipe> tableRecipes = new ArrayList<ShapedOreRecipe>();

        {
            List<ItemStack> legs = getOres(stackHelper, "plankWood", "vcraft-plankWood");
            for (ItemStack leg : legs) {
                ItemStack result = BlockToolTable.createItemstack(TinkerTools.toolTables, BlockToolTable.TableTypes.StencilTable.meta, leg);
                ShapedOreRecipe recipe = new ShapedOreRecipe(result, "P", "B", 'P', TinkerTools.pattern, 'B', leg);
                tableRecipes.add(recipe);
            }
        }

        {
            List<ItemStack> legs = getOres(stackHelper, "logWood", "vcraft-logWood");
            for (ItemStack leg : legs) {
                ItemStack result = BlockToolTable.createItemstack(TinkerTools.toolTables, BlockToolTable.TableTypes.PartBuilder.meta, leg);
                ShapedOreRecipe recipe = new ShapedOreRecipe(result, "P", "B", 'P', TinkerTools.pattern, 'B', leg);
                tableRecipes.add(recipe);
            }
        }

        return tableRecipes;
	}

    private static List<ItemStack> getOres(IStackHelper stackHelper, String... oreNames) {
        List<ItemStack> ores = new ArrayList<ItemStack>();
        for (String oreName : oreNames) {
            ores.addAll(stackHelper.getAllSubtypes(OreDictionary.getOres(oreName)));
        }
        return ores;
    }
}
