package slimeknights.tconstruct.tools.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class MattockTool extends ToolCore {
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new AOEToolHarvestLogic(1, 1, 1) {
    @Override
    public float calcDigSpeed(ItemStack stack, BlockState blockState) {
      if(!stack.hasTag()) {
        return 1f;
      }
      // TODO: general modifiable
      ToolStack tool = ToolStack.from(stack);
      if (tool.isBroken()) {
        return 0.3f;
      }
      if (!isEffective(tool, stack, blockState)) {
        return 1f;
      }

      // slower when a non-shovel block
      float speed = tool.getStats().getMiningSpeed();
      if (!blockState.isToolEffective(ToolType.SHOVEL)) {
        speed *= 0.6f;
      }
      return speed;
    }
  };

  public MattockTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return AxeTool.EFFECTIVE_MATERIALS.contains(state.getMaterial())
           || ExcavatorTool.EFFECTIVE_MATERIALS.contains(state.getMaterial())
           || ShovelItem.EFFECTIVE_ON.contains(state.getBlock())
           || AxeItem.EFFECTIVE_ON_BLOCKS.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return getToolHarvestLogic().tillBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN);
  }
}
