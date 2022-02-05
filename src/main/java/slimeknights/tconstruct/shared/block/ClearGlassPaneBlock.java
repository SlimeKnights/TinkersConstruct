package slimeknights.tconstruct.shared.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import slimeknights.mantle.block.IMultipartConnectedBlock;
import slimeknights.mantle.client.model.connected.ConnectedModelRegistry;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
    return getConnectionUpdate(state, facing, facingState);
  }

  @Override
  public boolean connects(BlockState state, BlockState neighbor) {
    return ConnectedModelRegistry.getPredicate("pane").test(state, neighbor);
  }
}
