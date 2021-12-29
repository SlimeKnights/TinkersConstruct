package slimeknights.tconstruct.library.tools.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class TestToolItem extends ToolItem {

  public TestToolItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean isCorrectToolForDrops(BlockState state) {
    return state.getBlock() == Blocks.DIRT;
  }
}
