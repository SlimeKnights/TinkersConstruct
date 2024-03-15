package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.block.entity.HeaterBlockEntity;

import javax.annotation.Nullable;

/**
 * Class for solid fuel heater for the melter
 */
public class HeaterBlock extends ControllerBlock {
  public HeaterBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new HeaterBlockEntity(pPos, pState);
  }

  @Override
  protected boolean canOpenGui(BlockState state) {
    return true;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state != null) {
      return state.setValue(IN_STRUCTURE, context.getLevel().getBlockState(context.getClickedPos().above()).is(TinkerTags.Blocks.HEATER_CONTROLLERS));
    }
    return null;
  }

  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.UP) {
      return state.setValue(IN_STRUCTURE, facingState.is(TinkerTags.Blocks.HEATER_CONTROLLERS));
    }
    return state;
  }

  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    if (state.getValue(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 14F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset);
    }
  }
}
