package slimeknights.tconstruct.tables.tileentity.table.tinkerstation;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.recipe.inventory.IReadOnlyInventory;

public interface ITinkerStationInventory extends IReadOnlyInventory {

  /**
   * Gets the stacks in the input slots
   * (the outer slots for the repair/modify screen and the tool parts for the others)
   *
   * @return the stacks in slots (0-4)
   */
  NonNullList<ItemStack> getAllInputStacks();

  /**
   * Gets the stack in the tinkerable slot.
   *
   * @return the itemstack in the tinkerable slot (slot 5/center slot)
   */
  ItemStack getTinkerableStack();

  /** @deprecated
   * Do not use, instead use getTinkerableSlot OR getAllInputStacks
   */
  @Override
  @Deprecated
  default ItemStack getStackInSlot(int index) {
    return ItemStack.EMPTY;
  }
}
