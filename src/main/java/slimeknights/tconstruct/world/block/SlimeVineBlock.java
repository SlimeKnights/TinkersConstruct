package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class SlimeVineBlock extends VineBlock {

  protected final SlimeGrassBlock.FoliageType foliage;
  protected final SlimeVineBlock nextStage;

  public SlimeVineBlock(SlimeGrassBlock.FoliageType foliage, SlimeVineBlock nextStage) {
    super(Block.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.PLANT));
    this.foliage = foliage;
    this.nextStage = nextStage;
  }

  @Override
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    if (!worldIn.isRemote) {
      if (random.nextInt(4) == 0) {
        this.grow(worldIn, random, pos, state);
      }
    }
  }

  private void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
    // end parts don't grow
    if (this.nextStage == null) {
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
          state = this.nextStage.getDefaultState()
                  .with(NORTH, state.get(NORTH))
                  .with(EAST, state.get(EAST))
                  .with(SOUTH, state.get(SOUTH))
                  .with(WEST, state.get(WEST));
        }
      }

      worldIn.setBlockState(below, state);
    }
  }

  private boolean freeFloating(World world, BlockPos pos, BlockState state) {
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
    for (Direction side : Direction.Plane.HORIZONTAL) {
      BooleanProperty prop = getPropertyFor(side);
      if (state.get(prop) && !canAttachTo(worldIn, pos, side.getOpposite())) {
        BlockState above = worldIn.getBlockState(pos.up());
        if (!(above.getBlock() instanceof VineBlock) || !above.get(prop)) {
          state = state.with(prop, false);
        }
      }
    }

    // is our position still valid?
    if (this.getNumOfFaces(state) == 0) {
      spawnDrops(state, worldIn, pos);
      worldIn.removeBlock(pos, false);
    }
    else if (oldState != state) {
      worldIn.setBlockState(pos, state, 2);
    }

    // notify bottom block to update its state since ours might have changed as well
    BlockPos down = pos.down();
    BlockState state2;
    while ((state2 = worldIn.getBlockState(down)).getBlock() instanceof VineBlock) {
      worldIn.notifyBlockUpdate(down, state2, state2, 3);
      down = down.down();
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

}
