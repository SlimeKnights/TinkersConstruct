package tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem {

  /**
   * Returns the material identifier of the material of the part this itemstack holds.
   *
   * @return Identifier of a material or "Unknown", null or empty if invalid.
   */
  public String getMaterialID(ItemStack stack);

  /**
   * Returns the material of the part this itemstack holds.
   *
   * @return Material or Material.UNKNOWN if invalid
   */
  public Material getMaterial(ItemStack stack);
}
