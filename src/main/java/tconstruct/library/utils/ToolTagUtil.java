package tconstruct.library.utils;

import net.minecraft.nbt.NBTTagCompound;

public final class ToolTagUtil {
  public static float getMiningSpeed(NBTTagCompound tag) { return tag.getFloat(Tags.MININGSPEED); }
  public static float getAttack(NBTTagCompound tag) { return tag.getFloat(Tags.ATTACK); }
  public static int getDurability(NBTTagCompound tag) { return tag.getInteger(Tags.DURABILITY); }
  public static int getHarvestLevel(NBTTagCompound tag) { return tag.getInteger(Tags.HARVESTLEVEL); }
  public static int getFreeModifiers(NBTTagCompound tag) {return tag.getInteger(Tags.MODIFIERS); }

  private ToolTagUtil() {}
}
