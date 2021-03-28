package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import java.util.Random;

// todo: evaluate this block
public class SlimeLeavesBlock extends LeavesBlock {

  private final SlimeGrassBlock.FoliageType foliageType;

  public SlimeLeavesBlock(Properties properties, SlimeGrassBlock.FoliageType foliageType) {
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
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    int i = getDistance(facingState) + 1;
    if (i != 1 || stateIn.get(DISTANCE) != i) {
      worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
    }

    return stateIn;
  }

  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    worldIn.setBlockState(pos, updateDistance(state, worldIn, pos), 3);
  }

  private static BlockState updateDistance(BlockState state, IWorld world, BlockPos pos) {
    int i = 7;

    BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
    for (Direction direction : Direction.values()) {
      mutableBlockPos.setPos(pos).move(direction);
      i = Math.min(i, getDistance(world.getBlockState(mutableBlockPos)) + 1);
      if (i == 1) {
        break;
      }
    }

    return state.with(DISTANCE, i);
  }

  private static int getDistance(BlockState neighbor) {
    if (TinkerTags.Blocks.SLIMY_LOGS.contains(neighbor.getBlock())) {
      return 0;
    } else {
      return neighbor.getBlock() instanceof SlimeLeavesBlock ? neighbor.get(DISTANCE) : 7;
    }
  }

  public SlimeGrassBlock.FoliageType getFoliageType() {
    return this.foliageType;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return updateDistance(this.getDefaultState().with(PERSISTENT, Boolean.TRUE), context.getWorld(), context.getPos());
  }

  @Override
  public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
    return this.isAir(state, world, pos) || state.isIn(BlockTags.LEAVES) || state.isIn(TinkerTags.Blocks.SLIMY_LEAVES);
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }
}
