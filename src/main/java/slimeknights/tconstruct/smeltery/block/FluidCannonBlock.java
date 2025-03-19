package slimeknights.tconstruct.smeltery.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.entity.FluidCannonBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FluidCannonBlockEntity.IFluidCannon;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.TRIGGERED;

/** Tank block which also shoots a fluid */
public class FluidCannonBlock extends SearedTankBlock implements IFluidCannon {
  @Getter
  private final float power;
  @Getter
  private final float velocity;
  @Getter
  private final float inaccuracy;
  public FluidCannonBlock(Properties properties, int capacity, float power, float velocity, float inaccuracy) {
    super(properties, capacity);
    this.power = power;
    this.velocity = velocity;
    this.inaccuracy = inaccuracy;
    this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(FACING, TRIGGERED);
  }


  /* Facing */

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext pContext) {
    return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState pState, Rotation pRotation) {
    return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState pState, Mirror pMirror) {
    return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
  }


  /* Triggering */

  @Override
  @Nullable
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FluidCannonBlockEntity(pos, state, this);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
    boolean wasTriggered = state.getValue(TRIGGERED);
    if (hasSignal && !wasTriggered) {
      level.scheduleTick(pos, this, 4);
      level.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_INVISIBLE);
    } else if (!hasSignal && wasTriggered) {
      level.setBlock(pos, state.setValue(TRIGGERED, false), Block.UPDATE_INVISIBLE);
    }
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (level.getBlockEntity(pos) instanceof FluidCannonBlockEntity cannon) {
      cannon.shoot(state, level, random);
    }
  }
}
