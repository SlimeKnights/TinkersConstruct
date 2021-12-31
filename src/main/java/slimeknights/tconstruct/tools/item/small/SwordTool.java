package slimeknights.tconstruct.tools.item.small;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolItem;

/** Tool with sword harvest and creative block breaking prevention */
public class SwordTool extends ToolItem {
  public SwordTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
    return !player.isCreative();
  }
}
