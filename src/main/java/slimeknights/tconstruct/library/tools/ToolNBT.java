package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.utils.Tags;

public class ToolNBT {
  public int durability;
  public int harvestLevel;
  public float attack;
  public float speed; // mining speed
  public int modifiers; // free modifiers

  public ToolNBT() {}

  public ToolNBT(ToolMaterialStats stats) {
    durability = stats.durability;
    harvestLevel = stats.harvestLevel;
    attack = stats.attack;
    speed = stats.miningspeed;
  }

  public ToolNBT(NBTTagCompound tag) {
    read(tag);
  }

  public void read(NBTTagCompound tag) {
    durability = tag.getInteger(Tags.DURABILITY);
    harvestLevel = tag.getInteger(Tags.HARVESTLEVEL);
    attack = tag.getFloat(Tags.ATTACK);
    speed = tag.getFloat(Tags.MININGSPEED);
    modifiers = tag.getInteger(Tags.FREE_MODIFIERS);
  }

  public void write(NBTTagCompound tag) {
    tag.setInteger(Tags.DURABILITY, durability);
    tag.setInteger(Tags.HARVESTLEVEL, harvestLevel);
    tag.setFloat(Tags.ATTACK, attack);
    tag.setFloat(Tags.MININGSPEED, speed);
    tag.setInteger(Tags.FREE_MODIFIERS, modifiers);
  }

  public NBTTagCompound get() {
    NBTTagCompound tag = new NBTTagCompound();
    write(tag);

    return tag;
  }
}
