package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import slimeknights.mantle.block.IMultipartConnectedBlock;
import slimeknights.mantle.client.model.connected.ConnectedModelRegistry;

import net.minecraft.block.AbstractBlock.Properties;

public class ClearGlassPaneBlock extends BetterPaneBlock implements IMultipartConnectedBlock {
  public ClearGlassPaneBlock(Properties builder) {
    super(builder);
    this.registerDefaultState(IMultipartConnectedBlock.defaultConnections(this.defaultBlockState()));
  }

  @Override
  protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    IMultipartConnectedBlock.fillStateContainer(builder);
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
    return getConnectionUpdate(state, facing, facingState);
  }

  @Override
  public boolean connects(BlockState state, BlockState neighbor) {
    return ConnectedModelRegistry.getPredicate("pane").test(state, neighbor);
  }
}
