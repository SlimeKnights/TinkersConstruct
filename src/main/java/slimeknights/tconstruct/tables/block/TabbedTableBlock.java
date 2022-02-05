package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.shared.block.TableBlock;

public abstract class TabbedTableBlock extends TableBlock implements ITabbedBlock {

  public TabbedTableBlock(Properties builder) {
    super(builder);
  }

  @Override
  public boolean openGui(Player player, Level world, BlockPos pos) {
    return super.openGui(player, world, pos);
  }
}
