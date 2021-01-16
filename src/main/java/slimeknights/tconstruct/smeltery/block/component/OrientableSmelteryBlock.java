package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Shared logic for smeltery blocks with four directions to face */
public class OrientableSmelteryBlock extends SearedBlock {
  public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

  private final Supplier<? extends SmelteryComponentTileEntity> tileEntity;
  public OrientableSmelteryBlock(Properties properties, Supplier<? extends SmelteryComponentTileEntity> tileEntity) {
    super(properties);
    this.tileEntity = tileEntity;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return tileEntity.get();
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.with(FACING, mirror.mirror(state.get(FACING)));
  }
}
