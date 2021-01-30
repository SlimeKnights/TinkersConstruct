package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * External logic for the ToolCore that handles mining calculations.
 */
public final class ToolMiningLogic {

  /**
   * Calculates the dig speed for the given blockstate
   *
   * @param stack the tool stack
   * @param blockState the block state to check
   * @return the dig speed
   */
  public float calcDigSpeed(ItemStack stack, BlockState blockState) {
    if(!stack.hasTag() || !(stack.getItem() instanceof ToolCore)) {
      return 1f;
    }

    ToolCore toolCore = (ToolCore) stack.getItem();
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return 0.3f;
    }

    if(!toolCore.isEffective(blockState)
      && !ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, blockState)
      && !hasRequiredHarvestLevel(stack, blockState)) {
      return 1f;
    }

    // calculate speed depending on stats
    float modifier = toolCore.getToolDefinition().getBaseStatDefinition().getMiningSpeedModifier();
    return tool.getStats().getMiningSpeed() * modifier;
  }

  /**
   * Checks if an item has the right harvest level of the correct type for the block.
   */
  public boolean hasRequiredHarvestLevel(ItemStack stack, BlockState state) {
    Block block = state.getBlock();

    // doesn't require a tool
    if(!state.getRequiresTool()) {
      return true;
    }

    ToolType type = block.getHarvestTool(state);
    int level = block.getHarvestLevel(state);

    return stack.getItem().getHarvestLevel(stack, type, null, state) >= level;
  }
}
