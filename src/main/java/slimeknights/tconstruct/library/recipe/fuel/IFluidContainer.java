package slimeknights.tconstruct.library.recipe.fuel;

import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.container.IEmptyContainer;

/**
 * Inventory containing just a single fluid
 */
public interface IFluidContainer extends IEmptyContainer {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
}
