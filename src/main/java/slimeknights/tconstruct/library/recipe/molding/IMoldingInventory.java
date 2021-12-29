package slimeknights.tconstruct.library.recipe.molding;

import net.minecraft.world.item.ItemStack;
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
   * Gets the item whose shape makes the pattern, typically in hand, often a tool part
   * @return  Pattern item
   */
  ItemStack getPattern();


  /* Required methods */

  /** @deprecated use {@link #getMaterial()} and {@link #getPattern()} */
  @Deprecated
  @Override
  default ItemStack getItem(int index) {
    switch (index) {
      case 0:
        return getMaterial();
      case 1:
        return getPattern();
    }
    return ItemStack.EMPTY;
  }

  @Override
  default int getContainerSize() {
    return 2;
  }

  @Override
  default boolean isEmpty() {
    return getPattern().isEmpty() && getMaterial().isEmpty();
  }
}
