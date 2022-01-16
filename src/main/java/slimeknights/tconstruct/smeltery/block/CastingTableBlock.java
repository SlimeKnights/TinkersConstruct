package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nullable;

public class CastingTableBlock extends AbstractCastingBlock {

  private static final VoxelShape SHAPE = Shapes.join(
    Shapes.block(),
    Shapes.or(
      Block.box(4.0D, 0.0D, 0.0D, 12.0D, 10.0D, 16.0D),
      Block.box(0.0D, 0.0D, 4.0D, 16.0D, 10.0D, 12.0D),
      Block.box(1.0D, 15.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    ), BooleanOp.ONLY_FIRST);

  public CastingTableBlock(Properties builder, boolean requireCast) {
    super(builder, requireCast);
  }

  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new CastingBlockEntity.Table(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> check) {
    return CastingBlockEntity.getTicker(pLevel, check, TinkerSmeltery.table.get());
  }
}
