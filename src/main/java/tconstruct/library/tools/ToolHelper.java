package tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.utils.Tags;
import tconstruct.library.utils.ToolUtil;

public final class ToolHelper {

  private ToolHelper() {
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

  public static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
    if(blockState == null) {
      return 0f;
    }

    NBTTagCompound tag = getToolTag(stack);
    if (tag == null) {
      return 1f;
    }

    // check if the tool has the correct class and harvest level
    if (!canHarvest(stack, blockState)) {
      return 0f;
    }

    // calculate speed depending on stats


    // strength = default 1
    float strength = stack.getItem().getStrVsBlock(stack, blockState.getBlock());
    float speed = tag.getFloat(Tags.MININGSPEED);

    return strength * speed;
  }

  /**
   * Returns true if the tool is effective for harvesting the given block.
   */
  public static boolean isToolEffective(ItemStack stack, IBlockState state) {
    for (String type : stack.getItem().getToolClasses(stack)) {
      if (state.getBlock().isToolEffective(type, state)) {
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
    if (block.getMaterial().isToolNotRequired()) {
      return true;
    }

    String type = block.getHarvestTool(state);
    int level = block.getHarvestLevel(state);

    return stack.getItem().getHarvestLevel(stack, type) >= level;
  }

  /* Helper Functions */

  // a small helper function so we don't have to write all the checks in every function
  // also doubles as safety check
  private static NBTTagCompound getToolTag(ItemStack stack) {
    NBTTagCompound tag = ToolUtil.getTinkerTag(stack);
    // safe check because getTinkerTag does all the null checks
    if(tag != null && stack.getItem() instanceof TinkersTool)
      return tag;

    return null;
  }

  public static int getIntTag(ItemStack stack, String key) {
    // getTinkerTag doubles as safety check
    NBTTagCompound tag = getToolTag(stack);
    if(tag == null) {
      return 0;
    }

    return tag.getInteger(key);
  }

  public static float getfloatTag(ItemStack stack, String key) {
    // getTinkerTag doubles as safety check
    NBTTagCompound tag = getToolTag(stack);
    if(tag == null) {
      return 0;
    }

    return tag.getFloat(key);
  }
}
