package slimeknights.tconstruct.library.recipe.inventory;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public interface IFluidInventory extends IReadOnlyInventory {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();

  /* Required methods */

  /** @deprecated unused method */
  @Deprecated
  @Override
  default ItemStack getStackInSlot(int index) {
    return ItemStack.EMPTY;
  }

  /** @deprecated unused method */
  @Deprecated
  @Override
  default boolean isEmpty() {
    return true;
  }

  /** @deprecated always 0, not useful */
  @Deprecated
  @Override
  default int getSizeInventory() {
    return 0;
  }
}
