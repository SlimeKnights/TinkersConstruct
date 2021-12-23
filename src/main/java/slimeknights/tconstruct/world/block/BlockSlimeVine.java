package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockSlimeVine extends BlockVine {

  protected final FoliageType foliage;
  protected final BlockSlimeVine nextStage;

  public BlockSlimeVine(FoliageType foliage, BlockSlimeVine nextStage) {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setSoundType(SoundType.PLANT);

    this.foliage = foliage;
    this.nextStage = nextStage;
  }

  @Override
  public boolean canAttachTo(World world, BlockPos pos, EnumFacing side) {
    // override to check for any vine type instead of just Blocks.VINE
    Block above = world.getBlockState(pos.up()).getBlock();
    return this.isAcceptableNeighbor(world, pos.offset(side.getOpposite()), side) && (above == Blocks.AIR || above instanceof BlockVine || this.isAcceptableNeighbor(world, pos.up(), EnumFacing.UP));
  }

  // copied from BlockVine
  private boolean isAcceptableNeighbor(World world, BlockPos pos, EnumFacing side) {
    IBlockState state = world.getBlockState(pos);
    return state.getBlockFaceShape(world, pos, side) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(state.getBlock());
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    IBlockState iblockstate = this.getDefaultState();
    iblockstate = iblockstate.withProperty(NORTH, isAcceptableNeighbor(world, pos.north(), EnumFacing.SOUTH));
    iblockstate = iblockstate.withProperty(EAST, isAcceptableNeighbor(world, pos.east(), EnumFacing.WEST));
    iblockstate = iblockstate.withProperty(SOUTH, isAcceptableNeighbor(world, pos.south(), EnumFacing.NORTH));
    iblockstate = iblockstate.withProperty(WEST, isAcceptableNeighbor(world, pos.west(), EnumFacing.EAST));
    return iblockstate;
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
    if(world.isRemote) {
      return;
    }

    IBlockState oldState = state;

    // check each side to see if it can stay
    for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
      PropertyBool prop = getPropertyFor(side);
      if (state.getValue(prop) && !this.canAttachTo(world, pos, side.getOpposite())) {
        IBlockState above = world.getBlockState(pos.up());
        Block aboveBlock = above.getBlock();
        if (!(aboveBlock instanceof BlockSlimeLeaves) && !(aboveBlock instanceof BlockVine && above.getValue(prop))) {
          state = state.withProperty(prop, false);
        }
      }
    }

    // is our position still valid?
    if(getNumGrownFaces(state) == 0) {
      this.dropBlockAsItem(world, pos, state, 0);
      world.setBlockToAir(pos);
    } else if (oldState != state) {
      world.setBlockState(pos, state, 2);
    }

    // notify bottom block to update its state since ours might have changed as well
    BlockPos down = pos.down();
    IBlockState state2;
    while((state2 = world.getBlockState(down)).getBlock() instanceof BlockVine) {
      world.notifyBlockUpdate(down, state2, state2, 3);
      down = down.down();
    }
  }

  @Override
  public void updateTick(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if(!worldIn.isRemote) {
      if(rand.nextInt(4) == 0) {
        grow(worldIn, rand, pos, state);
      }
    }
  }

  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    // end parts don't grow
    if(nextStage == null) {
      return;
    }

    // we only grow down
    BlockPos below = pos.down();
    if(worldIn.isAirBlock(below)) {
      // free floating position?
      if(freeFloating(worldIn, pos, state)) {
        // at most 3 middle parts
        int i = 0;
        while(worldIn.getBlockState(pos.up(i)).getBlock() == this) {
          i++;
        }

        if(i > 2 || rand.nextInt(2) == 0) {
          state = nextStage.getDefaultState()
                           .withProperty(NORTH, state.getValue(NORTH))
                           .withProperty(EAST, state.getValue(EAST))
                           .withProperty(SOUTH, state.getValue(SOUTH))
                           .withProperty(WEST, state.getValue(WEST));
        }
      }

      worldIn.setBlockState(below, state);
    }
  }

  private boolean freeFloating(World world, BlockPos pos, IBlockState state) {
    for(EnumFacing side : EnumFacing.HORIZONTALS) {
      if(state.getValue(getPropertyFor(side)) && isAcceptableNeighbor(world, pos.offset(side), side.getOpposite())) {
        return false;
      }
    }
    return true;
  }
}
