package slimeknights.tconstruct.tools;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.List;

public class TestTool extends ToolCore {

  public TestTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean isEffective(BlockState state) {
    return state.getBlock() == Blocks.DIRT;
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {

  }
}
