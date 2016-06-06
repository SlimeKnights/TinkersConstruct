package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;

public class TileFaucet extends TileEntity implements ITickable {

  public static final int LIQUID_TRANSFER = 6; // how much liquid is transferred per operation
  public static final int TRANSACTION_AMOUNT = Material.VALUE_Ingot;

  // direction we're pulling liquid from. cached so we don't have to query the world every time. set on pour-begin
  public EnumFacing direction; // used to continue draining and for rendering
  public boolean isPouring;
  public boolean stopPouring;
  public FluidStack drained; // fluid is drained instantly and distributed over time. how much is left
  public boolean lastRedstoneState;

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

  public void handleRedstone(boolean hasSignal) {
    if(hasSignal != lastRedstoneState) {
      lastRedstoneState = hasSignal;
      if(hasSignal) {
        activate();
      }
    }
  }

  @Override
  public void update() {
    if(worldObj.isRemote) {
      return;
    }
    // nothing to do if not pouring
    if(!isPouring) {
      return;
    }

    if(drained != null) {
      // done draining
      if(drained.amount <= 0) {
        drained = null;
        // pour me another, if we want to.
        if(!stopPouring) {
          doTransfer();
        }
        else {
          reset();
        }
      }
      else {
        // reduce amount (cooldown)
        pour();
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
          this.isPouring = true;
          pour();

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

  // takes the liquid inside and executes one pouring step
  protected void pour() {
    if(drained == null) {
      return;
    }

    TileEntity fillTE = worldObj.getTileEntity(pos.down());
    if(fillTE instanceof IFluidHandler) {
      IFluidHandler toFill = (IFluidHandler) fillTE;

      FluidStack fillStack = drained.copy();
      fillStack.amount = Math.min(drained.amount, LIQUID_TRANSFER);

      // can we fill?
      int filled = toFill.fill(EnumFacing.UP, fillStack, false);
      if(filled > 0) {
        // transfer it
        this.drained.amount -= filled;
        fillStack.amount = filled;
        toFill.fill(EnumFacing.UP, fillStack, true);
      }
    }
    else {
      // filling TE got lost. reset. all liquid buffered is lost.
      reset();
    }
  }

  protected void reset() {
    isPouring = false;
    stopPouring = false;
    drained = null;
    direction = EnumFacing.DOWN; // invalid direction
    lastRedstoneState = false;

    // sync to clients
    if(worldObj != null && !worldObj.isRemote && worldObj instanceof WorldServer) {
      TinkerNetwork.sendToClients((WorldServer) worldObj, pos, new FaucetActivationPacket(pos, null));
    }
  }


  /* Load & Save */

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    if(drained != null) {
      drained.writeToNBT(compound);
      compound.setInteger("direction", direction.getIndex());
      //compound.setString("direction", direction.getName());
      compound.setBoolean("stop", stopPouring);
    }
    return compound;
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
    if(fluid == null) {
      reset();
    }
    else {
      drained = fluid;
      isPouring = true;
      direction = worldObj.getBlockState(pos).getValue(BlockFaucet.FACING);
    }
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    readFromNBT(pkt.getNbtCompound());
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    readFromNBT(tag);
  }
}
