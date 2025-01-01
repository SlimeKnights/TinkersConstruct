package slimeknights.tconstruct.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.common.TinkerTags;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.world.level.block.MangrovePropaguleBlock.AGE;
import static net.minecraft.world.level.block.MangrovePropaguleBlock.SHAPE_PER_AGE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HANGING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/** Recreation of {@link net.minecraft.world.level.block.MangrovePropaguleBlock} to swap out the tree grower. */
public class SlimePropaguleBlock extends SlimeSaplingBlock {
  public SlimePropaguleBlock(AbstractTreeGrower treeIn, FoliageType foliageType, Properties properties) {
    super(treeIn, foliageType, properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(AGE, 0).setValue(WATERLOGGED, false).setValue(HANGING, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> pBuilder) {
    pBuilder.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockPlaceContext pContext) {
    FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
    boolean flag = fluidstate.getType() == Fluids.WATER;
    return Objects.requireNonNull(super.getStateForPlacement(pContext))
                  .setValue(WATERLOGGED, flag)
                  .setValue(AGE, 4);
  }


  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    Vec3 vec3 = pState.getOffset(pLevel, pPos);
    VoxelShape voxelshape;
    if (pState.getValue(HANGING)) {
      voxelshape = SHAPE_PER_AGE[pState.getValue(AGE)];
    } else {
      voxelshape = SHAPE;
    }

    return voxelshape.move(vec3.x, vec3.y, vec3.z);
  }

  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return state.getValue(HANGING)
           ? level.getBlockState(pos.above()).is(TinkerTags.Blocks.SLIMY_LEAVES)
           : super.canSurvive(state, level, pos);
  }

  @Override
  public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
    if (pState.getValue(WATERLOGGED)) {
      pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
    }
    return pFacing == Direction.UP && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
  }

  @SuppressWarnings("deprecation")
  @Override
  public FluidState getFluidState(BlockState pState) {
    return pState.getValue(WATERLOGGED)
           ? Fluids.WATER.getSource(false)
           : super.getFluidState(pState);
  }

  @Override
  public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
    if (!pState.getValue(HANGING)) {
      if (pRandom.nextInt(7) == 0) {
        this.advanceTree(pLevel, pPos, pState, pRandom);
      }

    } else {
      if (pState.getValue(AGE) != 4) {
        pLevel.setBlock(pPos, pState.cycle(AGE), 2);
      }

    }
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState state, boolean pIsClient) {
    return !state.getValue(HANGING) || state.getValue(AGE) != 4;
  }

  @Override
  public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
    return pState.getValue(HANGING)
           ? pState.getValue(AGE) != 4
           : super.isBonemealSuccess(pLevel, pRandom, pPos, pState);
  }

  @Override
  public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
    if (pState.getValue(HANGING) && pState.getValue(AGE) != 4) {
      pLevel.setBlock(pPos, pState.cycle(AGE), 2);
    } else {
      super.performBonemeal(pLevel, pRandom, pPos, pState);
    }
  }
}
