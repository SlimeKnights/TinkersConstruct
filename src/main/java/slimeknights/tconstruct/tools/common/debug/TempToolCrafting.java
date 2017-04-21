package slimeknights.tconstruct.tools.common.debug;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;

public class TempToolCrafting implements IRecipe {

  static {
    // register the recipe with the recipesorter
    RecipeSorter.register("tcon:tool", TempToolCrafting.class, RecipeSorter.Category.SHAPELESS, "");
  }

  private ItemStack outputTool;

  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting p_77572_1_) {
    return outputTool;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    outputTool = ItemStack.EMPTY;


    NonNullList<ItemStack> input = NonNullList.create();

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack slot = inv.getStackInSlot(i);
      // empty slot
      if(slot.isEmpty()) {
        continue;
      }

      // save it
      input.add(slot);
    }

    NonNullList<ItemStack> inputs = Util.copyItemStackArray(input);
    for(ToolCore tool : TinkerRegistry.getTools()) {
      outputTool = tool.buildItemFromStacks(inputs);
      if(outputTool != null) {
        break;
      }
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
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NonNullList.create();
  }
}
