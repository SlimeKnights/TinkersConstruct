package slimeknights.tconstruct.library.tools.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public class TestToolItem extends ToolItem {

  public TestToolItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return state.getBlock() == Blocks.DIRT;
  }
}
