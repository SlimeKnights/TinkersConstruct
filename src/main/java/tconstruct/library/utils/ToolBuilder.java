package tconstruct.library.utils;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.tools.partbehaviors.HeadPartBehavior;
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

  /**
   * Takes an arbitrary amount of ToolMaterialStats and creates a tag with the mean of their stats.
   * @return The resulting TagCompound
   */
  public static NBTTagCompound calculateHeadParts(ToolMaterialStats... stats) {
    int durability = 0;
    float attack = 0f;
    float speed = 0f;

    // sum up stats
    for(ToolMaterialStats stat : stats)
    {
      durability += stat.durability;
      attack += stat.attack;
      speed += stat.miningspeed;
    }

    // take mean
    durability /= stats.length;
    attack /= (float)stats.length;
    speed /= (float)stats.length;

    // create output tag
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger(Tags.DURABILITY, durability);
    tag.setFloat(Tags.ATTACK, attack);
    tag.setFloat(Tags.MININGSPEED, speed);

    return tag;
  }
}
