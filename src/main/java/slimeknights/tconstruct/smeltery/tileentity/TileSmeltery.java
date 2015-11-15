package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;

import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;

public class TileSmeltery extends TileEntity implements IMasterLogic, IUpdatePlayerListBox {

  protected static final int MAX_SIZE = 7;

  public boolean active;

  protected MultiblockSmeltery multiblock;
  protected int tick;

  public TileSmeltery() {
    multiblock = new MultiblockSmeltery(this);
  }

  @Override
  public void update() {
    if(this.worldObj.isRemote) {
      return;
    }
    tick = (tick + 1) % 100;

    if(!isActive()) {
      // check for smeltery once per second
      if(tick % 20 == 0) {
        checkSmelteryStructure();
      }
    }
  }

  /** Called by the servants */
  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {
    checkSmelteryStructure();
  }

  public void checkSmelteryStructure() {
    boolean wasActive = isActive();

    IBlockState state = this.worldObj.getBlockState(getPos());
    if(!(state.getBlock() instanceof BlockSmelteryController)) {
      active = false;
    }
    else {

      EnumFacing in = ((EnumFacing) state.getValue(BlockSmelteryController.FACING)).getOpposite();

      List<BlockPos> blocks = multiblock.detectMultiblock(this.worldObj, this.getPos().offset(in), MAX_SIZE);
      if(blocks.isEmpty()) {
        active = false;
      }
      else {
        // we found a valid smeltery. yay.
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), blocks);
        System.out.println("Formed Smeltery with " + blocks.size() + " blocks");
      }
    }

    // mark the block for updating so the smeltery controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.markBlockForUpdate(pos);
      this.markDirty();
    }
  }

  /* Network */

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);

    compound.setBoolean("active", active);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    active = compound.getBoolean("active");
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);

    boolean wasActive = active;

    readFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(isActive() != wasActive) {
      worldObj.markBlockForUpdate(pos);
    }
  }

  /* Getter */

  public boolean isActive() {
    return active;
  }
}
