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
    Preconditions.checkArgument(crafter.getContainerSize() == width * height, "Invalid width and height for inventroy size");
    this.crafter = crafter;
  }

  /** Inventory redirection */

  @Override
  public ItemStack getItem(int index) {
    return crafter.getItem(index);
  }

  @Override
  public int getContainerSize() {
    return crafter.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return crafter.isEmpty();
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    return crafter.removeItemNoUpdate(index);
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    return crafter.removeItem(index, count);
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    crafter.setItem(index, stack);
  }

  @Override
  public void setChanged() {
    crafter.setChanged();
  }

  @Override
  public void clearContent() {
    crafter.clearContent();
  }

  @Override
  public void fillStackedContents(RecipeItemHelper helper) {
    for (int i = 0; i < crafter.getContainerSize(); i++) {
      helper.accountSimpleStack(crafter.getItem(i));
    }
  }
}
