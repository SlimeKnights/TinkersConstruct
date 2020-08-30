package slimeknights.tconstruct.tables.tileentity.table.tinkerstation;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;

public interface ITinkerStationInventory extends IEmptyInventory {

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
}
