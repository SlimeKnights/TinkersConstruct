package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagUtil {
  public static int TAG_TYPE_STRING = (StringNBT.valueOf("")).getId();
  public static int TAG_TYPE_COMPOUND = (new CompoundNBT()).getId();

  /* Generic Tag Operations */

  @Deprecated
  public static CompoundNBT getTagSafe(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return new CompoundNBT();
    }
    return stack.getTag();
  }

  @Deprecated
  public static CompoundNBT getTagSafe(CompoundNBT tag, String key) {
    if (tag == null) {
      return new CompoundNBT();
    }

    return tag.getCompound(key);
  }

  @Deprecated
  public static ListNBT getTagListSafe(CompoundNBT tag, String key, int type) {
    if (tag == null) {
      return new ListNBT();
    }

    return tag.getList(key, type);
  }

  /* Operations concerning the base-data of the tool */
  public static CompoundNBT getBaseTag(ItemStack stack) {
    return getBaseTag(getTagSafe(stack));
  }

  public static CompoundNBT getBaseTag(CompoundNBT root) {
    return getTagSafe(root, Tags.BASE);
  }

  public static void setBaseTag(ItemStack stack, CompoundNBT tag) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setBaseTag(root, tag);

    stack.setTag(root);
  }

  public static void setBaseTag(CompoundNBT root, CompoundNBT tag) {
    if (root != null) {
      root.put(Tags.BASE, tag);
    }
  }

  public static ListNBT getBaseModifiersTagList(ItemStack stack) {
    return getBaseModifiersTagList(getTagSafe(stack));
  }

  public static ListNBT getBaseModifiersTagList(CompoundNBT root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MODIFIERS, TAG_TYPE_STRING);
  }

  public static void setBaseModifiersTagList(ItemStack stack, ListNBT tagList) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setBaseModifiersTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setBaseModifiersTagList(CompoundNBT root, ListNBT tagList) {
    CompoundNBT baseTag = getBaseTag(root);
    baseTag.put(Tags.BASE_MODIFIERS, tagList);
    setBaseTag(root, baseTag);
  }

  public static ListNBT getBaseMaterialsTagList(ItemStack stack) {
    return getBaseMaterialsTagList(getTagSafe(stack));
  }

  public static ListNBT getBaseMaterialsTagList(CompoundNBT root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MATERIALS, TAG_TYPE_STRING);
  }

  public static void setBaseMaterialsTagList(ItemStack stack, ListNBT tagList) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setBaseMaterialsTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setBaseMaterialsTagList(CompoundNBT root, ListNBT tagList) {
    getBaseTag(root).put(Tags.BASE_MATERIALS, tagList);
  }

  public static int getBaseModifiersUsed(CompoundNBT root) {
    return getBaseTag(root).getInt(Tags.BASE_USED_MODIFIERS);
  }

  public static void setBaseModifiersUsed(CompoundNBT root, int count) {
    getBaseTag(root).putInt(Tags.BASE_USED_MODIFIERS, count);
  }

  /* Operations concerning the calculated tool data */
  public static CompoundNBT getToolTag(ItemStack stack) {
    return getToolTag(getTagSafe(stack));
  }

  public static CompoundNBT getToolTag(CompoundNBT root) {
    return getTagSafe(root, Tags.TOOL_STATS);
  }

  public static void setToolTag(ItemStack stack, CompoundNBT tag) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setToolTag(root, tag);

    stack.setTag(root);
  }

  public static void setToolTag(CompoundNBT root, CompoundNBT tag) {
    if (root != null) {
      root.put(Tags.TOOL_STATS, tag);
    }
  }

  /* Operations concerning the data of modifiers */
  public static ListNBT getModifiersTagList(ItemStack stack) {
    return getModifiersTagList(getTagSafe(stack));
  }

  public static ListNBT getModifiersTagList(CompoundNBT root) {
    return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
  }

  public static void setModifiersTagList(ItemStack stack, ListNBT tagList) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setModifiersTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setModifiersTagList(CompoundNBT root, ListNBT tagList) {
    if (root != null) {
      root.put(Tags.TOOL_MODIFIERS, tagList);
    }
  }

  /* Operations concerning the list of traits present on the tool */
  public static ListNBT getTraitsTagList(ItemStack stack) {
    return getTraitsTagList(getTagSafe(stack));
  }

  public static ListNBT getTraitsTagList(CompoundNBT root) {
    return getTagListSafe(root, Tags.TOOL_TRAITS, TAG_TYPE_STRING);
  }

  public static void setTraitsTagList(ItemStack stack, ListNBT tagList) {
    CompoundNBT root = TagUtil.getTagSafe(stack);
    setTraitsTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setTraitsTagList(CompoundNBT root, ListNBT tagList) {
    if (root != null) {
      root.put(Tags.TOOL_TRAITS, tagList);
    }
  }

  /* Tool stats
  public static ToolNBT getToolStats(ItemStack stack) {
    return getToolStats(getTagSafe(stack));
  }

  public static ToolNBT getToolStats(CompoundNBT root) {
    return new ToolNBT(getToolTag(root));
  }

  public static ToolNBT getOriginalToolStats(ItemStack stack) {
    return getOriginalToolStats(getTagSafe(stack));
  }

  public static ToolNBT getOriginalToolStats(CompoundNBT root) {
    return new ToolNBT(getTagSafe(root, Tags.TOOL_DATA_ORIG));
  }*/

  /* Extra data */
  public static CompoundNBT getExtraTag(ItemStack stack) {
    return getExtraTag(getTagSafe(stack));
  }

  public static CompoundNBT getExtraTag(CompoundNBT root) {
    return getTagSafe(root, Tags.TINKER_EXTRA);
  }

  public static void setExtraTag(ItemStack stack, CompoundNBT tag) {
    CompoundNBT root = getTagSafe(stack);
    setExtraTag(root, tag);
    stack.setTag(root);
  }

  public static void setExtraTag(CompoundNBT root, CompoundNBT tag) {
    root.put(Tags.TINKER_EXTRA, tag);
  }

  /*public static Category[] getCategories(CompoundNBT root) {
    ListNBT categories = getTagListSafe(getExtraTag(root), Tags.EXTRA_CATEGORIES, 8);
    Category[] out = new Category[categories.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = Category.categories.get(categories.getString(i));
    }

    return out;
  }

  public static void setCategories(ItemStack stack, Category[] categories) {
    CompoundNBT root = getTagSafe(stack);
    setCategories(root, categories);
    stack.setTag(root);
  }

  public static void setCategories(CompoundNBT root, Category[] categories) {
    ListNBT list = new ListNBT();
    for (Category category : categories) {
      list.add(new StringNBT(category.name));
    }

    CompoundNBT extra = getExtraTag(root);
    extra.put(Tags.EXTRA_CATEGORIES, list);
    setExtraTag(root, extra);
  }*/

  public static void setEnchantEffect(ItemStack stack, boolean active) {
    CompoundNBT root = getTagSafe(stack);
    setEnchantEffect(root, active);
    stack.setTag(root);
  }

  public static void setEnchantEffect(CompoundNBT root, boolean active) {
    if (active) {
      root.putBoolean(Tags.ENCHANT_EFFECT, true);
    } else {
      root.remove(Tags.ENCHANT_EFFECT);
    }
  }

  public static boolean hasEnchantEffect(ItemStack stack) {
    return hasEnchantEffect(getTagSafe(stack));
  }

  public static boolean hasEnchantEffect(CompoundNBT root) {
    return root.getBoolean(Tags.ENCHANT_EFFECT);
  }

  public static void setResetFlag(ItemStack stack, boolean active) {
    CompoundNBT root = getTagSafe(stack);
    root.putBoolean(Tags.RESET_FLAG, active);
    stack.setTag(root);
  }

  public static boolean getResetFlag(ItemStack stack) {
    return getTagSafe(stack).getBoolean(Tags.RESET_FLAG);
  }

  public static void setNoRenameFlag(ItemStack stack, boolean active) {
    CompoundNBT root = getTagSafe(stack);
    setNoRenameFlag(root, active);
    stack.setTag(root);
  }

  public static void setNoRenameFlag(CompoundNBT root, boolean active) {
    CompoundNBT displayTag = root.getCompound("display");
    if (displayTag.contains("Name")) {
      displayTag.putBoolean(Tags.NO_RENAME, active);
      root.put("display", displayTag);
    }
  }

  public static boolean getNoRenameFlag(ItemStack stack) {
    CompoundNBT root = getTagSafe(stack);
    CompoundNBT displayTag = root.getCompound("display");
    return displayTag.getBoolean(Tags.NO_RENAME);
  }

  /* Helper functions */

  /**
   * Writes a block position to NBT
   * @param pos  Position to write
   * @return  Position in NBT
   */
  public static CompoundNBT writePos(BlockPos pos) {
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("x", pos.getX());
    tag.putInt("y", pos.getY());
    tag.putInt("z", pos.getZ());
    return tag;
  }

  /**
   * Reads a block position from NBT
   * @param tag  Tag
   * @return  Block position, or null if invalid
   */
  @Nullable
  public static BlockPos readPos(CompoundNBT tag) {
    if (tag.contains("x", NBT.TAG_ANY_NUMERIC) && tag.contains("y", NBT.TAG_ANY_NUMERIC) && tag.contains("z", NBT.TAG_ANY_NUMERIC)) {
      return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
    return null;
  }

  /**
   * Reads a block position from NBT
   * @param parent  Parent tag
   * @param key     Position key
   * @return  Block position, or null if invalid or missing
   */
  @Nullable
  public static BlockPos readPos(CompoundNBT parent, String key) {
    if (parent.contains(key, NBT.TAG_COMPOUND)) {
      return readPos(parent.getCompound(key));
    }
    return null;
  }
}
