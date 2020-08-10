package slimeknights.tconstruct.smeltery.recipe;

import net.minecraft.fluid.Fluid;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;

/**
 * Inventory containing a single item and a fluid
 */
public interface ICastingInventory extends ISingleItemInventory {
  /**
   * Gets the contained fluid in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
}
