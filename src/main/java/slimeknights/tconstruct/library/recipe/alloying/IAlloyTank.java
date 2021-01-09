package slimeknights.tconstruct.library.recipe.alloying;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;

/**
 * Inventory interface for the sake of alloying
 */
public interface IAlloyTank extends IEmptyInventory {
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
  FluidStack getFluidInTank(int tank);

  /**
   * Gets the amount of empty space in the tank
   * @return  Amount of empty space
   */
  int getRemainingSpace();
}
