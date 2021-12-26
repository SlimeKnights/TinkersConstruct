package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Block orientable in 4 directions
 */
public class OrientableBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public OrientableBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }
}
