package slimeknights.tconstruct.world.block;

import lombok.Getter;
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
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.Random;

// todo: evaluate this block
import net.minecraft.block.AbstractBlock.Properties;

public class SlimeLeavesBlock extends LeavesBlock {
  @Getter
  private final SlimeType foliageType;
  public SlimeLeavesBlock(Properties properties, SlimeType foliageType) {
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
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    int i = getDistance(facingState) + 1;
    if (i != 1 || stateIn.getValue(DISTANCE) != i) {
      worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
    }

    return stateIn;
  }

  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    worldIn.setBlock(pos, updateDistance(state, worldIn, pos), 3);
  }

  private static BlockState updateDistance(BlockState state, IWorld world, BlockPos pos) {
    int i = 7;

    BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
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
    if (TinkerTags.Blocks.SLIMY_LOGS.contains(neighbor.getBlock())) {
      return 0;
    } else {
      return neighbor.getBlock() instanceof SlimeLeavesBlock ? neighbor.getValue(DISTANCE) : 7;
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return updateDistance(this.defaultBlockState().setValue(PERSISTENT, Boolean.TRUE), context.getLevel(), context.getClickedPos());
  }

  @Override
  public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
    return this.isAir(state, world, pos) || state.is(BlockTags.LEAVES) || state.is(TinkerTags.Blocks.SLIMY_LEAVES);
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != SlimeType.ICHOR) {
      super.fillItemCategory(group, items);
    }
  }
}
