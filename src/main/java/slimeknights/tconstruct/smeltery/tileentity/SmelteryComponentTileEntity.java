package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.common.multiblock.ServantTileEntity;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import javax.annotation.Nullable;

public class SmelteryComponentTileEntity extends ServantTileEntity {

  public SmelteryComponentTileEntity() {
    this(TinkerSmeltery.smelteryComponent.get());
  }

  @SuppressWarnings("WeakerAccess")
  protected SmelteryComponentTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  // we send all our info to the client on load
  // TODO: should we?
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
    BlockPos master = this.getMasterPos();
    if (master != null && this.world != null) {
      TileEntity te = this.world.getTileEntity(master);
      if (te instanceof ISmelteryTankHandler) {
        return (ISmelteryTankHandler) te;
      }
    }
    return null;
  }
}
