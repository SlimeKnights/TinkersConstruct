package tconstruct.tools;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.LinkedList;
import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.ITinkerable;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.utils.ToolBuilder;

public class TempToolModifying implements IRecipe {
  static {
    // register the recipe with the recipesorter
    RecipeSorter.register("tcon:mod", TempToolModifying.class, RecipeSorter.Category.SHAPELESS, "");
  }

  private ItemStack outputTool;
  private ItemStack[] stacks = new ItemStack[0];

  @Override
  public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
    return outputTool;
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {
    outputTool = null;

    stacks = new ItemStack[inv.getSizeInventory()];
    ItemStack tool = null;

    for (int i = 0; i < inv.getSizeInventory(); i++) {
      stacks[i] = inv.getStackInSlot(i);
      if(stacks[i] != null && stacks[i].getItem() instanceof TinkersItem) {
        tool = stacks[i];
        stacks[i] = null;
      }
    }

    if(tool == null)
      return false;

    outputTool = ToolBuilder.tryModifyTool(stacks, tool);

    return outputTool != null;
  }

  @Override
  public int getRecipeSize() {
    return 2;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return outputTool;
  }

  @Override
  public ItemStack[] getRemainingItems(InventoryCrafting inv) {
    return stacks;
  }
}
