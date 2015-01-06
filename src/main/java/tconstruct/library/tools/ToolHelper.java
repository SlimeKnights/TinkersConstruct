package tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.utils.Tags;
import tconstruct.library.utils.ToolUtil;

public final class ToolHelper {

  private ToolHelper() {
  }

  public static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
    if (stack == null || blockState == null) {
      return 0f;
    }

    if (!(stack.getItem() instanceof TinkersTool)) {
      return 0f;
    }

    // check if the tool has the correct class and harvest level
    if (!canHarvest(stack, blockState)) {
      return 0f;
    }

    // calculate speed depending on stats
    NBTTagCompound tag = ToolUtil.getTinkerTag(stack);
    if (tag == null) {
      return 1f;
    }

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
}
