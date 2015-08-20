package slimeknights.tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ToolTagUtil {

  private ToolTagUtil() {
  }

  public static float getMiningSpeed(NBTTagCompound tag) {
    return tag.getFloat(Tags.MININGSPEED);
  }

  public static float getAttack(NBTTagCompound tag) {
    return tag.getFloat(Tags.ATTACK);
  }

  public static int getDurability(NBTTagCompound tag) {
    return tag.getInteger(Tags.DURABILITY);
  }

  public static int getHarvestLevel(NBTTagCompound tag) {
    return tag.getInteger(Tags.HARVESTLEVEL);
  }

  public static int getFreeModifiers(NBTTagCompound tag) {
    return tag.getInteger(Tags.FREE_MODIFIERS);
  }

  public static void setFreeModifiers(ItemStack stack, int modifiers) {
    NBTTagCompound toolTag = TagUtil.getToolTag(stack);
    toolTag.setInteger(Tags.FREE_MODIFIERS, modifiers);
    TagUtil.setToolTag(stack, toolTag);
  }
}
