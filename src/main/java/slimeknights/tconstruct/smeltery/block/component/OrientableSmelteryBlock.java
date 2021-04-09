package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

/** Shared logic for smeltery blocks with four directions to face */
public class OrientableSmelteryBlock extends SearedBlock {
  public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

  private final Supplier<? extends SmelteryComponentTileEntity> tileEntity;
  public OrientableSmelteryBlock(Settings properties, Supplier<? extends SmelteryComponentTileEntity> tileEntity) {
    super(properties);
    this.tileEntity = tileEntity;
  }

//  @Override
//  public BlockEntity createTileEntity(BlockState state, BlockView world) {
//    return tileEntity.get();
//  }

  @Override
  protected void appendProperties(StateManager.Builder<Block,BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.with(FACING, mirror.apply(state.get(FACING)));
  }
}
