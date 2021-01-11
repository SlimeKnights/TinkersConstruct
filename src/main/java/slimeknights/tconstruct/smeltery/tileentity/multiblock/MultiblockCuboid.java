package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableCollection.Builder;
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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Base class for all cuboid multiblocks
 */
@AllArgsConstructor
public abstract class MultiblockCuboid {

  // constants to make code more readible
  private static final int NORTH = Direction.NORTH.getHorizontalIndex();
  private static final int EAST = Direction.EAST.getHorizontalIndex();
  private static final int SOUTH = Direction.SOUTH.getHorizontalIndex();
  private static final int WEST = Direction.WEST.getHorizontalIndex();

  /** If true, the multiblock requires a floor */
  protected final boolean hasFloor;
  /** If true, the multiblock requires a frame */
  private final boolean hasFrame;
  /** If true, the multiblock requires a ceiling */
  protected final boolean hasCeiling;
  /** Maximum number of blocks to detect downwards */
  private final int downLimit;
  /** Maximum inner size of the structure */
  private final int innerLimit;

  /**
   * Constructor with default downLimit of 64 and innerLimit of 9
   */
  public MultiblockCuboid(boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    this(hasFloor, hasFrame, hasCeiling, 64, 9);
  }

  /**
   * Gets a list of all positions inside the structure
   * @param world   World instance
   * @param master  Position of the master
   * @param facing  Direction the master is facing. Opposite is behind the controller
   * @return  Set of positions in the sturcture
   */
  @Nullable
  protected Set<BlockPos> detectMultiblockPositions(World world, BlockPos master, Direction facing) {
    // list of blocks that are part of the multiblock
    ImmutableSet.Builder<BlockPos> subBlocks = ImmutableSet.builder();
    // center is the lowest block behind in a position behind the controller
    BlockPos center = getOuterPos(world, master.offset(facing.getOpposite()), Direction.DOWN, downLimit).up();

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

    Consumer<Collection<BlockPos>> posConsumer = subBlocks::addAll;

    // check the floor (frame check done inside)
    if (hasFloor) {
      if (!detectCap(world, center.down(), edges, CuboidSide.FLOOR, posConsumer)) {
        return null;
      }
    }

    // go up layer for layer (again, frame check done inside)
    int height = 0;
    for (; height + center.getY() < world.getHeight(); height++) {
      if(!detectLayer(world, center.up(height), edges, posConsumer)) {
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
      if(!detectCap(world, center.up(height), edges, CuboidSide.CEILING, posConsumer)) {
        return null;
      }
    }

    // return positions to finish
    return subBlocks.build();
  }

  /**
   * Detects a multiblock structure for the given controller
   * @param world   World instance
   * @param master  Position of the master
   * @param facing  Direction the master is facing. Opposite is behind the controller
   * @return  Multiblock structure data instance, or null if structure is invalid
   */
  @Nullable
  public MultiblockStructureData detectMultiblock(World world, BlockPos master, Direction facing) {
    Set<BlockPos> positions = detectMultiblockPositions(world, master, facing);
    if (positions != null) {
      return new MultiblockStructureData(positions, hasFloor, hasCeiling);
    }
    return null;
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
   * @param center    Start position for iteration
   * @param edges     Distances from edge for each direction
   * @param side      Side of the cube
   * @param consumer  Set of all structure positions for the output
   * @return  True if this "cap" is valid, false if not
   */
  protected boolean detectCap(World world, BlockPos center, int[] edges, CuboidSide side, Consumer<Collection<BlockPos>> consumer) {
    BlockPos from = center.add(edges[WEST], 0, edges[NORTH]);
    BlockPos to = center.add(edges[EAST], 0, edges[SOUTH]);

    // ensure the area is loaded before trying
    if (!world.isAreaLoaded(from, to)) {
      return false;
    }

    // temporary list of position candidates, so we can only add them if successful
    List<BlockPos> candidates = Lists.newArrayList();

    // validate frame first
    if (hasFrame) {
      // function to check a single position in the frame
      Predicate<BlockPos> frameCheck = pos -> {
        if (isValidBlock(world, pos, side, true)) {
          candidates.add(pos);
          return true;
        }
        return false;
      };

      // calculate blocks
      // x direction
      for (int x = 0; x <= to.getX() - from.getX(); x++) {
        if (!frameCheck.test(from.add(x, 0, 0))) return false;
        if (!frameCheck.test(to.add(-x, 0, 0))) return false;
      }
      // z direction. don't doublecheck corners
      for (int z = 1; z < to.getZ() - from.getZ(); z++) {
        if (!frameCheck.test(from.add(0, 0, z))) return false;
        if (!frameCheck.test(to.add(0, 0, -z))) return false;
      }
    }

    // validate inside of the floor
    from = from.add(1, 0, 1);
    to = to.add(-1, 0, -1);

    // iterate positions in the structure
    for(BlockPos row = from; row.getZ() <= to.getZ(); row = row.add(0, 0, 1)) {
      for(BlockPos pos = row; pos.getX() <= to.getX(); pos = pos.add(1, 0, 0)) {
        if (!isValidBlock(world, pos, side, false)) {
          return false;
        }
        candidates.add(pos);
      }
    }

    // structure detection successful, so add positions to the set
    consumer.accept(candidates);
    return true;
  }

  /**
   * Detects an inner layer of the structure. That is, an area with an empty center
   * @param world     World instance
   * @param center    Start position for iteration
   * @param edges     Distances from edge for each direction
   * @param consumer  Set of all structure positions for the output
   * @return  True if this layer is valid, false otherwise
   */
  protected boolean detectLayer(World world, BlockPos center, int[] edges, Consumer<Collection<BlockPos>> consumer) {
    BlockPos from = center.add(edges[WEST], 0, edges[NORTH]);
    BlockPos to = center.add(edges[EAST], 0, edges[SOUTH]);

    // ensure its loaded
    if(!world.isAreaLoaded(from, to)) {
      return false;
    }

    // temporary list of position candidates, so we can only add them if successful
    List<BlockPos> candidates = Lists.newArrayList();

    // validate frame first
    if (hasFrame) {
      // function to check a single position in the frame
      Predicate<BlockPos> frameCheck = pos -> {
        if (isValidBlock(world, pos, CuboidSide.WALL, true)) {
          candidates.add(pos);
          return true;
        }
        return false;
      };

      // we only have 4 corner blocks to check
      if (!frameCheck.test(from)) return false;
      if (!frameCheck.test(to)) return false;
      if (!frameCheck.test(new BlockPos(to.getX(), from.getY(), from.getZ()))) return false;
      if (!frameCheck.test(new BlockPos(from.getX(), from.getY(), to.getZ()))) return false;
    }

    // validate the inside
    for (int x = edges[1] + 1; x < edges[3]; x++) {
      for (int z = edges[2] + 1; z < edges[0]; z++) {
        // ensure its a valid block for inside the structure
        BlockPos pos = center.add(x, 0, z);
        if (isInnerBlock(world, pos)) {
          // skip airblocks for the list of inner blocks
          if (!world.isAirBlock(pos)) {
            candidates.add(pos);
          }
        } else {
          return false;
        }
      }
    }

    // validate the 4 sides
    Predicate<BlockPos> wallCheck = pos -> {
      if (isValidBlock(world, pos, CuboidSide.WALL, false)) {
        candidates.add(pos);
        return true;
      }
      return false;
    };
    for (int x = edges[1] + 1; x < edges[3]; x++) {
      if (!wallCheck.test(center.add(x, 0, edges[NORTH]))) return false;
      if (!wallCheck.test(center.add(x, 0, edges[SOUTH]))) return false;
    }
    for (int z = edges[2] + 1; z < edges[0]; z++) {
      if (!wallCheck.test(center.add(edges[WEST], 0, z))) return false;
      if (!wallCheck.test(center.add(edges[EAST], 0, z))) return false;
    }

    // was successful, add all candidates
    consumer.accept(candidates);
    return true;
  }



  /* Valid Blocks */

  /**
   * Checks if a block is valid in the structure
   * @param world    World argument
   * @param pos      Position to check
   * @param side     Side of the structure, floor, ceiling, or wall
   * @param isFrame  If true, checking a frame. If false, checking a side
   * @return  True if this block is valid
   */
  protected abstract boolean isValidBlock(World world, BlockPos pos, CuboidSide side, boolean isFrame);

  /**
   * Checks if a block is a valid block inside the cuboid
   * @param world  World instance
   * @param pos    Position to check
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
  public MultiblockStructureData readFromNBT(CompoundNBT nbt) {
    // serverside gets a tag list
    if (nbt.contains(MultiblockStructureData.TAG_POSITIONS, NBT.TAG_LIST)) {
      Set<BlockPos> set = readPosList(nbt, MultiblockStructureData.TAG_POSITIONS, ImmutableSet::builder);
      if (!set.isEmpty()) {
        return new MultiblockStructureData(set, hasFloor, hasCeiling);
      }
    } else {
      // client side gets just min and max
      BlockPos minPos = TagUtil.readPos(nbt, MultiblockStructureData.TAG_MIN);
      BlockPos maxPos = TagUtil.readPos(nbt, MultiblockStructureData.TAG_MAX);
      if (minPos != null && maxPos != null) {
        return new MultiblockStructureData(ImmutableSet.of(), minPos, maxPos, hasFloor, hasCeiling);
      }
    }

    return null;
  }

  /**
   * Reads a set of positions from a NBT position list
   * @param rootTag  Root NBT tag
   * @param key      Key to read
   * @return  Set of positions
   */
  @SuppressWarnings("unchecked")
  protected static <T extends Collection<BlockPos>> T readPosList(CompoundNBT rootTag, String key, Supplier<Builder<BlockPos>> builderSupplier) {
    ImmutableCollection.Builder<BlockPos> builder = builderSupplier.get();
    if (rootTag.contains(key, NBT.TAG_LIST)) {
      ListNBT list = rootTag.getList(key, NBT.TAG_COMPOUND);
      for (int i = 0; i < list.size(); i++) {
        BlockPos pos = TagUtil.readPos(list.getCompound(i));
        if (pos != null) {
          builder.add(pos);
        }
      }
    }
    return (T)builder.build();
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
