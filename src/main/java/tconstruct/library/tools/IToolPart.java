package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart {

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
