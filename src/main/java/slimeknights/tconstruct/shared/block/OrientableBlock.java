package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

/**
 * Block orientable in 4 directions
 */
public class OrientableBlock extends Block {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public OrientableBlock(Settings properties) {
    super(properties);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, BlockRotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.get(FACING)));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }
}
