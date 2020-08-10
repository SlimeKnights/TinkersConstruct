package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

public class SmelteryComponentTileEntity extends MultiServantLogic {

  public SmelteryComponentTileEntity() {
    this(TinkerSmeltery.smelteryComponent.get());
  }

  @SuppressWarnings("WeakerAccess")
  protected SmelteryComponentTileEntity(TileEntityType<?> tileEntityTypeIn) {
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
    this.read(this.getBlockState(), pkt.getNbtCompound());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return this.write(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(BlockState state, CompoundNBT tag) {
    this.read(state, tag);
  }

  /**
   * Gets a tile entity at the position of the master that contains a ISmelteryTankHandler
   *
   * @return null if the TE is not an ISmelteryTankHandler or if the master is missing
   */
  @Nullable
  protected ISmelteryTankHandler getSmelteryTankHandler() {
    if (this.getHasMaster() && this.world != null) {
      TileEntity te = this.world.getTileEntity(this.getMasterPosition());
      if (te instanceof ISmelteryTankHandler) {
        return (ISmelteryTankHandler) te;
      }
    }
    return null;
  }
}
