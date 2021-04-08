package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem extends ItemConvertible {
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

  /**
   * Gets the given item stack with this material applied
   * @param stack     Stack instance
   * @param material  Material
   * @return  Stack with material, or original stack if not a material item
   */
  static ItemStack withMaterial(ItemStack stack, IMaterial material) {
    Item item = stack.getItem();
    if (item instanceof IMaterialItem) {
      ItemStack output = ((IMaterialItem) item).getItemstackWithMaterial(material);
      if (stack.hasTag()) {
        assert stack.getTag() != null;
        assert output.getTag() != null;
        output.getTag().copyFrom(stack.getTag());
      }
      return output;
    }
    return stack;
  }
}
