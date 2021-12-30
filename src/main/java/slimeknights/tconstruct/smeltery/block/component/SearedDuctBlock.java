package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.block.entity.component.DuctTileEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;

/** Filtering drain block, have to reimplement either inventory block logic or seared block logic unfortunately */
public class SearedDuctBlock extends InventoryBlock {
  public static final BooleanProperty ACTIVE = SearedBlock.IN_STRUCTURE;
  public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
  public SearedDuctBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new DuctTileEntity(pPos, pState);
  }

  /* Seared block interaction */

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.is(this)) {
      BlockEntityHelper.get(SmelteryComponentTileEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onRemove(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentTileEntity.updateNeighbors(world, pos, state);
  }


  /* Orientation */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(ACTIVE, FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }
}
