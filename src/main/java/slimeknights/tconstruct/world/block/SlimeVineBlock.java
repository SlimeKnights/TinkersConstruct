package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.blocks.WorldBlocks;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

// todo: evaluate block
public class SlimeVineBlock extends VineBlock {

  private final SlimeGrassBlock.FoliageType foliage;
  private final VineStage vineStage;

  public SlimeVineBlock(Properties properties, SlimeGrassBlock.FoliageType foliage, VineStage vineStage) {
    super(properties);
    this.foliage = foliage;
    this.vineStage = vineStage;
  }

  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    if (!worldIn.isRemote) {
      if (random.nextInt(4) == 0) {
        this.grow(worldIn, random, pos, state);
      }
    }
  }

  public void grow(IWorld worldIn, Random rand, BlockPos pos, BlockState state) {
    // end parts don't grow
    if (this.getStateFromStage() == null) {
      return;
    }

    // we only grow down
    BlockPos below = pos.down();
    if (worldIn.isAirBlock(below)) {
      // free floating position?
      if (this.freeFloating(worldIn, pos, state)) {
        // at most 3 middle parts
        int i = 0;
        while (worldIn.getBlockState(pos.up(i)).getBlock() == this) {
          i++;
        }

        if (i > 2 || rand.nextInt(2) == 0) {
          state = this.getStateFromStage().getDefaultState().with(NORTH, state.get(NORTH)).with(EAST, state.get(EAST)).with(SOUTH, state.get(SOUTH)).with(WEST, state.get(WEST));
        }
      }

      state = state.with(UP, false);

      worldIn.setBlockState(below, state, 3);
    }
  }

  private Block getStateFromStage() {
    switch (this.vineStage) {
      case START:
        if (this.foliage == SlimeGrassBlock.FoliageType.BLUE) {
          return WorldBlocks.blue_slime_vine_middle.get();
        } else if (this.foliage == SlimeGrassBlock.FoliageType.PURPLE) {
          return WorldBlocks.purple_slime_vine_middle.get();
        }
      case MIDDLE:
        if (this.foliage == SlimeGrassBlock.FoliageType.BLUE) {
          return WorldBlocks.blue_slime_vine_end.get();
        } else if (this.foliage == SlimeGrassBlock.FoliageType.PURPLE) {
          return WorldBlocks.purple_slime_vine_end.get();
        }
      case END:
        return null;
    }
    return null;
  }

  private boolean freeFloating(IWorld world, BlockPos pos, BlockState state) {
    for (Direction side : Direction.Plane.HORIZONTAL) {
      if (state.get(getPropertyFor(side)) && canAttachTo(world, pos.offset(side), side.getOpposite())) {
        return false;
      }
    }
    return true;
  }

  @Override
  @Deprecated
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (worldIn.isRemote) {
      return;
    }

    BlockState oldState = state;

    // check each side to see if it can stay
    state = this.getCurrentState(state, worldIn, pos);

    // is our position still valid?
    if (this.getNumOfFaces(state) < 0) {
      spawnDrops(state, worldIn, pos);
      worldIn.removeBlock(pos, false);
    } else if (oldState != state) {
      worldIn.setBlockState(pos, state, 2);
    }

    // notify bottom block to update its state since ours might have changed as well
    BlockPos down = pos.down();
    BlockState state2;
    while ((state2 = worldIn.getBlockState(down)).getBlock() instanceof SlimeVineBlock) {
      worldIn.notifyBlockUpdate(down, state2, state2, 3);
      down = down.down();
    }
  }

  private BlockState getCurrentState(BlockState state, IBlockReader world, BlockPos pos) {
    BlockPos blockpos = pos.up();
    if (state.get(UP)) {
      state = state.with(UP, canAttachTo(world, blockpos, Direction.DOWN));
    }

    BlockState blockstate = null;

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      BooleanProperty booleanproperty = getPropertyFor(direction);
      if (state.get(booleanproperty)) {
        boolean flag = this.getFlagFromState(world, pos, direction);
        if (!flag) {
          if (blockstate == null) {
            blockstate = world.getBlockState(blockpos);
          }

          flag = blockstate.getBlock() instanceof SlimeLeavesBlock || (blockstate.getBlock() instanceof SlimeVineBlock && blockstate.get(booleanproperty));
        }

        state = state.with(booleanproperty, flag);
      }
    }

    return state;
  }

  private boolean getFlagFromState(IBlockReader world, BlockPos pos, Direction direction) {
    if (direction == Direction.DOWN) {
      return false;
    } else {
      BlockPos blockpos = pos.offset(direction);
      if (canAttachTo(world, blockpos, direction)) {
        return true;
      } else if (direction.getAxis() == Direction.Axis.Y) {
        return false;
      } else {
        BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
        BlockState blockstate = world.getBlockState(pos.up());
        return blockstate.getBlock() instanceof SlimeVineBlock && blockstate.get(booleanproperty);
      }
    }
  }

  private int getNumOfFaces(BlockState state) {
    int i = 0;

    for (BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values()) {
      if (state.get(booleanproperty)) {
        ++i;
      }
    }

    return i;
  }

  public static boolean canAttachTo(IBlockReader worldIn, BlockPos pos, Direction direction) {
    BlockState blockstate = worldIn.getBlockState(pos);
    return Block.doesSideFillSquare(blockstate.getCollisionShape(worldIn, pos), direction.getOpposite());
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return this.getNumOfFaces(this.getCurrentState(state, worldIn, pos)) > 0;
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
    } else {
      BlockState blockstate = this.getCurrentState(stateIn, worldIn, currentPos);
      return !(this.getNumOfFaces(blockstate) > 0) ? Blocks.AIR.getDefaultState() : blockstate;
    }
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockState blockstate = context.getWorld().getBlockState(context.getPos());
    boolean flag = blockstate.getBlock() == this;
    BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

    for (Direction direction : context.getNearestLookingDirections()) {
      if (direction != Direction.DOWN) {
        BooleanProperty booleanproperty = getPropertyFor(direction);
        boolean flag1 = flag && blockstate.get(booleanproperty);
        if (!flag1 && this.getFlagFromState(context.getWorld(), context.getPos(), direction)) {
          return blockstate1.with(booleanproperty, Boolean.TRUE);
        }
      }
    }

    return flag ? blockstate1 : null;
  }

  public BlockState getStateToPlace(IWorld world, BlockPos pos) {
    BlockState blockstate = world.getBlockState(pos);
    boolean flag = blockstate.getBlock() == this;
    BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

    for (Direction direction : new Direction[]{Direction.EAST, Direction.UP, Direction.SOUTH, Direction.NORTH, Direction.WEST}) {
      if (direction != Direction.DOWN) {
        BooleanProperty booleanproperty = getPropertyFor(direction);
        boolean flag1 = flag && blockstate.get(booleanproperty);
        if (!flag1 && this.getFlagFromState(world, pos, direction)) {
          blockstate1 = blockstate1.with(booleanproperty, Boolean.TRUE);
        }
      }
    }

    return blockstate1;
  }

  public SlimeGrassBlock.FoliageType getFoliageType() {
    return this.foliage;
  }

  public enum VineStage implements IStringSerializable {
    START,
    MIDDLE,
    END;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }

}
