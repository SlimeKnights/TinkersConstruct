package slimeknights.tconstruct.smeltery.multiblock;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.MultiServantLogic;

import java.util.List;

public abstract class MultiblockDetection {
  public static class MultiblockStructure {
    /** x-width of the structure */
    public final int xd;
    /** y-width of the structure */
    public final int yd;
    /** z-width of the structure */
    public final int zd;
    /** All blocks that are part of the structure */
    public final List<BlockPos> blocks;
    /** Smallest coordinate in the multiblock */
    public final BlockPos minPos;
    /** Largest coordinate in the multiblock */
    public final BlockPos maxPos;

    @Getter
    protected final AxisAlignedBB bb;

    public MultiblockStructure(int xd, int yd, int zd, List<BlockPos> blocks) {
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.blocks = blocks;

      int minx = Integer.MAX_VALUE;
      int maxx = Integer.MIN_VALUE;
      int miny = Integer.MAX_VALUE;
      int maxy = Integer.MIN_VALUE;
      int minz = Integer.MAX_VALUE;
      int maxz = Integer.MIN_VALUE;
      for(BlockPos pos : blocks) {
        if(pos.getX() < minx) {
          minx = pos.getX();
        }
        if(pos.getX() > maxx) {
          maxx = pos.getX();
        }
        if(pos.getY() < miny) {
          miny = pos.getY();
        }
        if(pos.getY() > maxy) {
          maxy = pos.getY();
        }
        if(pos.getZ() < minz) {
          minz = pos.getZ();
        }
        if(pos.getZ() > maxz) {
          maxz = pos.getZ();
        }
      }

      bb = new AxisAlignedBB(minx, miny, minz, maxx + 1, maxy + 1, maxz + 1);
      minPos = new BlockPos(minx, miny, minz);
      maxPos = new BlockPos(maxx, maxy, maxz);
    }
  }

  public BlockPos detectCenter(World world, BlockPos inside, int limit) {
    // inside = the block behind the controller "inside the smeltery"

    // adjust the x-position until the difference between the outer walls is at most 1
    // basically this means we center the block inside the smeltery on the x axis.
    int xd1 = 1, xd2 = 1; // x-difference
    int zd1 = 1, zd2 = 1; // z-difference
    for(int i = 1; i < limit; i++) // don't check farther than needed
    {
      // expand the range on the x axis as long as one side has not met a wall
      if(isInnerBlock(world, inside.add(-xd1, 0, 0))) {
        xd1++;
      }
      else if(isInnerBlock(world, inside.add(xd2, 0, 0))) {
        xd2++;
      }

      // if one side hit a wall and the other didn't we might have to re-center our x-position again
      if(xd1 - xd2 > 1) {
        // move x and offsets to the -x
        xd1--;
        inside = inside.add(-1, 0, 0);
        xd2++;
      }
      // or the right
      if(xd2 - xd1 > 1) {
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

      if(zd1 - zd2 > 1) {
        // move x and offsets to the -x
        zd1--;
        inside = inside.add(0, 0, -1);
        zd2++;
      }
      // or the right
      if(zd2 - zd1 > 1) {
        zd2--;
        inside = inside.add(0, 0, 1);
        zd1++;
      }
    }

    return inside;
  }

  protected BlockPos getOuterPos(World world, BlockPos pos, Direction direction, int limit) {
    for(int i = 0; i < limit && isInnerBlock(world, pos); i++) {
      pos = pos.offset(direction);
    }

    return pos;
  }

  public abstract MultiblockStructure detectMultiblock(World world, BlockPos center, int limit);

  /* Allowed blocks */
  public boolean isInnerBlock(World world, BlockPos pos) {
    return world.isBlockLoaded(pos) && world.isAirBlock(pos);
  }

  public abstract boolean isValidBlock(World world, BlockPos pos);

  public boolean checkIfMultiblockCanBeRechecked(World world, MultiblockStructure structure) {
    return structure != null && structure.minPos.distanceSq(structure.maxPos) > 1 && world.isAreaLoaded(structure.minPos, structure.maxPos);
  }

  public static void assignMultiBlock(World world, BlockPos master, List<BlockPos> servants) {
    TileEntity masterBlock = world.getTileEntity(master);
    if(!(masterBlock instanceof IMasterLogic)) {
      throw new IllegalArgumentException("Master must be of IMasterLogic");
    }

    // assign master to each servant
    for(BlockPos pos : servants) {
      if(world.isBlockLoaded(pos)) {
        TileEntity slave = world.getTileEntity(pos);
        if(slave instanceof MultiServantLogic && slave.getWorld() != null) {
          MultiServantLogic logic = (MultiServantLogic)slave;
          BlockPos current = logic.getMasterPosition();
          if (current == null || !current.equals(master)) {
            logic.overrideMaster(master);
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
          }
        }
      }
    }
  }
}
