package slimeknights.tconstruct.library.recipe.molding;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.container.IRecipeContainer;

/**
 * Inventory for molding recipes
 */
public interface IMoldingContainer extends IRecipeContainer {
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
    return switch (index) {
      case 0 -> getMaterial();
      case 1 -> getPattern();
      default -> ItemStack.EMPTY;
    };
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
