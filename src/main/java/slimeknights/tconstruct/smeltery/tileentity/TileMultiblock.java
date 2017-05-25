package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.block.BlockMultiblockController;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;

public abstract class TileMultiblock<T extends MultiblockDetection> extends TileInventory implements IMasterLogic {

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

  public TileMultiblock(String name, int inventorySize) {
    super(name, inventorySize);
  }

  public TileMultiblock(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);
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
    boolean wasActive = active;

    IBlockState state = this.getWorld().getBlockState(getPos());
    if(!(state.getBlock() instanceof BlockMultiblockController)) {
      active = false;
    }
    else {
      EnumFacing in = state.getValue(BlockMultiblockController.FACING).getOpposite();

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
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);

    compound.setBoolean(TAG_ACTIVE, active);
    compound.setTag(TAG_MINPOS, TagUtil.writePos(minPos));
    compound.setTag(TAG_MAXPOS, TagUtil.writePos(maxPos));

    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    active = compound.getBoolean(TAG_ACTIVE);
    minPos = TagUtil.readPos(compound.getCompoundTag(TAG_MINPOS));
    maxPos = TagUtil.readPos(compound.getCompoundTag(TAG_MAXPOS));
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    this.writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    boolean wasActive = active;

    readFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(active != wasActive) {
      IBlockState state = getWorld().getBlockState(getPos());
      getWorld().notifyBlockUpdate(getPos(), state, state, 3);
    }
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
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
