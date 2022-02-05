package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.container.IEmptyContainer;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import javax.annotation.Nullable;

/**
 * Inventory interface for all Tinker Station containers to use
 */
public interface ITinkerStationContainer extends IEmptyContainer {
  /**
   * Gets the stack in the tinkerable slot.
   *
   * @return the itemstack in the tinkerable slot (slot 5/center slot)
   */
  ItemStack getTinkerableStack();

  /**
   * Gets the stack in the given input slot
   * @param index  Slot index
   * @return  Stack
   */
  ItemStack getInput(int index);

  /**
   * Gets the number of input slots
   * @return  Input slot count
   */
  int getInputCount();

  /**
   * Gets material recipe for the given slot
   * @return material recipe for the given slot, null if the slot has no material recipe
   */
  @Nullable
  MaterialRecipe getInputMaterial(int index);
}
