package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.TinkerTags;

public class SlimeLeavesBlock extends LeavesBlock {
  @Getter
  private final FoliageType foliageType;
  public SlimeLeavesBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    int i = getDistance(facingState) + 1;
    if (i != 1 || stateIn.getValue(DISTANCE) != i) {
      worldIn.scheduleTick(currentPos, this, 1);
    }

    return stateIn;
  }

  @Override
  public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
    worldIn.setBlock(pos, updateDistance(state, worldIn, pos), 3);
  }

  private static BlockState updateDistance(BlockState state, LevelAccessor world, BlockPos pos) {
    int i = 7;

    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
    for (Direction direction : Direction.values()) {
      mutableBlockPos.set(pos).move(direction);
      i = Math.min(i, getDistance(world.getBlockState(mutableBlockPos)) + 1);
      if (i == 1) {
        break;
      }
    }

    return state.setValue(DISTANCE, i);
  }

  private static int getDistance(BlockState neighbor) {
    if (neighbor.is(TinkerTags.Blocks.SLIMY_LOGS)) {
      return 0;
    } else {
      return neighbor.getBlock() instanceof SlimeLeavesBlock ? neighbor.getValue(DISTANCE) : 7;
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return updateDistance(this.defaultBlockState().setValue(PERSISTENT, Boolean.TRUE), context.getLevel(), context.getClickedPos());
  }

// TODO: needed?
//  @Override
//  public boolean canBeReplacedByLeaves(BlockState state, LevelReader world, BlockPos pos) {
//    return this.isAir(state, world, pos) || state.is(BlockTags.LEAVES) || state.is(TinkerTags.Blocks.SLIMY_LEAVES);
//  }
}
