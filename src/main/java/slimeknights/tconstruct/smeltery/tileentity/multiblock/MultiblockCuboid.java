package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Base class for all cuboid multiblocks
 */
@AllArgsConstructor
public abstract class MultiblockCuboid<T extends MultiblockStructureData> {
  // constants to make code more readible
  private static final int NORTH = Direction.NORTH.getHorizontalIndex();
  private static final int EAST = Direction.EAST.getHorizontalIndex();
  private static final int SOUTH = Direction.SOUTH.getHorizontalIndex();
  private static final int WEST = Direction.WEST.getHorizontalIndex();

  /** If true, the multiblock requires a floor */
  protected final boolean hasFloor;
  /** If true, the multiblock requires a frame */
  protected final boolean hasFrame;
  /** If true, the multiblock requires a ceiling */
  protected final boolean hasCeiling;
  /** Maximum number of blocks to detect downwards */
  private final int maxHeight;
  /** Maximum inner size of the structure */
  private final int innerLimit;

  /**
   * Constructor with default downLimit of 64 and innerLimit of 9
   */
  public MultiblockCuboid(boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    this(hasFloor, hasFrame, hasCeiling, 64, 14);
  }

  /**
   * Gets a list of all positions inside the structure
   * @param world   World instance
   * @param master  Position of the master
   * @param facing  Direction the master is facing. Opposite is behind the controller
   * @return  Multiblock structue data
   */
  @Nullable
  public T detectMultiblock(World world, BlockPos master, Direction facing) {
    // list of blocks that are part of the multiblock, but not in a standard position
    ImmutableSet.Builder<BlockPos> extraBlocks = ImmutableSet.builder();
    // center is the lowest block behind in a position behind the controller
    BlockPos center = getOuterPos(world, master.offset(facing.getOpposite()), Direction.DOWN, maxHeight).up();

    // below lowest internal position
    if (!hasFrame && master.getY() < center.getY()) {
      return null;
    }

    // distances to the edges including the outer blocks
    int[] edges = new int[4];
    // order: south/west/north/east
    for (Direction direction : Plane.HORIZONTAL) {
      // move to wall
      BlockPos pos = getOuterPos(world, center, direction, innerLimit);
      edges[direction.getHorizontalIndex()] = (pos.getX() - center.getX()) + (pos.getZ() - center.getZ());
    }

    // walls too far away?
    int xd = (edges[SOUTH] - edges[NORTH]) - 1;
    int zd = (edges[EAST] - edges[WEST]) - 1;
    if(xd > innerLimit || zd > innerLimit) {
      return null;
    }

    // for the rest of calculation, will use a from and a to position bounds
    BlockPos from = center.add(edges[WEST], 0, edges[NORTH]);
    BlockPos to = center.add(edges[EAST], 0, edges[SOUTH]);
    Consumer<Collection<BlockPos>> posConsumer = extraBlocks::addAll;

    // check the floor (frame check done inside)
    if (hasFloor) {
      if (!detectCap(world, from.down(), to.down(), CuboidSide.FLOOR, posConsumer)) {
        return null;
      }
    }

    // go up layer for layer (again, frame check done inside)
    int height = 0;
    int localMax = Math.min(maxHeight, world.getHeight() - center.getY());
    for (; height < localMax; height++) {
      if(!detectLayer(world, from.up(height), to.up(height), posConsumer)) {
        break;
      }
    }

    // no walls?
    if(height <= master.getY() - center.getY()) {
      return null;
    }

    // detect ceiling (yup. frame check done inside.)
    if(hasCeiling) {
      // "height" failed above meaning there is a non-hollow layer there
      // assuming its a valid structure, it failed because its a ceiling (if another reason, the ceiling check will fail)
      if(!detectCap(world, from.up(height), to.up(height), CuboidSide.CEILING, posConsumer)) {
        return null;
      }
    }

    // get final bounds
    // min is 1 block down if we have a floor (to/from is at the first layer)
    // max is at height, 1 down is the last successful layer if no ceiling
    BlockPos minPos = hasFloor ? from.down() : from;
    BlockPos maxPos = to.up(hasCeiling ? height : height - 1);
    return create(minPos, maxPos, extraBlocks.build());
  }

  /* Layer detection */

  /**
   * Gets the farthest position in the given direction
   * @param world      World instance
   * @param pos        Start position
   * @param direction  Direction to check
   * @param limit      Max distance to check
   * @return  Block position of farthest position in the directon
   */
  protected BlockPos getOuterPos(World world, BlockPos pos, Direction direction, int limit) {
    for(int i = 0; i < limit && world.isBlockLoaded(pos) && isInnerBlock(world, pos); i++) {
      pos = pos.offset(direction);
    }

    return pos;
  }

  /**
   * Detects the floor or ceiling of the structure
   * @param world     World instance
   * @param from      Start position for the cap
   * @param to        End position for the cap
   * @param side      Side of the cube
   * @param consumer  Consumer for any extra positions in this region, specifically frame positions when frame is disabled
   * @return  True if this "cap" is valid, false if not
   */
  protected boolean detectCap(World world, BlockPos from, BlockPos to, CuboidSide side, Consumer<Collection<BlockPos>> consumer) {
    // ensure the area is loaded before trying
    if (!world.isAreaLoaded(from, to)) {
      return false;
    }

    // validate frame first
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    int height = from.getY();
    if (hasFrame) {
      // function to check a single position in the frame
      Predicate<BlockPos> frameCheck = pos -> isValidBlock(world, pos, side, true);

      // calculate blocks
      // x direction
      for (int x = from.getX(); x <= to.getX(); x++) {
        if (!frameCheck.test(mutable.setPos(x, height, from.getZ()))) return false;
        if (!frameCheck.test(mutable.setPos(x, height, to.getZ()))) return false;
      }
      // z direction. don't doublecheck corners
      for (int z = from.getZ() + 1; z < to.getZ(); z++) {
        if (!frameCheck.test(mutable.setPos(from.getX(), height, z))) return false;
        if (!frameCheck.test(mutable.setPos(to.getX(), height, z))) return false;
      }
    }

    // validate inside of the floor
    for (int z = from.getZ() + 1; z < to.getZ(); z++) {
      for (int x = from.getX() + 1; x < to.getX(); x++) {
        if (!isValidBlock(world, mutable.setPos(x, height, z), side, false)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Detects an inner layer of the structure. That is, an area with an empty center
   * @param world     World instance
   * @param from      Start position for the layer
   * @param to        End position for the layer
   * @param consumer  Consumer for any extra positions in this region
   * @return  True if this layer is valid, false otherwise
   */
  protected boolean detectLayer(World world, BlockPos from, BlockPos to, Consumer<Collection<BlockPos>> consumer) {
    // ensure its loaded
    if(!world.isAreaLoaded(from, to)) {
      return false;
    }

    // temporary list of position candidates, so we can only add them if successful
    List<BlockPos> candidates = Lists.newArrayList();

    // validate frame first
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    int height = from.getY();
    if (hasFrame) {
      // function to check a single position in the frame
      Predicate<BlockPos> frameCheck = pos -> isValidBlock(world, pos, CuboidSide.WALL, true);

      // we only have 4 corner blocks to check
      if (!frameCheck.test(from)) return false;
      if (!frameCheck.test(to)) return false;
      if (!frameCheck.test(mutable.setPos(to.getX(), height, from.getZ()))) return false;
      if (!frameCheck.test(mutable.setPos(from.getX(), height, to.getZ()))) return false;
    }

    // validate the inside
    for (int x = from.getX() + 1; x < to.getX(); x++) {
      for (int z = from.getZ() + 1; z < to.getZ(); z++) {
        // ensure its a valid block for inside the structure
        mutable.setPos(x, height, z);
        if (isInnerBlock(world, mutable)) {
          // any non airblocks are added to extra blocks, this region is ignored by default
          if (!world.isAirBlock(mutable)) {
            candidates.add(mutable.toImmutable());
          }
        } else {
          return false;
        }
      }
    }

    // validate the 4 sides
    Predicate<BlockPos> wallCheck = pos -> isValidBlock(world, pos, CuboidSide.WALL, false);
    for (int x = from.getX() + 1; x < to.getX(); x++) {
      if (!wallCheck.test(mutable.setPos(x, height, from.getZ()))) return false;
      if (!wallCheck.test(mutable.setPos(x, height, to.getZ()))) return false;
    }
    for (int z = from.getZ() + 1; z < to.getZ(); z++) {
      if (!wallCheck.test(mutable.setPos(from.getX(), height, z))) return false;
      if (!wallCheck.test(mutable.setPos(to.getX(), height, z))) return false;
    }

    // was successful, add all candidates
    consumer.accept(candidates);
    return true;
  }



  /* Valid Blocks */

  /**
   * Checks if a block is valid in the structure
   * @param world    World argument
   * @param pos      Position to check, note it may be mutable
   * @param side     Side of the structure, floor, ceiling, or wall
   * @param isFrame  If true, checking a frame. If false, checking a side
   * @return  True if this block is valid
   */
  protected abstract boolean isValidBlock(World world, BlockPos pos, CuboidSide side, boolean isFrame);

  /**
   * Checks if a block is a valid block inside the cuboid
   * @param world  World instance
   * @param pos    Position to check, note it may be mutable
   * @return  True if its a valid inner block
   */
  public boolean isInnerBlock(World world, BlockPos pos) {
    return world.isAirBlock(pos);
  }


  /* Utilities */

  /**
   * Checks if the structure should update
   * @param world      World instance
   * @param structure  Structure data
   * @param pos        Position that changed
   * @param state      State that changed
   * @return  True if the structure should update
   */
  public abstract boolean shouldUpdate(World world, MultiblockStructureData structure, BlockPos pos, BlockState state);


  /* Serializing */

  /**
   * Reads the structure data from NBT
   * @param  nbt  NBT tag
   * @return Structure data, or null if invalid
   */
  @Nullable
  public T readFromNBT(CompoundNBT nbt) {
    BlockPos minPos = TagUtil.readPos(nbt, MultiblockStructureData.TAG_MIN);
    BlockPos maxPos = TagUtil.readPos(nbt, MultiblockStructureData.TAG_MAX);
    if (minPos == null || maxPos == null) {
      return null;
    }
    // will be empty client side
    Set<BlockPos> extra = ImmutableSet.copyOf(readPosList(nbt, MultiblockStructureData.TAG_EXTRA_POS));
    return create(minPos, maxPos, extra);
  }

  /**
   * Creates a new instance from the given min and max bounds. Used to create the structure client side
   * @param min       Min pos
   * @param max       Max pos
   * @param extraPos  Set of extra positons in the structure
   * @return  Structure from bounds
   */
  public abstract T create(BlockPos min, BlockPos max, Set<BlockPos> extraPos);

  /**
   * Reads a set of positions from a NBT position list
   * @param rootTag  Root NBT tag
   * @param key      Key to read
   * @return  Set of positions
   */
  protected static Collection<BlockPos> readPosList(CompoundNBT rootTag, String key) {
    List<BlockPos> collection;
    if (rootTag.contains(key, NBT.TAG_LIST)) {
      ListNBT list = rootTag.getList(key, NBT.TAG_COMPOUND);
      collection = new ArrayList<>(list.size());
      for (int i = 0; i < list.size(); i++) {
        BlockPos pos = TagUtil.readPos(list.getCompound(i));
        if (pos != null) {
          collection.add(pos);
        }
      }
    } else {
      collection = Collections.emptyList();
    }
    return collection;
  }

  /**
   * Enum for each of the different faces of the cube
   */
  public enum CuboidSide {
    FLOOR,
    CEILING,
    WALL
  }
}
