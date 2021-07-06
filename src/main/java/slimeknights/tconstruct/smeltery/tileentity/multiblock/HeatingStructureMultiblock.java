package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock.StructureData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 */
public abstract class HeatingStructureMultiblock<T extends MantleTileEntity & IMasterLogic> extends MultiblockCuboid<StructureData> {
  private static final String TAG_TANKS = "tanks";
  private static final String TAG_INSIDE_CHECK = "insideCheck";

  /** Parent structure instance */
  protected final T parent;
  /** List to check if a tank is found between valid block checks */
  protected final List<BlockPos> tanks = new ArrayList<>();

  public HeatingStructureMultiblock(T parent, boolean hasFloor, boolean hasFrame, boolean hasCeiling, int maxHeight, int innerLimit) {
    super(hasFloor, hasFrame, hasCeiling, maxHeight, innerLimit);
    this.parent = parent;
  }

  public HeatingStructureMultiblock(T parent, boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    super(hasFloor, hasFrame, hasCeiling);
    this.parent = parent;
  }

  @Override
  public StructureData create(BlockPos min, BlockPos max, Set<BlockPos> extraPos) {
    // remove any tanks that are out of bounds, possible one got added in a layer later declared invalid
    // this might cause problems if we ever add a roof to the smeltery, possibly switch to a frame check?
    tanks.removeIf(pos -> !MultiblockStructureData.isWithin(pos, min, max));
    return new StructureData(min, max, extraPos, hasFloor, hasFrame, hasCeiling, ImmutableList.copyOf(tanks));
  }

  /**
   * Creates a copy of structure data for the client side
   * @param min  Min position
   * @param max  Max position
   * @return  Structure data
   */
  public StructureData createClient(BlockPos min, BlockPos max, List<BlockPos> tanks) {
    return new StructureData(min, max, Collections.emptySet(), hasFloor, hasFrame, hasCeiling, tanks);
  }

  @Override
  public StructureData detectMultiblock(World world, BlockPos master, Direction facing) {
    // clear tanks from last check before calling
    tanks.clear();
    return super.detectMultiblock(world, master, facing);
  }

  /**
   * Reads the structure data from NBT
   * @param  nbt  NBT tag
   * @return Structure data, or null if invalid
   */
  @Override
  @Nullable
  public StructureData readFromNBT(CompoundNBT nbt) {
    // add all tanks from NBT, will be picked up in the create call
    tanks.clear();
    tanks.addAll(readPosList(nbt, TAG_TANKS));
    return super.readFromNBT(nbt);
  }

  /**
   * Checks if the given block position is a valid slave
   * @param world  World instance
   * @param pos    Position to check, note it may be mutable
   * @return   True if its a valid slave
   */
  protected boolean isValidSlave(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this smeltery
    if (te instanceof IServantLogic) {
      return ((IServantLogic)te).isValidMaster(parent);
    }

    return true;
  }

  /**
   * Checks if this structure can expand up by one block
   * @return  True if this structure can expand
   */
  public boolean canExpand(StructureData data, World world) {
    BlockPos min = data.getMinPos();
    BlockPos to = data.getMaxPos().up();
    // want two positions one layer above the structure
    MultiblockResult result = detectLayer(world, new BlockPos(min.getX(), to.getY(), min.getZ()), to, pos -> {});
    setLastResult(result);
    return result.isSuccess();
  }


  /* Block checks */

  /** Return true for blocks valid at any location in the structure */
  protected abstract boolean isValidBlock(Block block);

  /** Return true for blocks valid in the structure floor */
  protected abstract boolean isValidFloor(Block block);

  /** Return true for blocks that serve as tanks */
  protected abstract boolean isValidTank(Block block);

  /** Return true for blocks valid in the structure walls */
  protected abstract boolean isValidWall(Block block);

  @Override
  protected boolean isValidBlock(World world, BlockPos pos, CuboidSide side, boolean isFrame) {
    // controller always is valid
    if (pos.equals(parent.getPos())) {
      return true;
    }
    if (!isValidSlave(world, pos)) {
      return false;
    }

    // floor has a smaller list
    BlockState state = world.getBlockState(pos);
    // treat frame blocks as walls, its more natural
    if (side == CuboidSide.FLOOR && !isFrame) {
      return isValidFloor(state.getBlock());
    }

    // add tanks to the internal lists
    if (isValidTank(state.getBlock())) {
      tanks.add(pos.toImmutable());
      return true;
    }
    return isValidWall(state.getBlock());
  }

  @Override
  public boolean shouldUpdate(World world, MultiblockStructureData structure, BlockPos pos, BlockState state) {
    if (structure.withinBounds(pos)) {
      // if its a part of the structure, need to update if its not a valid smeltery block
      if (structure.contains(pos)) {
        return !isValidBlock(state.getBlock());
      }
      // if not part of the actual structure, we only care if its a block that's not air in the inner section
      // in other words, ignore blocks added into the frame
      // note we don't do a check for a valid inner block, if it is a valid inner block we need to update to include it
      return structure.isInside(pos) && !state.isAir(world, pos);
    }

    // if its one block above, might be trying to expand upwards
    return structure.isDirectlyAbove(pos) && isValidWall(state.getBlock());
  }

  /** Extension of structure data to contain tanks list and the inside check */
  public static class StructureData extends MultiblockStructureData {
    /** Positions of all tanks in the structure area */
    @Getter
    private final List<BlockPos> tanks;
    /** Next position to check for inside checks */
    private BlockPos insideCheck;

    protected StructureData(BlockPos minPos, BlockPos maxPos, Set<BlockPos> extraPositions, boolean hasFloor, boolean hasFrame, boolean hasCeiling, List<BlockPos> tanks) {
      super(minPos, maxPos, extraPositions, hasFloor, hasFrame, hasCeiling);
      this.tanks = tanks;
    }

    /**
     * Checks if there are any tanks in this structure
     * @return  True if there are tanks
     */
    public boolean hasTanks() {
      return !tanks.isEmpty();
    }

    /**
     * Gets the next inside position in the structure
     * @param prev  Previous inside position
     * @return  Next inside position based on the previous one
     */
    private BlockPos getNextInsideCheck(@Nullable BlockPos prev) {
      BlockPos min = getMinInside();
      if (prev == null) {
        return min;
      }
      // smaller than min means the structure size changed
      if (prev.getX() < min.getX() || prev.getY() < min.getY() || prev.getZ() < min.getZ()) {
        return min;
      }

      BlockPos max = getMaxInside();
      // end of row
      if (prev.getZ() >= max.getZ()) {
        // end of layer
        if (prev.getX() >= max.getX()) {
          // top of structure
          if (prev.getY() >= max.getY()) {
            return min;
          } else {
            return new BlockPos(min.getX(), prev.getY() + 1, min.getZ());
          }
        } else {
          return new BlockPos(prev.getX() + 1, prev.getY(), min.getZ());
        }
      } else {
        return prev.add(0, 0, 1);
      }
    }

    /**
     * Gets the next inside position to check in the structure
     * @return  Next inside position based on the previous one
     */
    public BlockPos getNextInsideCheck() {
      insideCheck = getNextInsideCheck(insideCheck);
      return insideCheck;
    }

    /**
     * Gets the number of blocks making up the walls and floor
     * @return  Blocks in walls and floor
     */
    public int getPerimeterCount() {
      BlockPos min = getMinInside();
      BlockPos max = getMaxInside();
      int dx = max.getX() - min.getX();
      int dy = max.getY() - min.getY();
      int dz = max.getZ() - min.getZ();
      // 2 of the X and the Z wall, one of the floor
      return (2 * (dx * dy) + 2 * (dy * dz) + (dx * dz));
    }

    @Override
    public CompoundNBT writeClientNBT() {
      CompoundNBT nbt = super.writeClientNBT();
      nbt.put(TAG_TANKS, writePosList(tanks));
      return nbt;
    }

    /**
     * Writes this structure to NBT
     * @return  structure as NBT
     */
    @Override
    public CompoundNBT writeToNBT() {
      CompoundNBT nbt = super.writeToNBT();
      if (insideCheck != null) {
        nbt.put(TAG_INSIDE_CHECK, TagUtil.writePos(insideCheck));
      }
      return nbt;
    }
  }
}
