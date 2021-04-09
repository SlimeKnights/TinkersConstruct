package slimeknights.tconstruct.library.recipe.alloying;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;

/**
 * Inventory interface for the sake of alloying
 */
public interface IAlloyTank extends IEmptyInventory {
  /**
   * Gets the current temperature of this alloy tank
   * @return
   */
  int getTemperature();

  /**
   * Gets the number of tanks in this alloy tank. Note any tank may be empty
   * @return number of tanks in the alloy tank
   */
  int getTanks();

  /**
   * Gets the fluid in teh given tank
   * @param tank  Tank number
   * @return  Fluid in tank, empty if the tank is empty or the index is invalid
   */
  FluidVolume getFluidInTank(int tank);

  /**
   * Checks if the given recipe can fit
   * @param  fluid    Fluid to add
   * @param  removed  How much fluid this recipe will consume
   * @return true if the recipe will fit
   */
  boolean canFit(FluidVolume fluid, int removed);
}
