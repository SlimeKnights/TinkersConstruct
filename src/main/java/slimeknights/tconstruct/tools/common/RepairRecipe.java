package slimeknights.tconstruct.tools.common;

import com.google.common.collect.ImmutableSet;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.tools.TinkerTools;

public class RepairRecipe implements IRecipe {

  private static final Set<Item> repairItems = ImmutableSet.of(TinkerTools.sharpeningKit);

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    return getRepairedTool(inv, true) != null;
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return getRepairedTool(inv, true);
  }

  @Nonnull
  private ItemStack getRepairedTool(@Nonnull InventoryCrafting inv, boolean simulate) {

    ItemStack tool = null;
    NonNullList<ItemStack> input = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack slot = inv.getStackInSlot(i);
      // empty slot
      if(slot.isEmpty()) {
        continue;
      }

      slot = slot.copy();
      slot.setCount(1);

      Item item = slot.getItem();

      // is it the tool?
      if(item instanceof TinkersItem) {
        // stop if we already have a tool, 2 tools present
        if(tool != null) {
          return ItemStack.EMPTY;
        }
        tool = slot;
      }
      // otherwise.. input material
      else if(repairItems.contains(item)) {
        input.set(i, slot);
      }
      // invalid item
      else {
        return ItemStack.EMPTY;
      }
    }
    // no tool found?
    if(tool == null) {
      return ItemStack.EMPTY;
    }

    if(simulate) {
      input = Util.copyItemStackArray(input);
    }

    // do the repairing, also checks for valid input
    return ((TinkersItem) tool.getItem()).repair(tool.copy(), input);
  }

  @Override
  public int getRecipeSize() {
    return 9;
  }

  @Nonnull
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    /*
    getRepairedTool(inv, false);
    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);
      if(itemstack != null && (itemstack.stackSize == 0 || itemstack.getItem() instanceof TinkersItem)) {
        inv.setInventorySlotContents(i, null);
      }
    }

    return new ItemStack[0];*/
  }
}
