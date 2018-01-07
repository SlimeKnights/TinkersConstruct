package slimeknights.tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;

import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolNBT;

public final class TagUtil {

  public static int TAG_TYPE_STRING = (new NBTTagString()).getId();
  public static int TAG_TYPE_COMPOUND = (new NBTTagCompound()).getId();

  private TagUtil() {
  }

  /* Generic Tag Operations */
  public static NBTTagCompound getTagSafe(ItemStack stack) {
    // yes, the null checks aren't needed anymore, but they don't hurt either.
    // After all the whole purpose of this function is safety/processing possibly invalid input ;)
    if(stack == null || stack.getItem() == null || stack.isEmpty() || !stack.hasTagCompound()) {
      return new NBTTagCompound();
    }

    return stack.getTagCompound();
  }

  public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
    if(tag == null) {
      return new NBTTagCompound();
    }

    return tag.getCompoundTag(key);
  }

  public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
    if(tag == null) {
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

  public static void setNoRenameFlag(ItemStack stack, boolean active) {
    NBTTagCompound root = getTagSafe(stack);
    setNoRenameFlag(root, active);
    stack.setTagCompound(root);
  }

  public static void setNoRenameFlag(NBTTagCompound root, boolean active) {
    NBTTagCompound displayTag = root.getCompoundTag("display");
    if(displayTag.hasKey("Name")) {
      displayTag.setBoolean(Tags.NO_RENAME, active);
      root.setTag("display", displayTag);
    }
  }

  public static boolean getNoRenameFlag(ItemStack stack) {
    NBTTagCompound root = getTagSafe(stack);
    NBTTagCompound displayTag = root.getCompoundTag("display");
    return displayTag.getBoolean(Tags.NO_RENAME);
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
}
