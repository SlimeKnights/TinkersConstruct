package slimeknights.tconstruct.tables.block;

import slimeknights.tconstruct.shared.block.TableBlock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TinkerTableBlock extends TableBlock implements ITinkerStationBlock {

  public TinkerTableBlock(Settings builder) {
    super(builder);
  }

  @Override
  public boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    return super.openGui(player, world, pos);
  }

}
