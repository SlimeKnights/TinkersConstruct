package tconstruct.library.utils;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.partbehaviors.PartBehavior;

public final class ToolBuilder {
  private ToolBuilder() {}

  /**
   * A simple Tool consists of a head and an handle.
   * Head determines primary stats, handle multiplies primary stats.
   */
  public static NBTTagCompound buildSimpleTool(PartBehavior head, PartBehavior handle) {
    return null;
  }

}
