package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem {

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
}
