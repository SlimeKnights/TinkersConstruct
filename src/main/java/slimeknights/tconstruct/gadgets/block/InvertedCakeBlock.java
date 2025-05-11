package slimeknights.tconstruct.gadgets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Implementation of ichor cake, which is inverted and provides a rocket effect */
public class InvertedCakeBlock extends FoodCakeBlock {
  protected static final VoxelShape[] SHAPE_BY_BITE = {
    Block.box( 1, 8, 1, 15, 16, 15),
    Block.box( 3, 8, 1, 15, 16, 15),
    Block.box( 5, 8, 1, 15, 16, 15),
    Block.box( 7, 8, 1, 15, 16, 15),
    Block.box( 9, 8, 1, 15, 16, 15),
    Block.box(11, 8, 1, 15, 16, 15),
    Block.box(13, 8, 1, 15, 16, 15)
  };

  public InvertedCakeBlock(Properties properties, FoodProperties food, EffectCombination combination) {
    super(properties, food, combination);
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE_BY_BITE[pState.getValue(BITES)];
  }

  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
    if (facing == Direction.UP && !state.canSurvive(level, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    return state;
  }

  @Override
  public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
    return pLevel.getBlockState(pPos.above()).isSolid();
  }
}
