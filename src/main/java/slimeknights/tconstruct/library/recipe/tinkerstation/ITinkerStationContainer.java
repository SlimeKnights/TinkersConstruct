package slimeknights.tconstruct.library.recipe.tinkerstation;

import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import javax.annotation.Nullable;

/**
 * Inventory interface for all Tinker Station containers to use
 */
public interface ITinkerStationContainer extends ITinkerableContainer {
  /**
   * Gets material recipe for the given slot
   * @return material recipe for the given slot, null if the slot has no material recipe
   */
  @Nullable
  MaterialRecipe getInputMaterial(int index);
}
