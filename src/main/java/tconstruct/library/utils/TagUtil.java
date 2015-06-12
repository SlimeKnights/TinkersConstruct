package tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Set;

import tconstruct.library.tinkering.ITinkerable;

public final class TagUtil {

  private TagUtil() {
  }

  public static NBTTagCompound getTagSafe(ItemStack stack) {
    if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
      return new NBTTagCompound();
    }

    return stack.getTagCompound();
  }

  public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
    if (tag == null || !tag.hasKey(key)) {
      return new NBTTagCompound();
    }

    return tag.getCompoundTag(key);
  }

  public static NBTTagCompound getBaseTag(ItemStack stack) {
    if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
      return null;
    }

    return stack.getTagCompound().getCompoundTag(Tags.BASE_DATA);
  }

  public static NBTTagCompound getBaseTagSafe(ItemStack stack) {
    NBTTagCompound tag = getBaseTag(stack);

    return tag == null ? new NBTTagCompound() : tag;
  }

  public static void setBaseTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound tagCompound = TagUtil.getTagSafe(stack);
    tagCompound.setTag(Tags.BASE_DATA, tag);

    stack.setTagCompound(tagCompound);
  }

  /**
   * Returns the Tinkers NBT Tag of an itemstack if present.
   *
   * @return the tag or null if none is present or if it's not a tinker tool..
   */
  public static NBTTagCompound getToolTag(ItemStack stack) {
    if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
      return null;
    }

    if (!(stack.getItem() instanceof ITinkerable)) {
      return null;
    }

    return stack.getTagCompound().getCompoundTag(Tags.TOOL_DATA);
  }

  /**
   * Like getToolTag but returns an empty tag instead of null on failure.
   */
  public static NBTTagCompound getToolTagSafe(ItemStack stack) {
    NBTTagCompound tag = getToolTag(stack);

    return tag == null ? new NBTTagCompound() : tag;
  }

  public static void setToolTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound tagCompound = TagUtil.getTagSafe(stack);
    tagCompound.setTag(Tags.TOOL_DATA, tag);

    stack.setTagCompound(tagCompound);
  }


  public static NBTTagCompound getModifiersBaseTag(ItemStack stack) {
    return getTagSafe(getBaseTag(stack), Tags.BASE_MODIFIERS);
  }

  public static NBTTagCompound getModifiersTag(ItemStack stack) {
    return getTagSafe(getTagSafe(stack), Tags.TOOL_MODIFIERS);
  }

  public static void setModifiersTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound tagCompound = TagUtil.getTagSafe(stack);
    tagCompound.setTag(Tags.TOOL_MODIFIERS, tag);

    stack.setTagCompound(tagCompound);
  }

  /**
   * Adds the given value to the integer tag given.
   *
   * @param tag   The tag to modify.
   * @param key   The key where to take the first value from. 0 if nonexistent.
   * @param value The value to add to the existing value on the tag.
   */
  public static void addInteger(NBTTagCompound tag, String key, int value) {
    tag.setInteger(key, tag.getInteger(key) + value);
  }

  /**
   * Adds the given value to the float tag given.
   *
   * @param tag   The tag to modify.
   * @param key   The key where to take the first value from. 0 if nonexistent.
   * @param value The value to add to the existing value on the tag.
   */
  public static void addFloat(NBTTagCompound tag, String key, float value) {
    tag.setFloat(key, tag.getFloat(key) + value);
  }

  /**
   * Takes an arbitrary amount of NBT tags and combines them together. The resulting tags are the average of the values
   * in each TagCompound. If one of the tags is missing in a TagCompound, the average will be taken from the other ones
   * instead of counting as 0.
   *
   * Remark: Only uses Float and Integer tags.
   *
   * @param tags The Tags to combine.
   * @return The combined TagCompound
   */
  public static NBTTagCompound combineTagsAverage(NBTTagCompound... tags) {
    Set<String> processedKeys = new HashSet<>();
    NBTTagCompound result = new NBTTagCompound();

    // cycle through all of the tagcompounds so we don't miss a tag
    for (NBTTagCompound current : tags) {
      for (Object o : current.getKeySet()) {
        String key = (String) o;
        // skip already processed keys
        if (processedKeys.contains(key)) {
          continue;
        }

        processedKeys.add(key);

        switch (current.getTag(key).getId()) {
          // int
          case 3:
            result.setInteger(key, averageIntTags(key, tags));
            break;
          // float
          case 5:
            result.setFloat(key, averageFloatTags(key, tags));
            break;
          case 1: // byte
          case 2: // short
          case 4: // long
          case 6: // double
          case 11: // int array
          default: // not supported
        }
      }
    }

    return result;
  }

  private static int averageIntTags(String key, NBTTagCompound... tags) {
    int count = 0;
    int total = 0;
    // get the value for each of the tags
    for (NBTTagCompound compound : tags) {
      if (compound.hasKey(key)) {
        count++;
        total += compound.getInteger(key);
      }
    }

    return total / count;
  }

  private static float averageFloatTags(String key, NBTTagCompound... tags) {
    int count = 0;
    float total = 0;
    // get the value for each of the tags
    for (NBTTagCompound compound : tags) {
      if (compound.hasKey(key)) {
        count++;
        total += compound.getFloat(key);
      }
    }

    return total / (float) count;
  }
}
