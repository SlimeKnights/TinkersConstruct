package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.block.MultiblockControllerBlock;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;

import javax.annotation.Nonnull;

public abstract class MultiblockTile<T extends MultiblockDetection> extends InventoryTileEntity implements IMasterLogic {

  public static final String TAG_ACTIVE = "active";
  public static final String TAG_MINPOS = "minPos";
  public static final String TAG_MAXPOS = "maxPos";
  protected static final int MAX_SIZE = 9; // consistancy by this point. All others do 9x9
  protected boolean active;
  // Info about the structure/multiblock
  protected MultiblockDetection.MultiblockStructure info;
  protected T multiblock;
  /** smallest coordinate INSIDE the multiblock */
  protected BlockPos minPos;
  /** biggest coordinate INSIDE the multiblock */
  protected BlockPos maxPos;

  public MultiblockTile(TileEntityType<?> type, ITextComponent name, int inventorySize) {
    super(type, name, inventorySize);
  }

  public MultiblockTile(TileEntityType<?> type, ITextComponent name, int inventorySize, int maxStackSize) {
    super(type, name, inventorySize, maxStackSize);
  }

  /** Call in the constructor. Set the multiblock */
  protected void setMultiblock(T multiblock) {
    this.multiblock = multiblock;
  }

  public BlockPos getMinPos() {
    return minPos;
  }

  public BlockPos getMaxPos() {
    return maxPos;
  }

  /** Called by the servants */
  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {
    checkMultiblockStructure();
  }


  // Checks if the tank is fully built and updates status accordingly
  public void checkMultiblockStructure() {
    System.out.println("checkMultiblockStructure()");
    boolean wasActive = active;

    BlockState state = this.getWorld().getBlockState(getPos());
    if(!(state.getBlock() instanceof MultiblockControllerBlock)) {
      active = false;
      System.out.println("!instanceof MultiblockControllerBlock");
    }
    else {
      Direction in = state.get(MultiblockControllerBlock.FACING).getOpposite();

      // we only check if the chunks we want to check are loaded. Otherwise we assume the previous state is/was correct
      if(info == null || multiblock.checkIfMultiblockCanBeRechecked(world, info)) {
        MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(this.getWorld(), this.getPos().offset(in), MAX_SIZE);
        if(structure == null) {
          active = false;
          updateStructureInfoInternal(null);
        }
        else {
          // we found a valid tank. booyah!
          active = true;
          MultiblockDetection.assignMultiBlock(this.getWorld(), this.getPos(), structure.blocks);
          updateStructureInfoInternal(structure);
          // we still have to update since something caused us to rebuild our stats
          // might be the tank size changed
          if(wasActive) {
            this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
          }
        }
      }
    }

    // mark the block for updating so the controller block updates its graphics
    if(wasActive != active) {
      this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
      this.markDirty();
    }
  }

  protected final void updateStructureInfoInternal(MultiblockDetection.MultiblockStructure structure) {
    info = structure;

    if(structure == null) {
      structure = new MultiblockDetection.MultiblockStructure(0, 0, 0, ImmutableList.of(this.pos));
    }

    if(info != null) {
      minPos = info.minPos.add(1, 1, 1); // add walls and floor
      maxPos = info.maxPos.add(-1, hasCeiling() ? -1 : 0, -1); // subtract walls, no ceiling
    }
    else {
      minPos = maxPos = this.pos;
    }
    updateStructureInfo(structure);
  }

  /** if true the maxPos will be adjusted accordingly that the structure has no ceiling */
  protected boolean hasCeiling() {
    return true;
  }

  protected abstract void updateStructureInfo(MultiblockDetection.MultiblockStructure structure);

  public boolean isActive() {
    return active && (getWorld() == null || getWorld().isRemote || info != null);
  }

  public void setInvalid() {
    this.active = false;
    updateStructureInfoInternal(null);
  }

  @Override
  public void validate() {
    super.validate();
    // on validation we set active to false so the tank checks anew if it's formed
    active = false;
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putBoolean(TAG_ACTIVE, active);
    tag.put(TAG_MINPOS, TagUtil.writePos(minPos));
    tag.put(TAG_MAXPOS, TagUtil.writePos(maxPos));

    return super.write(tag);
  }

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    active = tag.getBoolean(TAG_ACTIVE);
    minPos = TagUtil.readPos(tag.getCompound(TAG_MINPOS));
    maxPos = TagUtil.readPos(tag.getCompound(TAG_MAXPOS));
    super.read(state, tag);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT tag = new CompoundNBT();
    this.write(tag);
    return new SUpdateTileEntityPacket(this.getPos(), -999, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    boolean wasActive = active;

    this.read(this.getBlockState(), pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(active != wasActive) {
      BlockState state = getWorld().getBlockState(getPos());
      getWorld().notifyBlockUpdate(getPos(), state, state, 3);
    }
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return write(new CompoundNBT());
  }

  /** Returns true if it's a client world, false if no world or server */
  public boolean isClientWorld() {
    return this.getWorld() != null && this.getWorld().isRemote;
  }

  /** Returns true if it's a server world, false if no world or client */
  public boolean isServerWorld() {
    return this.getWorld() != null && !this.getWorld().isRemote;
  }
}
