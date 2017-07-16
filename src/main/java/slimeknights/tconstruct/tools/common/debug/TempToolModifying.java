package slimeknights.tconstruct.tools.common.debug;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class TempToolModifying extends Impl<IRecipe> implements IRecipe {

  public TempToolModifying() {
    this.setRegistryName(Util.getResource("mod"));
  }

  private ItemStack outputTool;

  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting p_77572_1_) {
    return outputTool;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    outputTool = null;

    NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    ItemStack tool = ItemStack.EMPTY;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks.set(i, inv.getStackInSlot(i));
      if(!stacks.get(i).isEmpty() && stacks.get(i).getItem() instanceof TinkersItem) {
        tool = stacks.get(i);
        stacks.set(i, ItemStack.EMPTY);
      }
    }

    if(tool.isEmpty()) {
      return false;
    }

    try {
      outputTool = ToolBuilder.tryModifyTool(stacks, tool, false);
    }
    catch(TinkerGuiException e) {
      System.out.println(e.getMessage());
    }

    return outputTool != null;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return outputTool;
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    ItemStack tool = null;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks.set(i, inv.getStackInSlot(i));
      if(!stacks.get(i).isEmpty() && stacks.get(i).getItem() instanceof TinkersItem) {
        tool = stacks.get(i);
        stacks.set(i, ItemStack.EMPTY);
      }
    }

    try {
      ToolBuilder.tryModifyTool(stacks, tool, true);
    }
    catch(TinkerGuiException e) {
      e.printStackTrace();
    }

    return stacks;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }
}
