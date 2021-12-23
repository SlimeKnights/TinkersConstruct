package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

/**
 * Logic for slime vines. Have three stages unlike vanilla vines, and only grow down
 */
public class SlimeVineBlock extends VineBlock {
  public static final EnumProperty<VineStage> STAGE = EnumProperty.create("stage", VineStage.class);

  @Getter
  private final SlimeType foliage;
  public SlimeVineBlock(Properties properties, SlimeType foliage) {
    super(properties);
    this.setDefaultState(this.getDefaultState().with(STAGE, VineStage.START));
    this.foliage = foliage;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(STAGE);
  }

  @Override
  public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    if (worldIn.isRemote) {
      return;
    }

    // if its only the top piece, start growing down a side
    if (hasNoHorizontalSides(state)) {
      // randomly choose sides to add
      BlockState newState = state;
      boolean onLeaves = worldIn.getBlockState(pos.up()).isIn(TinkerTags.Blocks.SLIMY_LEAVES);
      for (Direction side : Direction.Plane.HORIZONTAL) {
        // must be hanging from leaves or have a valid side
        if ((onLeaves || canAttachTo(worldIn, pos.offset(side), side)) && random.nextInt(6) == 0) {
          newState = newState.with(getPropertyFor(side), true);
        }
      }
      // if there was a change, update
      if (newState != state) {
        worldIn.setBlockState(pos, newState, 3);
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
  public void grow(IWorld worldIn, Random random, BlockPos pos, BlockState state) {
    // no growing ends
    if (hasNoHorizontalSides(state) || state.get(STAGE) == VineStage.END) {
      return;
    }

    // start growing down if we have existing sides
    BlockPos below = pos.down();
    if (worldIn.isAirBlock(below)) {
      // free floating position? possibly move to next stage
      if (freeFloating(worldIn, pos, state)) {
        // force transition after 3 vines
        int i = 1;
        VineStage stage = state.get(STAGE);
        for (; i < 3; i++) {
          BlockState above = worldIn.getBlockState(pos.up(i));
          if (!above.matchesBlock(this) || above.get(STAGE) != stage) {
            break;
          }
        }
        if (i > 2 || random.nextInt(2) == 0) {
          state = state.cycleValue(STAGE);
        }
      }
      // place new vine at position
      worldIn.setBlockState(below, state.with(UP, false), 3);
    }
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return hasSides(updateConnections(state, worldIn, pos));
  }

  /**
   * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
   * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
   * returns its solidified counterpart.
   * Note that this method should ideally consider only the specific face passed in.
   */
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.DOWN) {
      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
    BlockState updated = updateConnections(stateIn, worldIn, currentPos);
    return !hasSides(updated) ? Blocks.AIR.getDefaultState() : updated;
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    BlockState currState = world.getBlockState(pos);
    boolean isVine = currState.matchesBlock(this);
    BlockState vineState = isVine ? currState : this.getDefaultState();

    // try each direction, see if we can place on that side
    for (Direction direction : context.getNearestLookingDirections()) {
      if (direction != Direction.DOWN) {
        // if no existing vine on the side and its valid, place there
        BooleanProperty prop = getPropertyFor(direction);
        if (!(isVine && currState.get(prop)) && this.hasAttachment(world, pos, direction)) {
          return vineState.with(prop, true);
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
    for (BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values()) {
      if (state.get(booleanproperty)) {
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
      if (state.get(getPropertyFor(side))) {
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
  private static boolean freeFloating(IWorld world, BlockPos pos, BlockState state) {
    for (Direction side : Direction.Plane.HORIZONTAL) {
      if (state.get(getPropertyFor(side)) && canAttachTo(world, pos.offset(side), side)) {
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
  private BlockState updateConnections(BlockState state, IBlockReader world, BlockPos pos) {
    BlockPos up = pos.up();
    if (state.get(UP)) {
      state = state.with(UP, canAttachTo(world, up, Direction.UP));
    }
    // update each side with whether it can be supported
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      BooleanProperty prop = getPropertyFor(direction);
      if (state.get(prop)) {
        state = state.with(prop, hasAttachment(world, pos, direction));
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
  private boolean hasAttachment(IBlockReader world, BlockPos pos, Direction side) {
    // down has no attachments
    if (side == Direction.DOWN) {
      return false;
    }
    // remaining direction must be supported
    BlockPos offset = pos.offset(side);
    if (canAttachTo(world, offset, side)) {
      return true;
    }
    // if not supported, try finding a vine or leaves there
    BlockState upState = world.getBlockState(pos.up());
    if (upState.isIn(TinkerTags.Blocks.SLIMY_LEAVES)) {
      return true;
    }
    // otherwise, if not up try a supporting vine (must not be end to support)
    return side != Direction.UP && (upState.matchesBlock(this) && upState.get(FACING_TO_PROPERTY_MAP.get(side)) && upState.get(STAGE) != VineStage.END);
  }

  /** Stages of the vine, cycles through them as it grows */
  public enum VineStage implements IStringSerializable {
    START,
    MIDDLE,
    END;

    @Override
    public String getString() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
