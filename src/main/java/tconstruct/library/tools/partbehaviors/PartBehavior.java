package tconstruct.library.tools.partbehaviors;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.Material;

public abstract class PartBehavior {

  private final String[] requiredMaterialTypes;

  public PartBehavior(String... requiredMaterialType) {
    this.requiredMaterialTypes = requiredMaterialType;
  }

  public boolean isMaterialUsable(Material material) {
    if (material == null) {
      return false;
    }

    for (String type : requiredMaterialTypes) {
      if (material.getStats(type) == null) {
        // required material not found
        return false;
      }
    }

    return true;
  }

  /**
   * Apply the behaviour of the part to the stats of the tool.
   *
   * @param tag      The Tinker-Base-Tag of the tool
   * @param material The material to use
   */
  public abstract void applyPartBehavior(NBTTagCompound tag, Material material);
}
