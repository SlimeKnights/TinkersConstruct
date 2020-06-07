package slimeknights.tconstruct.smeltery.recipe;

import net.minecraft.fluid.Fluid;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;

public interface ICastingInventory extends ISingleItemInventory {
  /**
   * Gets the contained fluid in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
}
