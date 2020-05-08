package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.tileentity.SmelteryTileEntities;

import javax.annotation.Nonnull;

public class SmelteryComponentTileEntity extends MultiServantLogic {

  public SmelteryComponentTileEntity() {
    this(SmelteryTileEntities.SMELTERY_COMPONENT);
  }

  public SmelteryComponentTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  // we send all our info to the client on load
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT tag = this.write(new CompoundNBT());
    return new SUpdateTileEntityPacket(this.getPos(), -999, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    super.onDataPacket(net, pkt);
    this.read(pkt.getNbtCompound());
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return this.write(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(@Nonnull CompoundNBT tag) {
    this.read(tag);
  }

  /**
   * Gets a tile entity at the position of the master that contains a ISmelteryTankHandler
   *
   * @return null if the TE is not an ISmelteryTankHandler or if the master is missing
   */
  protected ISmelteryTankHandler getSmelteryTankHandler() {
    if (this.getHasMaster()) {
      TileEntity te = this.getWorld().getTileEntity(this.getMasterPosition());
      if (te instanceof ISmelteryTankHandler) {
        return (ISmelteryTankHandler) te;
      }
    }
    return null;
  }
}
