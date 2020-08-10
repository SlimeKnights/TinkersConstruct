package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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

  public ClearGlassPaneBlock(Properties builder) {
    super(builder);
    this.setDefaultState(IMultipartConnectedBlock.defaultConnections(this.getDefaultState()));
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    IMultipartConnectedBlock.fillStateContainer(builder);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    return getConnectionUpdate(state, facing, facingState);
  }

  @Override
  public boolean connects(BlockState state, BlockState neighbor) {
    return ConnectedModelRegistry.getPredicate("pane").test(state, neighbor);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
    // cull top and bottom if all props that we have are contained in the above or below
    if (adjacentBlockState.getBlock() == this && side.getAxis().isVertical()) {
      for (Direction dir : Plane.HORIZONTAL) {
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
