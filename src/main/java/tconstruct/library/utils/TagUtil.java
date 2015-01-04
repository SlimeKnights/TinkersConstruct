package tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import tconstruct.library.ITinkerable;

public final class TagUtil {

  private TagUtil() {
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

    return stack.getTagCompound().getCompoundTag(((ITinkerable) stack.getItem()).getTagName());
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
   * Takes an arbitrary amount of NBT tags and combines them together. The resulting tags are the
   * average of the values in each TagCompound. If one of the tags is missing in a TagCompound, the
   * average will be taken from the other ones instead of counting as 0.
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

    return total/count;
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

    return total/(float)count;
  }
}
