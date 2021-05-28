package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumMap;

/** Pane block with sensible culling */
public class BetterPaneBlock extends PaneBlock {
  public static final EnumMap<Direction,BooleanProperty> DIRECTIONS;
  static {
    DIRECTIONS = new EnumMap<>(Direction.class);
    DIRECTIONS.put(Direction.NORTH, NORTH);
    DIRECTIONS.put(Direction.EAST, EAST);
    DIRECTIONS.put(Direction.SOUTH, SOUTH);
    DIRECTIONS.put(Direction.WEST, WEST);
  }

  public BetterPaneBlock(Properties builder) {
    super(builder);
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
