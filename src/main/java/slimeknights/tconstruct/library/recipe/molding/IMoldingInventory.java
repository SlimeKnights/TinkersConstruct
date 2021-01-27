package slimeknights.tconstruct.library.recipe.molding;

import net.minecraft.item.ItemStack;
import slimeknights.mantle.recipe.inventory.IReadOnlyInventory;

/**
 * Inventory for molding recipes
 */
public interface IMoldingInventory extends IReadOnlyInventory {
  /**
   * Gets the material being molded, typically sand
   * @return  Material item
   */
  ItemStack getMaterial();

  /**
   * Gets the item whose shape makes the mold, typically in hand
   * @return  Mold item
   */
  ItemStack getMold();


  /* Required methods */

  /** @deprecated use {@link #getMaterial()} and {@link #getMold()} */
  @Deprecated
  @Override
  default ItemStack getStackInSlot(int index) {
    switch (index) {
      case 0:
        return getMaterial();
      case 1:
        return getMold();
    }
    return ItemStack.EMPTY;
  }

  @Override
  default int getSizeInventory() {
    return 2;
  }

  @Override
  default boolean isEmpty() {
    return getMold().isEmpty() && getMaterial().isEmpty();
  }
}
