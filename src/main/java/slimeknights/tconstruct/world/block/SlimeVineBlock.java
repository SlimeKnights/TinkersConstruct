package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Logic for slime vines. Have three stages unlike vanilla vines, and only grow down
 */
public class SlimeVineBlock extends VineBlock {
  public static final EnumProperty<VineStage> STAGE = EnumProperty.create("stage", VineStage.class);

  @Getter
  private final SlimeType foliage;
  public SlimeVineBlock(Properties properties, SlimeType foliage) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(STAGE, VineStage.START));
    this.foliage = foliage;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(STAGE);
  }

  @Override
  public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
    if (worldIn.isClientSide) {
      return;
    }

    // if its only the top piece, start growing down a side
    if (hasNoHorizontalSides(state)) {
      // randomly choose sides to add
      BlockState newState = state;
      boolean onLeaves = worldIn.getBlockState(pos.above()).is(TinkerTags.Blocks.SLIMY_LEAVES);
      for (Direction side : Direction.Plane.HORIZONTAL) {
        // must be hanging from leaves or have a valid side
        if ((onLeaves || isAcceptableNeighbour(worldIn, pos.relative(side), side)) && random.nextInt(6) == 0) {
          newState = newState.setValue(getPropertyForFace(side), true);
        }
      }
      // if there was a change, update
      if (newState != state) {
        worldIn.setBlock(pos, newState, 3);
      }
      // normal side growth
    } else if (random.nextInt(4) == 0) {
      grow(worldIn, random, pos, state);
    }
  }

  /**
   * Grows the vine one stage
   * @param worldIn World instance
   * @param random  Random instance
   * @param pos     Pos
   * @param state   State
   */
  public void grow(LevelAccessor worldIn, RandomSource random, BlockPos pos, BlockState state) {
    // no growing ends
    if (hasNoHorizontalSides(state) || state.getValue(STAGE) == VineStage.END) {
      return;
    }

    // start growing down if we have existing sides
    BlockPos below = pos.below();
    if (worldIn.isEmptyBlock(below)) {
      // free floating position? possibly move to next stage
      if (freeFloating(worldIn, pos, state)) {
        // force transition after 3 vines
        int i = 1;
        VineStage stage = state.getValue(STAGE);
        for (; i < 3; i++) {
          BlockState above = worldIn.getBlockState(pos.above(i));
          if (!above.is(this) || above.getValue(STAGE) != stage) {
            break;
          }
        }
        if (i > 2 || random.nextInt(2) == 0) {
          state = state.cycle(STAGE);
        }
      }
      // place new vine at position
      worldIn.setBlock(below, state.setValue(UP, false), 3);
    }
  }

  @Override
  public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
    return hasSides(updateConnections(state, worldIn, pos));
  }

  /**
   * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
   * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
   * returns its solidified counterpart.
   * Note that this method should ideally consider only the specific face passed in.
   */
  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.DOWN) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
    BlockState updated = updateConnections(stateIn, worldIn, currentPos);
    return !hasSides(updated) ? Blocks.AIR.defaultBlockState() : updated;
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState currState = world.getBlockState(pos);
    boolean isVine = currState.is(this);
    BlockState vineState = isVine ? currState : this.defaultBlockState();

    // try each direction, see if we can place on that side
    for (Direction direction : context.getNearestLookingDirections()) {
      if (direction != Direction.DOWN) {
        // if no existing vine on the side and its valid, place there
        BooleanProperty prop = getPropertyForFace(direction);
        if (!(isVine && currState.getValue(prop)) && this.hasAttachment(world, pos, direction)) {
          return vineState.setValue(prop, true);
        }
      }
    }
    // no sides worked? just say it was fine if there is a vine here
    return isVine ? vineState : null;
  }

  /*
   * Helpers
   */

  /**
   * Checks if the given vines have at least one face
   * @param state  State to check
   * @return  True if there is at least one face
   */
  private static boolean hasSides(BlockState state) {
    for (BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
      if (state.getValue(booleanproperty)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given vine has no sides
   * @param state  State to check
   * @return  True if there are no sides
   */
  private static boolean hasNoHorizontalSides(BlockState state) {
    for (Direction side : Direction.Plane.HORIZONTAL) {
      if (state.getValue(getPropertyForFace(side))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the given vine is free floating
   * @param world  World instance
   * @param pos    Vine position
   * @param state  Vine state
   * @return  True if free floating, false if any side is "connected" to a block
   */
  private static boolean freeFloating(LevelAccessor world, BlockPos pos, BlockState state) {
    for (Direction side : Direction.Plane.HORIZONTAL) {
      if (state.getValue(getPropertyForFace(side)) && isAcceptableNeighbour(world, pos.relative(side), side)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets the updated connections based on neighbors
   * @param state  State to check
   * @param world  World
   * @param pos    Position of vines
   * @return  Updated connections
   */
  private BlockState updateConnections(BlockState state, BlockGetter world, BlockPos pos) {
    BlockPos up = pos.above();
    if (state.getValue(UP)) {
      state = state.setValue(UP, isAcceptableNeighbour(world, up, Direction.UP));
    }
    // update each side with whether it can be supported
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      BooleanProperty prop = getPropertyForFace(direction);
      if (state.getValue(prop)) {
        state = state.setValue(prop, hasAttachment(world, pos, direction));
      }
    }

    return state;
  }

  /**
   * Checks if the vine side can stay
   * @param world  World
   * @param pos    Pos to check
   * @param side   Vine side to check
   * @return  True if it can hold
   */
  private boolean hasAttachment(BlockGetter world, BlockPos pos, Direction side) {
    // down has no attachments
    if (side == Direction.DOWN) {
      return false;
    }
    // remaining direction must be supported
    BlockPos offset = pos.relative(side);
    if (isAcceptableNeighbour(world, offset, side)) {
      return true;
    }
    // if not supported, try finding a vine or leaves there
    BlockState upState = world.getBlockState(pos.above());
    if (upState.is(TinkerTags.Blocks.SLIMY_LEAVES)) {
      return true;
    }
    // otherwise, if not up try a supporting vine (must not be end to support)
    return side != Direction.UP && (upState.is(this) && upState.getValue(PROPERTY_BY_DIRECTION.get(side)) && upState.getValue(STAGE) != VineStage.END);
  }

  /** Stages of the vine, cycles through them as it grows */
  public enum VineStage implements StringRepresentable {
    START,
    MIDDLE,
    END;

    @Override
    public String getSerializedName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
