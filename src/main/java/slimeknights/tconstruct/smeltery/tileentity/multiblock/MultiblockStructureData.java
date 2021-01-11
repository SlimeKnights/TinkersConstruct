package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Data class representing the size and contents of a multiblock
 */
public class MultiblockStructureData {
  public static final String TAG_POSITIONS = "positions";
  public static final String TAG_MIN = "min";
  public static final String TAG_MAX = "max";

  /** Smallest block position in the structure */
  @Getter
  private final BlockPos minPos;
  /** Largest block position in the structure */
  @Getter
  private final BlockPos maxPos;

  /** Contains all positions currently part of the structure */
  protected final Set<BlockPos> positions;

  // TODO: needed?
//  /** Size of the structure in each direction */
//  @Getter
//  private final int dx, dy, dz;

  /**
   * Smallest position inside the structure walls
   */
  @Getter
  private final BlockPos insideMin;
  /**
   * Largest position inside the structure walls
   */
  @Getter
  private final BlockPos insideMax;

  /** Size of the inside of the structure */
  @Getter
  private final int internalSize;

  /**
   * Gets the min position based on the set
   * @param positions  Position set
   * @return  Min position
   */
  private static BlockPos getMinPos(Set<BlockPos> positions) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;
    for(BlockPos pos : positions) {
      if(pos.getX() < minX) minX = pos.getX();
      if(pos.getY() < minY) minY = pos.getY();
      if(pos.getZ() < minZ) minZ = pos.getZ();
    }
    return new BlockPos(minX, minY, minZ);
  }

  /**
   * Gets the max position based on the set
   * @param positions  Position set
   * @return  Max position
   */
  private static BlockPos getMaxPos(Set<BlockPos> positions) {
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    int maxZ = Integer.MIN_VALUE;
    for(BlockPos pos : positions) {
      if(pos.getX() > maxX) maxX = pos.getX();
      if(pos.getY() > maxY) maxY = pos.getY();
      if(pos.getZ() > maxZ) maxZ = pos.getZ();
    }
    return new BlockPos(maxX, maxY, maxZ);
  }

  public MultiblockStructureData(Set<BlockPos> positions, BlockPos minPos, BlockPos maxPos, boolean hasFloor, boolean hasCeiling) {
    this.positions = positions;
    this.minPos = minPos;
    this.maxPos = maxPos;

    // inner positions
    insideMin = minPos.add(1, hasFloor ? 1 : 0, 1);
    insideMax = maxPos.add(-1, hasCeiling ? -1 : 0, -1);
    internalSize = (insideMax.getX() - insideMin.getX() + 1)
                   * (insideMax.getY() - insideMin.getY() + 1)
                   * (insideMax.getZ() - insideMin.getZ() + 1);
  }

  public MultiblockStructureData(Set<BlockPos> positions, boolean hasFloor, boolean hasCeiling) {
    this(positions, getMinPos(positions), getMaxPos(positions), hasFloor, hasCeiling);
  }

  /**
   * Checks if the given block position is part of this structure
   * @param pos  Position to check
   * @return  True if its part of this structure
   */
  public boolean contains(BlockPos pos) {
    // can save a bit of effort on hash lookup if we check bounds first
    return isInside(pos) && positions.contains(pos);
  }

  /**
   * Checks if the block position is within the bounds of the structure
   * @param pos  Position to check
   * @return  True if the position is within the bounds
   */
  public boolean isInside(BlockPos pos) {
    return pos.getX() >= minPos.getX() && pos.getY() >= minPos.getY() && pos.getZ() >= minPos.getZ()
      && pos.getX() <= maxPos.getX() && pos.getY() <= maxPos.getY() && pos.getZ() <= maxPos.getZ();
  }

  /**
   * Checks if the block position is directly above the structure
   * @param pos  Position to check
   * @return  True if the position is exactly one block above the structure
   */
  public boolean isDirectlyAbove(BlockPos pos) {
    return pos.getX() >= minPos.getX() && pos.getZ() >= minPos.getZ()
           && pos.getX() <= maxPos.getX() && pos.getZ() <= maxPos.getZ()
           && pos.getY() == maxPos.getY() + 1;
  }

  /**
   * Assigns the master to all servants in this structure
   * @param master        Master to assign
   * @param oldStructure  Previous structure instance. Reduces the number of masters assigned and removes old masters
   */
  public void assignMaster(IMasterLogic master, @Nullable MultiblockStructureData oldStructure) {
    Predicate<BlockPos> shouldUpdate;
    if (oldStructure == null) {
      shouldUpdate = pos -> true;
    } else {
      shouldUpdate = pos -> !oldStructure.contains(pos);
    }

    World world = master.getTileEntity().getWorld();
    assert world != null;
    // assign master to each servant
    for (BlockPos pos : positions) {
      if (shouldUpdate.test(pos) && world.isBlockLoaded(pos)) {
        TileEntityHelper.getTile(IServantLogic.class, world, pos).ifPresent(te -> te.setPotentialMaster(master));
      }
    }

    // remove master from anything only in the old structure
    if (oldStructure != null) {
      for (BlockPos pos : oldStructure.positions) {
        if (!contains(pos) && world.isBlockLoaded(pos)) {
          TileEntityHelper.getTile(IServantLogic.class, world, pos).ifPresent(te -> te.removeMaster(master));
        }
      }
    }
  }

  /**
   * Clears the master on all blocks in this structure
   * @param master  Master to remove
   */
  public void clearMaster(IMasterLogic master) {
    World world = master.getTileEntity().getWorld();
    assert world != null;
    for (BlockPos pos : positions) {
      if (!contains(pos) && world.isBlockLoaded(pos)) {
        TileEntityHelper.getTile(IServantLogic.class, world, pos).ifPresent(te -> te.removeMaster(master));
      }
    }
  }

  /**
   * Writes this structure to NBT
   * @return  structure as NBT
   */
  public CompoundNBT writeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(TAG_POSITIONS, writePosList(positions));
    return nbt;
  }

  /**
   * Writes this structure to NBT for the client, requires fewer positions to be synced
   * @return  structure as NBT
   */
  public CompoundNBT writeClientNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(TAG_MIN, TagUtil.writePos(minPos));
    nbt.put(TAG_MAX, TagUtil.writePos(maxPos));
    return nbt;
  }

  /**
   * Writes a lit of positions to NBT
   * @param collection  Position collection
   * @return  NBT list
   */
  protected static ListNBT writePosList(Collection<BlockPos> collection) {
    ListNBT list = new ListNBT();
    for (BlockPos pos : collection) {
      list.add(TagUtil.writePos(pos));
    }
    return list;
  }
}
