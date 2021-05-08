package slimeknights.tconstruct.tables.tileentity.table.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;

/**
 * Extension of {@link CraftingInventory} to use instead wrap an existing {@link IInventory}
 */
public class CraftingInventoryWrapper extends CraftingInventory {
  private final IInventory crafter;
  public CraftingInventoryWrapper(IInventory crafter, int width, int height) {
    //noinspection ConstantConditions
    super(null, width, height);
    Preconditions.checkArgument(crafter.getSizeInventory() == width * height, "Invalid width and height for inventroy size");
    this.crafter = crafter;
  }

  /** Inventory redirection */

  @Override
  public ItemStack getStackInSlot(int index) {
    return crafter.getStackInSlot(index);
  }

  @Override
  public int getSizeInventory() {
    return crafter.getSizeInventory();
  }

  @Override
  public boolean isEmpty() {
    return crafter.isEmpty();
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return crafter.removeStackFromSlot(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    return crafter.decrStackSize(index, count);
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    crafter.setInventorySlotContents(index, stack);
  }

  @Override
  public void markDirty() {
    crafter.markDirty();
  }

  @Override
  public void clear() {
    crafter.clear();
  }

  @Override
  public void fillStackedContents(RecipeItemHelper helper) {
    for (int i = 0; i < crafter.getSizeInventory(); i++) {
      helper.accountPlainStack(crafter.getStackInSlot(i));
    }
  }
}
