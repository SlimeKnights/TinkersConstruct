package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import slimeknights.tconstruct.library.materials.IMaterial;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem extends IItemProvider {
  /**
   * Returns the material of the part this itemstack holds.
   *
   * @return Material or Material.UNKNOWN if invalid
   */
  IMaterial getMaterial(ItemStack stack);

  /**
   * Returns the item with the given material
   */
  ItemStack getItemstackWithMaterial(IMaterial material);

  /**
   * Returns true if the material can be used for this toolpart
   */
  default boolean canUseMaterial(IMaterial mat) {
    return true;
  }

  /**
   * Gets the material from a given item stack
   * @param stack  Item stack containing a material item
   * @return  Material, or unknown if none
   */
  static IMaterial getMaterialFromStack(ItemStack stack) {
    if ((stack.getItem() instanceof IMaterialItem)) {
      return ((IMaterialItem) stack.getItem()).getMaterial(stack);
    }
    return IMaterial.UNKNOWN;
  }
}
