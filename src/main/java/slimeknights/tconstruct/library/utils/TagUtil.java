package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagUtil {
  public static int TAG_TYPE_STRING = (StringTag.of("")).getType();
  public static int TAG_TYPE_COMPOUND = (new CompoundTag()).getType();

  /* Generic Tag Operations */

  @Deprecated
  public static CompoundTag getTagSafe(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return new CompoundTag();
    }
    return stack.getTag();
  }

  @Deprecated
  public static CompoundTag getTagSafe(CompoundTag tag, String key) {
    if (tag == null) {
      return new CompoundTag();
    }

    return tag.getCompound(key);
  }

  @Deprecated
  public static ListTag getTagListSafe(CompoundTag tag, String key, int type) {
    if (tag == null) {
      return new ListTag();
    }

    return tag.getList(key, type);
  }

  /* Operations concerning the base-data of the tool */
  public static CompoundTag getBaseTag(ItemStack stack) {
    return getBaseTag(getTagSafe(stack));
  }

  public static CompoundTag getBaseTag(CompoundTag root) {
    return getTagSafe(root, Tags.BASE);
  }

  public static void setBaseTag(ItemStack stack, CompoundTag tag) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setBaseTag(root, tag);

    stack.setTag(root);
  }

  public static void setBaseTag(CompoundTag root, CompoundTag tag) {
    if (root != null) {
      root.put(Tags.BASE, tag);
    }
  }

  public static ListTag getBaseModifiersTagList(ItemStack stack) {
    return getBaseModifiersTagList(getTagSafe(stack));
  }

  public static ListTag getBaseModifiersTagList(CompoundTag root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MODIFIERS, TAG_TYPE_STRING);
  }

  public static void setBaseModifiersTagList(ItemStack stack, ListTag tagList) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setBaseModifiersTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setBaseModifiersTagList(CompoundTag root, ListTag tagList) {
    CompoundTag baseTag = getBaseTag(root);
    baseTag.put(Tags.BASE_MODIFIERS, tagList);
    setBaseTag(root, baseTag);
  }

  public static ListTag getBaseMaterialsTagList(ItemStack stack) {
    return getBaseMaterialsTagList(getTagSafe(stack));
  }

  public static ListTag getBaseMaterialsTagList(CompoundTag root) {
    return getTagListSafe(getBaseTag(root), Tags.BASE_MATERIALS, TAG_TYPE_STRING);
  }

  public static void setBaseMaterialsTagList(ItemStack stack, ListTag tagList) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setBaseMaterialsTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setBaseMaterialsTagList(CompoundTag root, ListTag tagList) {
    getBaseTag(root).put(Tags.BASE_MATERIALS, tagList);
  }

  public static int getBaseModifiersUsed(CompoundTag root) {
    return getBaseTag(root).getInt(Tags.BASE_USED_MODIFIERS);
  }

  public static void setBaseModifiersUsed(CompoundTag root, int count) {
    getBaseTag(root).putInt(Tags.BASE_USED_MODIFIERS, count);
  }

  /* Operations concerning the calculated tool data */
  public static CompoundTag getToolTag(ItemStack stack) {
    return getToolTag(getTagSafe(stack));
  }

  public static CompoundTag getToolTag(CompoundTag root) {
    return getTagSafe(root, Tags.TOOL_STATS);
  }

  public static void setToolTag(ItemStack stack, CompoundTag tag) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setToolTag(root, tag);

    stack.setTag(root);
  }

  public static void setToolTag(CompoundTag root, CompoundTag tag) {
    if (root != null) {
      root.put(Tags.TOOL_STATS, tag);
    }
  }

  /* Operations concerning the data of modifiers */
  public static ListTag getModifiersTagList(ItemStack stack) {
    return getModifiersTagList(getTagSafe(stack));
  }

  public static ListTag getModifiersTagList(CompoundTag root) {
    return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
  }

  public static void setModifiersTagList(ItemStack stack, ListTag tagList) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setModifiersTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setModifiersTagList(CompoundTag root, ListTag tagList) {
    if (root != null) {
      root.put(Tags.TOOL_MODIFIERS, tagList);
    }
  }

  /* Operations concerning the list of traits present on the tool */
  public static ListTag getTraitsTagList(ItemStack stack) {
    return getTraitsTagList(getTagSafe(stack));
  }

  public static ListTag getTraitsTagList(CompoundTag root) {
    return getTagListSafe(root, Tags.TOOL_TRAITS, TAG_TYPE_STRING);
  }

  public static void setTraitsTagList(ItemStack stack, ListTag tagList) {
    CompoundTag root = TagUtil.getTagSafe(stack);
    setTraitsTagList(root, tagList);

    stack.setTag(root);
  }

  public static void setTraitsTagList(CompoundTag root, ListTag tagList) {
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
  public static CompoundTag getExtraTag(ItemStack stack) {
    return getExtraTag(getTagSafe(stack));
  }

  public static CompoundTag getExtraTag(CompoundTag root) {
    return getTagSafe(root, Tags.TINKER_EXTRA);
  }

  public static void setExtraTag(ItemStack stack, CompoundTag tag) {
    CompoundTag root = getTagSafe(stack);
    setExtraTag(root, tag);
    stack.setTag(root);
  }

  public static void setExtraTag(CompoundTag root, CompoundTag tag) {
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
    CompoundTag root = getTagSafe(stack);
    setEnchantEffect(root, active);
    stack.setTag(root);
  }

  public static void setEnchantEffect(CompoundTag root, boolean active) {
    if (active) {
      root.putBoolean(Tags.ENCHANT_EFFECT, true);
    } else {
      root.remove(Tags.ENCHANT_EFFECT);
    }
  }

  public static boolean hasEnchantEffect(ItemStack stack) {
    return hasEnchantEffect(getTagSafe(stack));
  }

  public static boolean hasEnchantEffect(CompoundTag root) {
    return root.getBoolean(Tags.ENCHANT_EFFECT);
  }

  public static void setResetFlag(ItemStack stack, boolean active) {
    CompoundTag root = getTagSafe(stack);
    root.putBoolean(Tags.RESET_FLAG, active);
    stack.setTag(root);
  }

  public static boolean getResetFlag(ItemStack stack) {
    return getTagSafe(stack).getBoolean(Tags.RESET_FLAG);
  }

  public static void setNoRenameFlag(ItemStack stack, boolean active) {
    CompoundTag root = getTagSafe(stack);
    setNoRenameFlag(root, active);
    stack.setTag(root);
  }

  public static void setNoRenameFlag(CompoundTag root, boolean active) {
    CompoundTag displayTag = root.getCompound("display");
    if (displayTag.contains("Name")) {
      displayTag.putBoolean(Tags.NO_RENAME, active);
      root.put("display", displayTag);
    }
  }

  public static boolean getNoRenameFlag(ItemStack stack) {
    CompoundTag root = getTagSafe(stack);
    CompoundTag displayTag = root.getCompound("display");
    return displayTag.getBoolean(Tags.NO_RENAME);
  }

  /* Helper functions */

  /**
   * Writes a block position to NBT
   * @param pos  Position to write
   * @return  Position in NBT
   */
  public static CompoundTag writePos(BlockPos pos) {
    CompoundTag tag = new CompoundTag();
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
  public static BlockPos readPos(CompoundTag tag) {
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
  public static BlockPos readPos(CompoundTag parent, String key) {
    if (parent.contains(key, NBT.TAG_COMPOUND)) {
      return readPos(parent.getCompound(key));
    }
    return null;
  }
}
