package slimeknights.tconstruct.tools.common.debug;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class TempToolModifying implements IRecipe {

  static {
    // register the recipe with the recipesorter
    RecipeSorter.register("tcon:mod", TempToolModifying.class, RecipeSorter.Category.SHAPELESS, "");
  }

  private ItemStack outputTool;

  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting p_77572_1_) {
    return outputTool;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    outputTool = null;

    ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];
    ItemStack tool = null;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks[i] = inv.getStackInSlot(i);
      if(stacks[i] != null && stacks[i].getItem() instanceof TinkersItem) {
        tool = stacks[i];
        stacks[i] = null;
      }
    }

    if(tool == null) {
      return false;
    }

    try {
      outputTool = ToolBuilder.tryModifyTool(stacks, tool, false);
    } catch(TinkerGuiException e) {
      System.out.println(e.getMessage());
    }

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

  @Nonnull
  @Override
  public ItemStack[] getRemainingItems(@Nonnull InventoryCrafting inv) {
    ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];
    ItemStack tool = null;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks[i] = inv.getStackInSlot(i);
      if(stacks[i] != null && stacks[i].getItem() instanceof TinkersItem) {
        tool = stacks[i];
        stacks[i] = null;
      }
    }

    try {
      ToolBuilder.tryModifyTool(stacks, tool, true);
    } catch(TinkerGuiException e) {
      e.printStackTrace();
    }

    return stacks;
  }
}
