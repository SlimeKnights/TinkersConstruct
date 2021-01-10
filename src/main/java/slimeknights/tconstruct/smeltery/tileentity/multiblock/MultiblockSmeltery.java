package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MultiblockSmeltery extends MultiblockCuboid {
  private static final String TAG_TANKS = "tanks";
  private static final String TAG_INSIDE_CHECK = "insideCheck";

  /** Parent smeltery instance */
  private final SmelteryTileEntity smeltery;
  /** Boolean to check if a tank is found between valid block checks */
  private final List<BlockPos> tanks = new ArrayList<>();

  public MultiblockSmeltery(SmelteryTileEntity smeltery) {
    super(true, false, false);
    this.smeltery = smeltery;
  }

  @Override
  public StructureData detectMultiblock(World world, BlockPos master, Direction facing) {
    tanks.clear();
    Set<BlockPos> positions = super.detectMultiblockPositions(world, master, facing);
    if (positions == null) {
      return null;
    }

    // tanks are possibly added on a layer that is later declared invalid, so remove those before doing checks
    tanks.removeIf(pos -> !positions.contains(pos));
    if (tanks.isEmpty()) {
      return null;
    }
    return new StructureData(positions, hasFloor, hasCeiling, ImmutableList.copyOf(tanks));
  }

  /**
   * Checks if the given block position is a valid slave
   * @param world  World instance
   * @param pos    Position to check
   * @return   True if its a valid slave
   */
  protected boolean isValidSlave(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this smeltery
    if (te instanceof IServantLogic) {
      return ((IServantLogic)te).isValidMaster(smeltery);
    }

    return true;
  }

  @Override
  protected boolean isValidBlock(World world, BlockPos pos, CuboidSide side, boolean isFrame) {
    // controller always is valid
    if (pos.equals(smeltery.getPos())) {
      return true;
    }
    if (!isValidSlave(world, pos)) {
      return false;
    }

    // floor has a smaller list
    BlockState state = world.getBlockState(pos);
    if (side == CuboidSide.FLOOR) {
      return TinkerTags.Blocks.SMELTERY_FLOOR.contains(state.getBlock());
    }

    // add tanks to the internal lists
    if (TinkerTags.Blocks.SMELTERY_TANKS.contains(state.getBlock())) {
      tanks.add(pos);
      return true;
    }
    return TinkerTags.Blocks.SMELTERY_WALL.contains(state.getBlock());
  }

  @Override
  public boolean shouldUpdate(World world, MultiblockStructureData structure, BlockPos pos, BlockState state) {
    if (structure.isInside(pos)) {
      // if its a part of the structure, need to update if its not a valid smeltery block
      if (structure.contains(pos)) {
        return !TinkerTags.Blocks.SMELTERY_WALL.contains(state.getBlock());
      // block is inside the structure, but not one of the blocks of the structure, means its blocking part of the structure, so update
      // note we don't do a check for a valid inner block, if it is a valid inner block we need to update to include it
      } else return !state.isAir(world, pos);
    }

    // if its one block above, might be trying to expand upwards
    return structure.isDirectlyAbove(pos) && TinkerTags.Blocks.SMELTERY_WALL.contains(state.getBlock());
  }

  /**
   * Reads the structure data from NBT
   * @param  nbt  NBT tag
   * @return Structure data, or null if invalid
   */
  @Override
  @Nullable
  public StructureData readFromNBT(CompoundNBT nbt) {
    Set<BlockPos> positions = readPosList(nbt, MultiblockStructureData.TAG_POSITIONS, ImmutableSet::builder);
    if (!positions.isEmpty()) {
      List<BlockPos> tanks = readPosList(nbt, TAG_TANKS, ImmutableList::builder);
      if (!tanks.isEmpty()) {
        StructureData structure = new StructureData(positions, hasFloor, hasCeiling, tanks);
        structure.insideCheck = TagUtil.readPos(nbt, TAG_INSIDE_CHECK);
        return structure;
      }
    }

    return null;
  }

  public static class StructureData extends MultiblockStructureData {

    /** Positions of all tanks in the structure area */
    private final List<BlockPos> tanks;
    /** Next position to check for inside checks */
    private BlockPos insideCheck;
    public StructureData(Set<BlockPos> positions, boolean hasFloor, boolean hasCeiling, List<BlockPos> tanks) {
      super(positions, hasFloor, hasCeiling);
      this.tanks = tanks;
    }

    /**
     * Gets the next inside position in the structure
     * @param prev  Previous inside position
     * @return  Next inside position based on the previous one
     */
		private BlockPos getNextInsideCheck(@Nullable BlockPos prev) {
      BlockPos min = getInsideMin();
		  if (prev == null) {
		    return min;
      }
		  // smaller than min means the structure size changed
		  if (prev.getX() < min.getX() || prev.getY() < min.getY() || prev.getZ() < min.getZ()) {
		    return min;
      }

		  BlockPos max = getInsideMax();
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
     * Writes this structure to NBT
     * @return  structure as NBT
     */
    @Override
    public CompoundNBT writeToNBT() {
      CompoundNBT nbt = super.writeToNBT();
      nbt.put(TAG_TANKS, writePosList(tanks));
      if (insideCheck != null) {
        nbt.put(TAG_INSIDE_CHECK, TagUtil.writePos(insideCheck));
      }
      return nbt;
    }
  }
}
