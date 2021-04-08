package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import java.util.Random;

// todo: evaluate this block
public class SlimeLeavesBlock extends LeavesBlock {

  private final FoliageType foliageType;

  public SlimeLeavesBlock(Settings properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  /**
   * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
   * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
   * returns its solidified counterpart.
   * Note that this method should ideally consider only the specific face passed in.
   */
  @Override
  public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
    int i = getDistanceFromLog(facingState) + 1;
    if (i != 1 || stateIn.get(DISTANCE) != i) {
      worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
    }

    return stateIn;
  }

  @Override
  public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    worldIn.setBlockState(pos, updateDistanceFromLogs(state, worldIn, pos), 3);
  }

  private static BlockState updateDistanceFromLogs(BlockState state, WorldAccess world, BlockPos pos) {
    int i = 7;

    BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
    for (Direction direction : Direction.values()) {
      mutableBlockPos.set(pos).move(direction);
      i = Math.min(i, getDistanceFromLog(world.getBlockState(mutableBlockPos)) + 1);
      if (i == 1) {
        break;
      }
    }

    return state.with(DISTANCE, i);
  }

  private static int getDistanceFromLog(BlockState neighbor) {
    if (TinkerTags.Blocks.SLIMY_LOGS.contains(neighbor.getBlock())) {
      return 0;
    } else {
      return neighbor.getBlock() instanceof SlimeLeavesBlock ? neighbor.get(DISTANCE) : 7;
    }
  }

  public FoliageType getFoliageType() {
    return this.foliageType;
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    return updateDistanceFromLogs(this.getDefaultState().with(PERSISTENT, Boolean.TRUE), context.getWorld(), context.getBlockPos());
  }

  @Override
  public boolean canBeReplacedByLeaves(BlockState state, WorldView world, BlockPos pos) {
    return this.isAir(state, world, pos) || state.isIn(BlockTags.LEAVES) || state.isIn(TinkerTags.Blocks.SLIMY_LEAVES);
  }

  @Override
  public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.addStacksForDisplay(group, items);
    }
  }
}
