package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BeaconBaseBlock extends Block {

  public BeaconBaseBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
    return true;
  }
}
