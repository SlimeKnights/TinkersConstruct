package slimeknights.tconstruct.library.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.traits.ITrait;

public final class ToolHelper {

  private ToolHelper() {
  }

  public static boolean hasCategory(ItemStack stack, Category category) {
    if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof TinkersItem)) {
      return false;
    }

    return ((TinkersItem) stack.getItem()).hasCategory(category);
  }

  /* Basic Tool data */
  public static int getDurability(ItemStack stack) {
    return getIntTag(stack, Tags.DURABILITY);
  }

  public static int getHarvestLevel(ItemStack stack) {
    return getIntTag(stack, Tags.HARVESTLEVEL);
  }

  public static float getMiningSpeed(ItemStack stack) {
    return getfloatTag(stack, Tags.MININGSPEED);
  }

  public static float getAttack(ItemStack stack) {
    return getIntTag(stack, Tags.ATTACK);
  }

  public static int getFreeModifiers(ItemStack stack) {
    return getIntTag(stack, Tags.FREE_MODIFIERS);
  }

  public static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
    if(blockState == null) {
      return 0f;
    }

    if(!stack.hasTagCompound()) {
      return 1f;
    }

    // check if the tool has the correct class and harvest level
    if(!canHarvest(stack, blockState)) {
      return 0f;
    }

    if(isBroken(stack)) {
      return 0.3f;
    }

    // calculate speed depending on stats

    // strength = default 1
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    float strength = stack.getItem().getStrVsBlock(stack, blockState.getBlock());
    float speed = tag.getFloat(Tags.MININGSPEED);

    return strength * speed;
  }

  /**
   * Returns true if the tool is effective for harvesting the given block.
   */
  public static boolean isToolEffective(ItemStack stack, IBlockState state) {
    for(String type : stack.getItem().getToolClasses(stack)) {
      if(state.getBlock().isToolEffective(type, state)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if an item has the right harvest level of the correct type for the block.
   */
  public static boolean canHarvest(ItemStack stack, IBlockState state) {
    Block block = state.getBlock();

    // doesn't require a tool
    if(block.getMaterial().isToolNotRequired()) {
      return true;
    }

    String type = block.getHarvestTool(state);
    int level = block.getHarvestLevel(state);

    return stack.getItem().getHarvestLevel(stack, type) >= level;
  }

  public static void damageTool(ItemStack stack, int amount, EntityLivingBase entity) {
    if(amount == 0 || isBroken(stack))
      return;

    int actualAmount = amount;
    NBTTagList list = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < list.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
      if(trait != null) {
        if(amount > 0) {
          actualAmount = trait.onToolDamage(stack, amount, actualAmount, entity);
        } else {
          actualAmount = trait.onToolHeal(stack, amount, actualAmount, entity);
        }
      }
    }

    // ensure we never deal more damage than durability
    actualAmount = Math.min(actualAmount, stack.getMaxDamage() - stack.getItemDamage());
    stack.damageItem(actualAmount, entity);

    if(stack.getMaxDamage() - stack.getItemDamage() == 0) {
      breakTool(stack, entity);
    }
  }

  public static void healTool(ItemStack stack, int amount, EntityLivingBase entity) {
    damageTool(stack, -amount, entity);
  }

  public static boolean isBroken(ItemStack stack) {
    return TagUtil.getToolTag(stack).getBoolean(Tags.BROKEN);
  }

  public static void breakTool(ItemStack stack, EntityLivingBase entity) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    tag.setBoolean(Tags.BROKEN, true);
    TagUtil.setToolTag(stack, tag);

    entity.renderBrokenItemStack(stack);
  }

  public static void repairTool(ItemStack stack, int amount, EntityLivingBase entity) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
    tag.setBoolean(Tags.BROKEN, false);
    TagUtil.setToolTag(stack, tag);

    healTool(stack, amount, entity);
  }

  /* Helper Functions */

  public static int getIntTag(ItemStack stack, String key) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);

    return tag.getInteger(key);
  }

  public static float getfloatTag(ItemStack stack, String key) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);

    return tag.getFloat(key);
  }
}
