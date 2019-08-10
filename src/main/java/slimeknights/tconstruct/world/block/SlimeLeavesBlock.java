package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;

public class SlimeLeavesBlock extends LeavesBlock {

  private final SlimeGrassBlock.FoliageType foliageType;

  public SlimeLeavesBlock(SlimeGrassBlock.FoliageType foliageType) {
    super(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.3F).tickRandomly().sound(SoundType.PLANT));
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
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    worldIn.setBlockState(pos, updateDistance(state, worldIn, pos), 3);
  }

  private static BlockState updateDistance(BlockState state, IWorld world, BlockPos pos) {
    int i = 7;

    try (BlockPos.PooledMutableBlockPos mutableBlockPos = BlockPos.PooledMutableBlockPos.retain()) {
      for (Direction direction : Direction.values()) {
        mutableBlockPos.setPos(pos).move(direction);
        i = Math.min(i, getDistance(world.getBlockState(mutableBlockPos)) + 1);
        if (i == 1) {
          break;
        }
      }
    }

    return state.with(DISTANCE, Integer.valueOf(i));
  }

  private static int getDistance(BlockState neighbor) {
    if (TinkerWorld.SLIMY_LOGS.contains(neighbor.getBlock())) {
      return 0;
    }
    else {
      return neighbor.getBlock() instanceof SlimeLeavesBlock ? neighbor.get(DISTANCE) : 7;
    }
  }

  /**
   * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
   * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
   */
  @Override
  public BlockRenderLayer getRenderLayer() {
    return LeavesBlock.renderTranslucent ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
  }

  public SlimeGrassBlock.FoliageType getFoliageType() {
    return this.foliageType;
  }

  @Override
  public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
    return false;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return updateDistance(this.getDefaultState().with(PERSISTENT, Boolean.TRUE), context.getWorld(), context.getPos());
  }

  @Override
  public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
    return this.isAir(state, world, pos) || state.isIn(BlockTags.LEAVES) || state.isIn(TinkerWorld.SLIMY_LEAVES);
  }
}
