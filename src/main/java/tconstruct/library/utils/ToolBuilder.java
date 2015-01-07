package tconstruct.library.utils;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.materials.ToolMaterialStats;

public final class ToolBuilder {

  private ToolBuilder() {
  }

  /**
   * A simple Tool consists of a head and an handle. Head determines primary stats, handle
   * multiplies primary stats.
   */
  public static NBTTagCompound buildSimpleTool(Material headMaterial, Material handleMaterial, Material... accessoriesMaterials) {
    NBTTagCompound result;
    ToolMaterialStats headStats = headMaterial.getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats handleStats = handleMaterial.getStats(ToolMaterialStats.TYPE);

    // get the start values from the head
    result = calculateHeadParts(headStats);
    // add the accessories
    for(Material material : accessoriesMaterials)
    {
      ToolMaterialStats accessoryStats = material.getStats(ToolMaterialStats.TYPE);
      calculateAccessoryParts(result, accessoryStats);
    }

    // multiply with the handles
    calculateHandleParts(result, handleStats);

    // and don't forget the harvest level
    calculateHarvestLevel(result, headStats);


    return result;
  }

  /**
   * Takes an arbitrary amount of ToolMaterialStats and creates a tag with the mean of their stats.
   *
   * @return The resulting TagCompound
   */
  public static NBTTagCompound calculateHeadParts(ToolMaterialStats... stats) {
    int durability = 0;
    float attack = 0f;
    float speed = 0f;

    // sum up stats
    for (ToolMaterialStats stat : stats) {
      durability += stat.durability;
      attack += stat.attack;
      speed += stat.miningspeed;
    }

    // take mean
    durability /= stats.length;
    attack /= (float) stats.length;
    speed /= (float) stats.length;

    // create output tag
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger(Tags.DURABILITY, durability);
    tag.setFloat(Tags.ATTACK, attack);
    tag.setFloat(Tags.MININGSPEED, speed);

    return tag;
  }

  /**
   * Adds the durability of the given materials to the tag.
   */
  public static void calculateAccessoryParts(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int durability = baseTag.getInteger(Tags.DURABILITY);

    // sum up stats
    for (ToolMaterialStats stat : stats) {
      durability += stat.durability;
    }

    // set value
    baseTag.setInteger(Tags.DURABILITY, durability);
  }

  /**
   * Takes an arbitrary amount of ToolMaterialStats and multiplies the durability in the basetag
   * with the average
   */
  public static void calculateHandleParts(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int count = 0;
    float multiplier = 0;

    // sum up stats
    for (ToolMaterialStats stat : stats) {
      multiplier += stat.durabilityModifier;
      count++;
    }

    if (count > 0) {
      // calculate the multiplier from the summed up stats
      multiplier *= (0.5 + count * 0.5);
      multiplier /= count;

      int durability = baseTag.getInteger(Tags.DURABILITY);
      durability *= multiplier;
      baseTag.setInteger(Tags.DURABILITY, durability);
    }
  }

  /**
   * Choses the highest harvestlevel of the given materials and sets it in the tag.
   */
  public static void calculateHarvestLevel(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int harvestLevel = 0;

    // get max
    for (ToolMaterialStats stat : stats) {
      if(stat.harvestLevel > harvestLevel)
        harvestLevel = stat.harvestLevel;
    }

    baseTag.setInteger(Tags.HARVESTLEVEL, harvestLevel);
  }
}
