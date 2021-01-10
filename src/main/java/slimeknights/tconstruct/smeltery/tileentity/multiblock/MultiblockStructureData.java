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

  /** Smallest block position in the structure */
  @Getter
  private final BlockPos minPos;
  /** Largest block position in the structure */
  @Getter
  private final BlockPos maxPos;

  /** Contains all positions currently part of the structure */
  protected final Set<BlockPos> positions;

  /** Size of the structure in each direction */
  @Getter
  private final int dx, dy, dz;

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
  public MultiblockStructureData(Set<BlockPos> positions, boolean hasFloor, boolean hasCeiling) {
    this.positions = positions;

    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    int minZ = Integer.MAX_VALUE;
    int maxZ = Integer.MIN_VALUE;
    for(BlockPos pos : positions) {
      if(pos.getX() < minX) minX = pos.getX();
      if(pos.getX() > maxX) maxX = pos.getX();
      if(pos.getY() < minY) minY = pos.getY();
      if(pos.getY() > maxY) maxY = pos.getY();
      if(pos.getZ() < minZ) minZ = pos.getZ();
      if(pos.getZ() > maxZ) maxZ = pos.getZ();
    }

    minPos = new BlockPos(minX, minY, minZ);
    maxPos = new BlockPos(maxX, maxY, maxZ);
    dx = maxX - minX + 1;
    dy = maxY - minY + 1;
    dz = maxZ - minZ + 1;

    // inner positions
    insideMin = minPos.add(1, hasFloor ? 1 : 0, 1);
    insideMax = maxPos.add(-1, hasCeiling ? -1 : 0, -1);
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
