package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.tables.entity.FallingAnvilEntity;

import java.util.Random;

public class TinkersAnvilBlock extends TinkerStationBlock implements Fallable {
  private static final VoxelShape PART_BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
  private static final VoxelShape X_AXIS_AABB = Shapes.or(
    PART_BASE,
    Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
    Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
    Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
  private static final VoxelShape Z_AXIS_AABB = Shapes.or(
    PART_BASE,
    Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
    Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
    Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));

  public TinkersAnvilBlock(Properties builder, int slotCount) {
    super(builder, slotCount);
  }
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
  }

  @Override
  public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    pLevel.scheduleTick(pPos, this, this.getDelayAfterPlace());
  }

  @Override
  public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
    pLevel.scheduleTick(pCurrentPos, this, this.getDelayAfterPlace());
    return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
  }

  @Override
  public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
    if (FallingBlock.isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
      FallingAnvilEntity fallingblockentity = FallingAnvilEntity.fall(pLevel, pPos, pState);
      fallingblockentity.setHurtsEntities(2.0F, 40);
    }
  }

  public int getDelayAfterPlace() {
    return 2;
  }

  @Override
  public void onLand(Level p_48793_, BlockPos p_48794_, BlockState p_48795_, BlockState p_48796_, FallingBlockEntity p_48797_) {
    if (!p_48797_.isSilent()) {
      p_48793_.levelEvent(1031, p_48794_, 0);
    }

  }

  @Override
  public void onBrokenAfterFall(Level pLevel, BlockPos pPos, FallingBlockEntity pFallingBlock) {
    if (!pFallingBlock.isSilent()) {
      pLevel.levelEvent(1029, pPos, 0);
    }

  }

  @Override
  public DamageSource getFallDamageSource() {
    return DamageSource.ANVIL;
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!isMoving) {
      super.onRemove(state, level, pos, newState, isMoving);
    }
    if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
      level.removeBlockEntity(pos);
    }
  }
}
