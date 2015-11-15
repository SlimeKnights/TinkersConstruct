package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;

/**
 * Base class for a rectangular multiblock detection
 */
public abstract class MultiblockDetection {

  /**
   * Pass in any position inside the multiblock.
   * It'll detect the bottommost center block if one exists.
   * This is defined as the block that still is an air block, has a solid block to each side and es equally +-1 distant from each wall.
   * The passed parameter is a maximum distance where it stops searching. Just pass the max-size of your multiblock.
   */
  public BlockPos detectCenter(World world, BlockPos inside, int limit) {
    // inside = the block behind the controller "inside the smeltery"

    // adjust the x-position until the difference between the outer walls is at most 1
    // basically this means we center the block inside the smeltery on the x axis.
    int xd1 = 1, xd2 = 1; // x-difference
    int zd1 = 1, zd2 = 1; // z-difference
    for (int i = 1; i < limit; i++) // don't check farther than needed
    {
      // expand the range on the x axis as long as one side has not met a wall
      if(isInnerBlock(world, inside.add(-xd1, 0, 0))) {
        xd1++;
      }
      else if(isInnerBlock(world, inside.add(xd2, 0, 0))) {
        xd2++;
      }

      // if one side hit a wall and the other didn't we might have to re-center our x-position again
      if (xd1 - xd2 > 1)
      {
        // move x and offsets to the -x
        xd1--;
        inside = inside.add(-1, 0, 0);
        xd2++;
      }
      // or the right
      if (xd2 - xd1 > 1)
      {
        xd2--;
        inside = inside.add(1, 0, 0);
        xd1++;
      }

      // also do exactly the same on the z axis
      if(isInnerBlock(world, inside.add(0, 0, -zd1))) {
        zd1++;
      }
      else if(isInnerBlock(world, inside.add(0, 0, zd2))) {
        zd2++;
      }

      if (zd1 - zd2 > 1)
      {
        // move x and offsets to the -x
        zd1--;
        inside = inside.add(0, 0, -1);
        zd2++;
      }
      // or the right
      if (zd2 - zd1 > 1)
      {
        zd2--;
        inside = inside.add(0, 0, 1);
        zd1++;
      }
    }

    return inside;
  }

  protected BlockPos getOuterPos(World world, BlockPos pos, EnumFacing direction, int limit) {
    for(int i = 0; i < limit && isInnerBlock(world, pos); i++) {
      pos = pos.offset(direction);
    }

    return pos;
  }

  /* Allowed blocks */
  public boolean isInnerBlock(World world, BlockPos pos) {
    return world.isAirBlock(pos);
  }

  public abstract boolean isValidBlock(World world, BlockPos pos);

  public static void assignMultiBlock(World world, BlockPos master, List<BlockPos> servants) {
    Block masterBlock = world.getBlockState(master).getBlock();
    if(!(masterBlock instanceof IMasterLogic)) {
      throw new IllegalArgumentException("Master must be of IMasterLogic");
    }

    IMasterLogic masterLogic = (IMasterLogic) masterBlock;
    // assign master to each servant
    for(BlockPos pos : servants) {
      IBlockState state = world.getBlockState(pos);
      if(state.getBlock() instanceof IServantLogic) {
        ((IServantLogic) state.getBlock()).setPotentialMaster(masterLogic, world, pos);
      }
    }
  }
}
