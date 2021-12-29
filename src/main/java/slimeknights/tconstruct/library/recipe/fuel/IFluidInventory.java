package slimeknights.tconstruct.library.recipe.fuel;

import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;

/**
 * Inventory containing just a single fluid
 */
public interface IFluidInventory extends IEmptyInventory {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
}
