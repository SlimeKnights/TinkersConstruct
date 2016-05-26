package slimeknights.tconstruct.smeltery.multiblock;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class MultiblockCuboid extends MultiblockDetection {

  // if the multiblock requires a floor/ceiling
  public final boolean hasCeiling;
  public final boolean hasFloor;
  public final boolean hasFrame; // whether the frame needs to be present

  public MultiblockCuboid(boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    this.hasCeiling = hasCeiling;
    this.hasFloor = hasFloor;
    this.hasFrame = hasFrame;
  }

  /**
   * Detects a cuboid multiblock
   *
   * @param world  The world.
   * @param center A position inside the multiblock at the height of the master block
   * @param limit  Maximum INNER size of the multiblock.
   * @return Info about the multiblock or null if none was found
   */
  @Override
  public MultiblockStructure detectMultiblock(World world, BlockPos center, int limit) {
    // list of blocks that are part of the multiblock
    List<BlockPos> subBlocks = Lists.newArrayList();

    // move as low as possible
    int masterY = center.getY();
    center = getOuterPos(world, center, EnumFacing.DOWN, limit + 1).up();

    // distances to the edges including the outer blocks
    int edges[] = new int[4];
    // order: south/west/north/east
    for(EnumFacing direction : EnumFacing.HORIZONTALS) {
      // move to wall
      BlockPos pos = getOuterPos(world, center, direction, limit);

      edges[direction.getHorizontalIndex()] = (pos.getX() - center.getX()) + (pos.getZ() - center.getZ());
    }

    // walls too far away?
    int xd = (edges[EnumFacing.SOUTH.getHorizontalIndex()] - edges[EnumFacing.NORTH.getHorizontalIndex()]) - 1;
    int zd = (edges[EnumFacing.EAST.getHorizontalIndex()] - edges[EnumFacing.WEST.getHorizontalIndex()]) - 1;
    if(xd > limit ||
       zd > limit) {
      return null;
    }

    // check the floor (frame check done inside)
    if(hasFloor) {
      if(!detectFloor(world, center.down(), edges, subBlocks)) {
        return null;
      }
    }

    // go up layer for layer (again, frame check done inside)
    int height = 0;
    for(; height + center.getY() < world.getHeight(); height++) {
      if(!detectLayer(world, center.up(height), height, edges, subBlocks)) {
        break;
      }
    }

    // no walls?
    if(height < 1 + masterY - center.getY()) {
      return null;
    }

    // detect ceiling (yup. frame check done inside.)
    if(hasCeiling) {
      // move as high as possible
      if(!detectCeiling(world, center.up(height + 1), edges, subBlocks)) {
        return null;
      }
    }

    return new MultiblockStructure(xd, height, zd, subBlocks);
  }

  /* Valid Blocks */

  public boolean isFloorBlock(World world, BlockPos pos) {
    return isValidBlock(world, pos);
  }

  public boolean isCeilingBlock(World world, BlockPos pos) {
    return isValidBlock(world, pos);
  }

  public boolean isFrameBlock(World world, BlockPos pos) {
    return isValidBlock(world, pos);
  }

  public boolean isWallBlock(World world, BlockPos pos) {
    return isValidBlock(world, pos);
  }

  /* Detecting the outer shapes */
  protected boolean detectFloor(World world, BlockPos center, int[] edges, List<BlockPos> subBlocks) {
    return detectPlaneXZ(world, center, edges, false, subBlocks);
  }

  private boolean detectCeiling(World world, BlockPos center, int[] edges, List<BlockPos> subBlocks) {
    return detectPlaneXZ(world, center, edges, true, subBlocks);
  }

  protected boolean detectPlaneXZ(World world, BlockPos center, int[] edges, boolean ceiling, List<BlockPos> subBlocks) {
    BlockPos from = center.add(edges[1], 0, edges[2]);
    BlockPos to = center.add(edges[3], 0, edges[0]);
    List<BlockPos> candidates = Lists.newArrayList();

    // validate frame first
    if(hasFrame) {
      // calculate blocks
      List<BlockPos> frame = Lists.newArrayList();
      // x direction
      for(int x = 0; x <= to.getX() - from.getX(); x++) {
        frame.add(from.add(x, 0, 0));
        frame.add(to.add(-x, 0, 0));
      }
      // z direction. don't doublecheck corners
      for(int z = 1; z < to.getZ() - from.getZ(); z++) {
        frame.add(from.add(0, 0, z));
        frame.add(to.add(0, 0, -z));
      }

      // check the blocks
      for(BlockPos pos : frame) {
        if(!isFrameBlock(world, pos)) {
          return false;
        }
        candidates.add(pos);
      }
    }

    // validate inside of the floor
    from = from.add(1, 0, 1);
    to = to.add(-1, 0, -1);

    for(BlockPos z = from; z.getZ() <= to.getZ(); z = z.add(0, 0, 1)) {
      for(BlockPos x = z; x.getX() <= to.getX(); x = x.add(1, 0, 0)) {
        if(ceiling && !isCeilingBlock(world, x)) {
          return false;
        }
        else if(!ceiling && !isFloorBlock(world, x)) {
          return false;
        }
        candidates.add(x);
      }
    }

    subBlocks.addAll(candidates);
    return true;
  }

  protected boolean detectLayer(World world, BlockPos center, int layer, int[] edges, List<BlockPos> subBlocks) {
    BlockPos from = center.add(edges[1], 0, edges[2]);
    BlockPos to = center.add(edges[3], 0, edges[0]);
    List<BlockPos> candidates = Lists.newArrayList();

    // validate frame first
    if(hasFrame) {
      // calculate blocks
      List<BlockPos> frame = Lists.newArrayList();
      // we only have 4 corner blocks to check
      frame.add(from);
      frame.add(to);
      frame.add(new BlockPos(to.getX(), from.getY(), from.getZ()));
      frame.add(new BlockPos(from.getX(), from.getY(), to.getZ()));

      // check the blocks
      for(BlockPos pos : frame) {
        if(!isFrameBlock(world, pos)) {
          return false;
        }
        candidates.add(pos);
      }
    }

    // validate the inside
    List<BlockPos> blocks = Lists.newArrayList();
    for(int x = edges[1]+1; x < edges[3]; x++) {
      for(int z = edges[2]+1; z < edges[0]; z++) {
        blocks.add(center.add(x, 0 ,z));
      }
    }
    for(BlockPos pos : blocks) {
      if(!isInnerBlock(world, pos)) {
        return false;
      }
      if(!world.isAirBlock(pos)) {
        candidates.add(pos);
      }
    }

    // validate the 4 sides
    blocks.clear();
    for(int x = edges[1]+1; x < edges[3]; x++) {
      blocks.add(center.add(x, 0, edges[2]));
      blocks.add(center.add(x, 0, edges[0]));
    }
    for(int z = edges[2]+1; z < edges[0]; z++) {
      blocks.add(center.add(edges[1], 0, z));
      blocks.add(center.add(edges[3], 0, z));
    }

    for(BlockPos pos : blocks) {
      if(!isWallBlock(world, pos)) {
        return false;
      }
      candidates.add(pos);
    }

    subBlocks.addAll(candidates);
    return true;
  }
}
