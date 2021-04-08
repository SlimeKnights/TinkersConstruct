package slimeknights.tconstruct.shared.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.block.IMultipartConnectedBlock;
import slimeknights.mantle.client.model.connected.ConnectedModelRegistry;

import java.util.EnumMap;

public class ClearGlassPaneBlock extends PaneBlock implements IMultipartConnectedBlock {

  private static final EnumMap<Direction,BooleanProperty> DIRECTIONS;
  static {
    DIRECTIONS = new EnumMap<>(Direction.class);
    DIRECTIONS.put(Direction.NORTH, NORTH);
    DIRECTIONS.put(Direction.EAST, EAST);
    DIRECTIONS.put(Direction.SOUTH, SOUTH);
    DIRECTIONS.put(Direction.WEST, WEST);
  }

  public ClearGlassPaneBlock(Settings builder) {
    super(builder);
    this.setDefaultState(IMultipartConnectedBlock.defaultConnections(this.getDefaultState()));
  }

  @Override
  protected void appendProperties(Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    IMultipartConnectedBlock.fillStateContainer(builder);
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.getStateForNeighborUpdate(stateIn, facing, facingState, world, currentPos, facingPos);
    return getConnectionUpdate(state, facing, facingState);
  }

  @Override
  public boolean connects(BlockState state, BlockState neighbor) {
    return ConnectedModelRegistry.getPredicate("pane").test(state, neighbor);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
    // cull top and bottom if all props that we have are contained in the above or below
    if (adjacentBlockState.getBlock() == this && side.getAxis().isVertical()) {
      for (Direction dir : Type.HORIZONTAL) {
        BooleanProperty prop = DIRECTIONS.get(dir);
        if (state.get(prop) && !adjacentBlockState.get(prop)) {
          return false;
        }
      }
      return true;
    }
    return super.isSideInvisible(state, adjacentBlockState, side);
  }
}
