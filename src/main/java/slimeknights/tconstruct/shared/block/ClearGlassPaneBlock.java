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

import java.util.EnumMap;

import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_DOWN;
import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_EAST;
import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_NORTH;
import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_SOUTH;
import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_UP;
import static slimeknights.mantle.block.ConnectedTextureBlock.CONNECTED_WEST;

public class ClearGlassPaneBlock extends PaneBlock {

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
    this.setDefaultState(this.getDefaultState()
                             .with(CONNECTED_DOWN,  Boolean.FALSE)
                             .with(CONNECTED_EAST,  Boolean.FALSE)
                             .with(CONNECTED_NORTH, Boolean.FALSE)
                             .with(CONNECTED_SOUTH, Boolean.FALSE)
                             .with(CONNECTED_UP,    Boolean.FALSE)
                             .with(CONNECTED_WEST,  Boolean.FALSE));
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    BlockState state = super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    return state.with(CONNECTED_DOWN,  this.isSideConnectable(state, world, currentPos, Direction.DOWN))
                .with(CONNECTED_EAST,  this.isSideConnectable(state, world, currentPos, Direction.EAST))
                .with(CONNECTED_NORTH, this.isSideConnectable(state, world, currentPos, Direction.NORTH))
                .with(CONNECTED_SOUTH, this.isSideConnectable(state, world, currentPos, Direction.SOUTH))
                .with(CONNECTED_UP,    this.isSideConnectable(state, world, currentPos, Direction.UP))
                .with(CONNECTED_WEST,  this.isSideConnectable(state, world, currentPos, Direction.WEST));
  }

  /**
   * Checks if the given side can connect
   * @param state  State to check
   * @param world  World instance
   * @param pos    Block position
   * @param side   Side to check
   * @return  True if the side can connect
   */
  private boolean isSideConnectable(BlockState state, IWorld world, BlockPos pos, Direction side) {
    BlockState connected = world.getBlockState(pos.offset(side));
    return this.canConnect(state, connected);
  }

  /**
   * Checks if this state can connect to the neighboring state
   * @param original   Current block state
   * @param connected  Connected block state
   * @return  True if the block can connect
   */
  protected boolean canConnect(BlockState original, BlockState connected) {
    // must be the same block, and either both blocks must be center only, or neither are center only
    return original.getBlock() == connected.getBlock()
           && (original.get(NORTH) || original.get(EAST) || original.get(SOUTH) || original.get(WEST)) == (connected.get(NORTH) || connected.get(EAST) || connected.get(SOUTH) || connected.get(WEST));
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
