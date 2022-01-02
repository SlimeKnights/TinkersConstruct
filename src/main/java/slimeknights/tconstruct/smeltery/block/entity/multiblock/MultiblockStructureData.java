package slimeknights.tconstruct.smeltery.block.entity.multiblock;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Data class representing the size and contents of a multiblock
 */
public class MultiblockStructureData {
  public static final String TAG_EXTRA_POS = "extra";
  public static final String TAG_MIN = "min";
  public static final String TAG_MAX = "max";

  /** Smallest block position in the structure */
  @Getter
  private final BlockPos minPos;
  /** Largest block position in the structure */
  @Getter
  private final BlockPos maxPos;

  /** Contains all positions not in the standard areas, typically inside */
  protected final Set<BlockPos> extra;

  /** Booleans to determine bound check parameters */
  private final boolean hasCeiling, hasFrame, hasFloor;

  /**
   * Smallest position inside the structure walls
   */
  @Getter
  private final BlockPos minInside;
  /**
   * Largest position inside the structure walls
   */
  @Getter
  private final BlockPos maxInside;

  /** Inside sizes */
  @Getter
  private final int innerX, innerY, innerZ;

  /** Bounding box representing the area inside the structure */
  @Getter
  private final AABB bounds;

  public MultiblockStructureData(BlockPos minPos, BlockPos maxPos, Set<BlockPos> extraPositons, boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    this.minPos = minPos;
    this.maxPos = maxPos;
    this.extra = extraPositons;
    this.hasFloor = hasFloor;
    this.hasFrame = hasFrame;
    this.hasCeiling = hasCeiling;

    // inner positions
    minInside = minPos.offset(1, hasFloor ? 1 : 0, 1);
    maxInside = maxPos.offset(-1, hasCeiling ? -1 : 0, -1);
    innerX = maxInside.getX() - minInside.getX() + 1;
    innerY = maxInside.getY() - minInside.getY() + 1;
    innerZ = maxInside.getZ() - minInside.getZ() + 1;
    bounds = new AABB(minInside, maxInside.offset(1, 1, 1));
  }

  /**
   * Checks if a positon is within the cube made from two other positions
   * @param pos  Position to check
   * @param min  Min position
   * @param max  Max position
   * @return  True if within the positions
   */
  public static boolean isWithin(BlockPos pos, BlockPos min, BlockPos max) {
    return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ()
           && pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
  }

  /**
   * Checks if the block position is within the bounds of the structure
   * @param pos  Position to check
   * @return  True if the position is within the bounds
   */
  public boolean withinBounds(BlockPos pos) {
    return isWithin(pos, minPos, maxPos);
  }

  /**
   * Checks if the position is within the inside of the structure
   * @param pos  Position to check
   * @return  True if within the central bounds
   */
  public boolean isInside(BlockPos pos) {
    return isWithin(pos, minInside, maxInside);
  }

  /**
   * Checks if the given block position is part of this structure.
   * @param pos  Position to check
   * @return  True if its part of this structure
   */
  public boolean contains(BlockPos pos) {
    return withinBounds(pos) && containsBase(pos);
  }

  /**
   * Checks if the given block position is part of this structure. Slightly simplier logic assuming the position is within bounds
   * @param pos  Position to check
   * @return  True if its part of this structure
   */
  private boolean containsBase(BlockPos pos) {
    // blocks in the inner region are added to the extra positions, fall back to that
    if (!isInside(pos)) {
      // if there is a frame, shape is a full cube so the subtraction is all we need
      if (hasFrame) {
        return true;
      }

      // otherwise we have to count edges to make sure its not on a frame
      // frame is any blocks touching two edges
      int edges = 0;
      if (pos.getX() == minPos.getX() || pos.getX() == maxPos.getX()) edges++;
      if (pos.getZ() == minPos.getZ() || pos.getZ() == maxPos.getZ()) edges++;
      if ((hasFloor && pos.getY() == minPos.getY()) ||
          (hasCeiling && pos.getX() == maxPos.getX())) edges++;
      if (edges < 2) {
        return true;
      }
    }

    // inner blocks and frame blocks (no frame) can both be added
    // though note checking code does not currently support finding extra frame blocks
    return extra.contains(pos);
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
   * Iterates over each position contained in this structure
   * @param consumer  Position consumer, note the position is mutable, so call {@link BlockPos#immutable()} if you have to store it
   */
  public void forEachContained(Consumer<BlockPos.MutableBlockPos> consumer) {
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
      for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
        for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
          mutable.set(x, y, z);
          if (containsBase(mutable)) {
            consumer.accept(mutable);
          }
        }
      }
    }
  }

  /**
   * Assigns the master to all servants in this structure
   * @param master        Master to assign
   * @param oldStructure  Previous structure instance. Reduces the number of masters assigned and removes old masters
   */
  public <T extends MantleBlockEntity & IMasterLogic> void assignMaster(T master, @Nullable MultiblockStructureData oldStructure) {
    Predicate<BlockPos> shouldUpdate;
    if (oldStructure == null) {
      shouldUpdate = pos -> true;
    } else {
      shouldUpdate = pos -> !oldStructure.contains(pos);
    }

    Level world = master.getLevel();
    assert world != null;


    // assign master to each servant
    forEachContained(pos -> {
      if (shouldUpdate.test(pos) && world.hasChunkAt(pos)) {
        BlockEntityHelper.get(IServantLogic.class, world, pos).ifPresent(te -> te.setPotentialMaster(master));
      }
    });

    // remove master from anything only in the old structure
    if (oldStructure != null) {
      oldStructure.forEachContained(pos -> {
        if (!contains(pos) && world.hasChunkAt(pos)) {
          BlockEntityHelper.get(IServantLogic.class, world, pos).ifPresent(te -> te.removeMaster(master));
        }
      });
    }
  }

  /**
   * Clears the master on all blocks in this structure
   * @param master  Master to remove
   */
  public <T extends MantleBlockEntity & IMasterLogic> void clearMaster(T master) {
    Level world = master.getLevel();
    assert world != null;
    forEachContained(pos -> {
      if (world.hasChunkAt(pos)) {
        BlockEntityHelper.get(IServantLogic.class, world, pos).ifPresent(te -> te.removeMaster(master));
      }
    });
  }

  /**
   * Writes this structure to NBT for the client, client does not need a full list of positions, just render bounds
   * @return  structure as NBT
   */
  public CompoundTag writeClientTag() {
    CompoundTag nbt = new CompoundTag();
    nbt.put(TAG_MIN, TagUtil.writePos(minPos));
    nbt.put(TAG_MAX, TagUtil.writePos(maxPos));
    return nbt;
  }

  /**
   * Writes the full NBT data for writing to disk
   * @return  structure as NBT
   */
  public CompoundTag writeToTag() {
    CompoundTag nbt = writeClientTag();
    if (!extra.isEmpty()) {
      nbt.put(TAG_EXTRA_POS, writePosList(extra));
    }
    return nbt;
  }

  /**
   * Writes a lit of positions to NBT
   * @param collection  Position collection
   * @return  NBT list
   */
  protected static ListTag writePosList(Collection<BlockPos> collection) {
    ListTag list = new ListTag();
    for (BlockPos pos : collection) {
      list.add(TagUtil.writePos(pos));
    }
    return list;
  }
}
