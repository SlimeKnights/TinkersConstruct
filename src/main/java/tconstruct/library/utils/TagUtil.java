package tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.ITinkerItem;

public final class TagUtil {
  private TagUtil() {}

  /**
   * Returns the Tinkers NBT Tag of an itemstack if present.
   * @return the tag or null if none is present or if it's not a tinker tool..
   */
  public static NBTTagCompound getToolTag(ItemStack stack) {
    if(stack == null || stack.getItem() == null || !stack.hasTagCompound())
      return null;

    if(!(stack.getItem() instanceof ITinkerItem))
      return null;

    return stack.getTagCompound().getCompoundTag(((ITinkerItem) stack.getItem()).getTagName());
  }

  /**
   * Adds the given value to the integer tag given.
   * @param tag The tag to modify.
   * @param key The key where to take the first value from. 0 if nonexistent.
   * @param value The value to add to the existing value on the tag.
   */
  public static void addInteger(NBTTagCompound tag, String key, int value) {
    tag.setInteger(key, tag.getInteger(key) + value);
  }

  /**
   * Adds the given value to the float tag given.
   * @param tag The tag to modify.
   * @param key The key where to take the first value from. 0 if nonexistent.
   * @param value The value to add to the existing value on the tag.
   */
  public static void addFloat(NBTTagCompound tag, String key, float value) {
    tag.setFloat(key, tag.getFloat(key) + value);
  }

  public static final String TAG_BASE = "Tinkers";

  public static final String TAG_DURABILITY = "Durability";
}
