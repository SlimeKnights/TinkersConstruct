package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolNBT;

public final class TagUtil {

  public static int TAG_TYPE_STRING = (new NBTTagString()).getId();
  public static int TAG_TYPE_COMPOUND = (new NBTTagCompound()).getId();

  private TagUtil() {
  }

  /* Generic Tag Operations */
  public static NBTTagCompound getTagSafe(ItemStack stack) {
    if(stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
      return new NBTTagCompound();
    }

    return stack.getTagCompound();
  }

  public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
    if(tag == null || !tag.hasKey(key)) {
      return new NBTTagCompound();
    }

    return tag.getCompoundTag(key);
  }

  public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
    if(tag == null || !tag.hasKey(key)) {
      return new NBTTagList();
    }

    return tag.getTagList(key, type);
  }


  /* Operations concerning the base-data of the tool */
  public static NBTTagCompound getBaseTag(ItemStack stack) {
    return getBaseTag(getTagSafe(stack));
  }

  public static NBTTagCompound getBaseTag(NBTTagCompound root) {
    return getTagSafe(root, Tags.BASE_DATA);
  }

  public static void setBaseTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setBaseTag(root, tag);

    stack.setTagCompound(root);
  }

  public static void setBaseTag(NBTTagCompound root, NBTTagCompound tag) {
    if(root != null) {
      root.setTag(Tags.BASE_DATA, tag);
    }
  }


  public static NBTTagList getBaseModifiersTagList(ItemStack stack) {
    return getBaseModifiersTagList(getTagSafe(stack));
  }

  public static NBTTagList getBaseModifiersTagList(NBTTagCompound root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MODIFIERS, TAG_TYPE_STRING);
  }

  public static void setBaseModifiersTagList(ItemStack stack, NBTTagList tagList) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setBaseModifiersTagList(root, tagList);

    stack.setTagCompound(root);
  }

  public static void setBaseModifiersTagList(NBTTagCompound root, NBTTagList tagList) {
    NBTTagCompound baseTag = getBaseTag(root);
    baseTag.setTag(Tags.BASE_MODIFIERS, tagList);
    setBaseTag(root, baseTag);
  }

  public static NBTTagList getBaseMaterialsTagList(ItemStack stack) {
    return getBaseMaterialsTagList(getTagSafe(stack));
  }

  public static NBTTagList getBaseMaterialsTagList(NBTTagCompound root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MATERIALS, TAG_TYPE_STRING);
  }

  public static void setBaseMaterialsTagList(ItemStack stack, NBTTagList tagList) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setBaseMaterialsTagList(root, tagList);

    stack.setTagCompound(root);
  }

  public static void setBaseMaterialsTagList(NBTTagCompound root, NBTTagList tagList) {
    getBaseTag(root).setTag(Tags.BASE_MATERIALS, tagList);
  }

  public static int getBaseModifiersUsed(NBTTagCompound root) {
    return getBaseTag(root).getInteger(Tags.BASE_USED_MODIFIERS);
  }

  public static void setBaseModifiersUsed(NBTTagCompound root, int count) {
    getBaseTag(root).setInteger(Tags.BASE_USED_MODIFIERS, count);
  }

  /* Operations concerning the calculated tool data */
  public static NBTTagCompound getToolTag(ItemStack stack) {
    return getToolTag(getTagSafe(stack));
  }

  public static NBTTagCompound getToolTag(NBTTagCompound root) {
    return getTagSafe(root, Tags.TOOL_DATA);
  }

  public static void setToolTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setToolTag(root, tag);

    stack.setTagCompound(root);
  }

  public static void setToolTag(NBTTagCompound root, NBTTagCompound tag) {
    if(root != null) {
      root.setTag(Tags.TOOL_DATA, tag);
    }
  }


  /* Operations concerning the data of modifiers */
  public static NBTTagList getModifiersTagList(ItemStack stack) {
    return getModifiersTagList(getTagSafe(stack));
  }

  public static NBTTagList getModifiersTagList(NBTTagCompound root) {
    return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
  }

  public static void setModifiersTagList(ItemStack stack, NBTTagList tagList) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setModifiersTagList(root, tagList);

    stack.setTagCompound(root);
  }

  public static void setModifiersTagList(NBTTagCompound root, NBTTagList tagList) {
    if(root != null) {
      root.setTag(Tags.TOOL_MODIFIERS, tagList);
    }
  }

  /* Operations concerning the list of traits present on the tool */
  public static NBTTagList getTraitsTagList(ItemStack stack) {
    return getTraitsTagList(getTagSafe(stack));
  }

  public static NBTTagList getTraitsTagList(NBTTagCompound root) {
    return getTagListSafe(root, Tags.TOOL_TRAITS, TAG_TYPE_STRING);
  }

  public static void setTraitsTagList(ItemStack stack, NBTTagList tagList) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    setTraitsTagList(root, tagList);

    stack.setTagCompound(root);
  }

  public static void setTraitsTagList(NBTTagCompound root, NBTTagList tagList) {
    if(root != null) {
      root.setTag(Tags.TOOL_TRAITS, tagList);
    }
  }

  /* Tool stats */
  public static ToolNBT getToolStats(ItemStack stack) {
    return getToolStats(getTagSafe(stack));
  }

  public static ToolNBT getToolStats(NBTTagCompound root) {
    return new ToolNBT(getToolTag(root));
  }

  public static ToolNBT getOriginalToolStats(ItemStack stack) {
    return getOriginalToolStats(getTagSafe(stack));
  }

  public static ToolNBT getOriginalToolStats(NBTTagCompound root) {
    return new ToolNBT(getTagSafe(root, Tags.TOOL_DATA_ORIG));
  }

  /* Extra data */
  public static NBTTagCompound getExtraTag(ItemStack stack) {
    return getExtraTag(getTagSafe(stack));
  }

  public static NBTTagCompound getExtraTag(NBTTagCompound root) {
    return getTagSafe(root, Tags.TINKER_EXTRA);
  }

  public static void setExtraTag(ItemStack stack, NBTTagCompound tag) {
    NBTTagCompound root = getTagSafe(stack);
    setExtraTag(root, tag);
    stack.setTagCompound(root);
  }

  public static void setExtraTag(NBTTagCompound root, NBTTagCompound tag) {
    root.setTag(Tags.TINKER_EXTRA, tag);
  }

  public static Category[] getCategories(NBTTagCompound root) {
    NBTTagList categories = getTagListSafe(getExtraTag(root), Tags.EXTRA_CATEGORIES, 8);
    Category[] out = new Category[categories.tagCount()];
    for(int i = 0; i < out.length; i++) {
      out[i] = Category.categories.get(categories.getStringTagAt(i));
    }

    return out;
  }

  public static void setCategories(ItemStack stack, Category[] categories) {
    NBTTagCompound root = getTagSafe(stack);
    setCategories(root, categories);
    stack.setTagCompound(root);
  }

  public static void setCategories(NBTTagCompound root, Category[] categories) {
    NBTTagList list = new NBTTagList();
    for(Category category : categories) {
      list.appendTag(new NBTTagString(category.name));
    }

    NBTTagCompound extra = getExtraTag(root);
    extra.setTag(Tags.EXTRA_CATEGORIES, list);
    setExtraTag(root, extra);
  }

  public static void setEnchantEffect(ItemStack stack, boolean active) {
    NBTTagCompound root = getTagSafe(stack);
    setEnchantEffect(root, active);
    stack.setTagCompound(root);
  }

  public static void setEnchantEffect(NBTTagCompound root, boolean active) {
    if(active) {
      root.setBoolean(Tags.ENCHANT_EFFECT, true);
    }
    else {
      root.removeTag(Tags.ENCHANT_EFFECT);
    }
  }

  public static boolean hasEnchantEffect(ItemStack stack) {
    return hasEnchantEffect(getTagSafe(stack));
  }

  public static boolean hasEnchantEffect(NBTTagCompound root) {
    return root.getBoolean(Tags.ENCHANT_EFFECT);
  }

  public static void setResetFlag(ItemStack stack, boolean active) {
    NBTTagCompound root = getTagSafe(stack);
    root.setBoolean(Tags.RESET_FLAG, active);
    stack.setTagCompound(root);
  }

  public static boolean getResetFlag(ItemStack stack) {
    return getTagSafe(stack).getBoolean(Tags.RESET_FLAG);
  }

  /* Helper functions */

  public static NBTTagCompound writePos(BlockPos pos) {
    NBTTagCompound tag = new NBTTagCompound();
    if(pos != null) {
      tag.setInteger("X", pos.getX());
      tag.setInteger("Y", pos.getY());
      tag.setInteger("Z", pos.getZ());
    }
    return tag;
  }

  public static BlockPos readPos(NBTTagCompound tag) {
    if(tag != null) {
      return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }
    return null;
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
    Set<String> processedKeys = Sets.newHashSet();
    NBTTagCompound result = new NBTTagCompound();

    // cycle through all of the tagcompounds so we don't miss a tag
    for(NBTTagCompound current : tags) {
      for(Object o : current.getKeySet()) {
        String key = (String) o;
        // skip already processed keys
        if(processedKeys.contains(key)) {
          continue;
        }

        processedKeys.add(key);

        switch(current.getTag(key).getId()) {
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
    for(NBTTagCompound compound : tags) {
      if(compound.hasKey(key)) {
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
    for(NBTTagCompound compound : tags) {
      if(compound.hasKey(key)) {
        count++;
        total += compound.getFloat(key);
      }
    }

    return total / (float) count;
  }
}
