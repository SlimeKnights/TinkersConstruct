package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;

public class TileSmeltery extends TileInventory implements IMasterLogic, IUpdatePlayerListBox, IInventoryGui {

  protected static final int MAX_SIZE = 7;

  public boolean active;

  protected MultiblockSmeltery multiblock;
  protected int tick;

  public TileSmeltery() {
    super("gui.smeltery.name", 0);
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

      MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(this.worldObj, this.getPos().offset(in), MAX_SIZE);
      if(structure == null) {
        active = false;
      }
      else {
        // we found a valid smeltery. yay.
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), structure.blocks);
      }
    }

    // mark the block for updating so the smeltery controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.markBlockForUpdate(pos);
      this.markDirty();
    }
  }

  /* GUI */
  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerSmeltery(inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiSmeltery((ContainerSmeltery)createContainer(inventoryplayer, world, pos));
  }

  /* Network & Saving */

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
