package slimeknights.tconstruct.tables.tileentity.table.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.StackedContents;

/**
 * Extension of {@link CraftingInventory} to use instead wrap an existing {@link IInventory}
 */
public class CraftingInventoryWrapper extends CraftingContainer {
  private final Container crafter;
  public CraftingInventoryWrapper(Container crafter, int width, int height) {
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
  public void fillStackedContents(StackedContents helper) {
    for (int i = 0; i < crafter.getContainerSize(); i++) {
      helper.accountSimpleStack(crafter.getItem(i));
    }
  }
}
