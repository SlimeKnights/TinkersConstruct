package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.TankFluidUpdatePacket;

public class TileFaucet extends TileEntity implements ITickable {

  public static final int LIQUID_TRANSFER = 6; // how much liquid is transferred per operation
  public static final int TRANSACTION_AMOUNT = Material.VALUE_Ingot;

  // direction we're pulling liquid from. cached so we don't have to query the world every time. set on pour-begin
  public EnumFacing direction; // used to continue draining and for rendering
  public boolean isPouring;
  public boolean stopPouring;
  public FluidStack drained; // fluid is drained instantly and distributed over time. how much is left

  public TileFaucet() {
    reset();
  }

  // begin pouring
  public boolean activate() {
    IBlockState state = worldObj.getBlockState(pos);
    // invalid state
    if(!state.getPropertyNames().contains(BlockFaucet.FACING)) {
      return false;
    }

    // already pouring? we want to stop then
    if(isPouring) {
      stopPouring = true;
      return true;
    }

    direction = worldObj.getBlockState(pos).getValue(BlockFaucet.FACING);
    doTransfer();
    return isPouring;
  }

  @Override
  public void update() {
    // nothing to do if not pouring
    if(!isPouring) {
      return;
    }

    if(drained != null) {
      // reduce amount (cooldown)
      drained.amount -= LIQUID_TRANSFER;
      // done draining
      if(drained.amount < 0) {
        drained = null;
        // pour me another, if we want to.
        if(!stopPouring) {
          doTransfer();
        }
        else {
          reset();
          // sync to clients
          if(!worldObj.isRemote && worldObj instanceof WorldServer) {
            TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FaucetActivationPacket(pos, null));
          }
        }
      }
    }
  }

  protected void doTransfer() {
    // still got content left
    if(drained != null) {
      return;
    }
    TileEntity drainTE = worldObj.getTileEntity(pos.offset(direction));
    TileEntity fillTE = worldObj.getTileEntity(pos.down());
    if(drainTE instanceof IFluidHandler && fillTE instanceof IFluidHandler) {
      IFluidHandler toDrain = (IFluidHandler) drainTE;
      IFluidHandler toFill = (IFluidHandler) fillTE;

      // can we drain?
      FluidStack drained = toDrain.drain(direction, TRANSACTION_AMOUNT, false);
      if(drained != null) {
        // can we fill?
        int filled = toFill.fill(EnumFacing.UP, drained, false);
        if(filled > 0) {
          // drain the liquid and transfer it, buffer the amount for delay
          this.drained = toDrain.drain(direction, filled, true);
          toFill.fill(EnumFacing.UP, this.drained, true);
          this.isPouring = true;

          // sync to clients
          if(!worldObj.isRemote && worldObj instanceof WorldServer) {
            TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FaucetActivationPacket(pos, drained));
          }

          return;
        }
      }
    }
    // draining unsuccessful
    reset();
  }

  protected void reset() {
    isPouring = false;
    stopPouring = false;
    drained = null;
    direction = EnumFacing.DOWN; // invalid direction
  }


  /* Load & Save */

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    if(drained != null) {
      drained.writeToNBT(compound);
      compound.setInteger("direction", direction.getIndex());
      //compound.setString("direction", direction.getName());
      compound.setBoolean("stop", stopPouring);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    drained = FluidStack.loadFluidStackFromNBT(compound);

    if(drained != null) {
      isPouring = true;
      direction = EnumFacing.values()[compound.getInteger("direction")];
      //direction = EnumFacing.valueOf(compound.getString("direction"));
      stopPouring = compound.getBoolean("stop");
    }
    else {
      reset();
    }
  }

  public void onActivationPacket(FluidStack fluid) {
    drained = fluid;
    isPouring = fluid == null;
    direction = worldObj.getBlockState(pos).getValue(BlockFaucet.FACING);
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
    readFromNBT(pkt.getNbtCompound());
  }
}
