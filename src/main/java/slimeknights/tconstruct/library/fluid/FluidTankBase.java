package slimeknights.tconstruct.library.fluid;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankBase<T extends MantleTileEntity> extends FluidTank {

  protected T parent;

  public FluidTankBase(int capacity, T parent) {
    super(capacity);
    this.parent = parent;
  }

  @Override
  public int fillInternal(FluidStack resource, boolean doFill) {
    int amount = super.fillInternal(resource, doFill);
    if(amount > 0 && doFill) {
      sendUpdate(amount);
    }
    return amount;
  }

  @Override
  public FluidStack drainInternal(int maxDrain, boolean doDrain) {
    FluidStack fluid = super.drainInternal(maxDrain, doDrain);
    if(fluid != null && doDrain) {
      sendUpdate(-fluid.amount);
    }
    return fluid;
  }

  protected void sendUpdate(int amount) {
    if(amount != 0) {
      World world = parent.getWorld();
      if(!world.isRemote) {
        TinkerNetwork.sendToClients((WorldServer) world, parent.getPos(), new FluidUpdatePacket(parent.getPos(), this.getFluid()));
      }
    }
  }

  @Override
  public void setCapacity(int capacity) {
    this.capacity = capacity;

    // reduce the fluid size if its over the new capacity
    if(this.fluid != null && this.fluid.amount > capacity) {
      this.drain(this.fluid.amount - capacity, true);
    }
  }

  @Override
  protected void onContentsChanged() {
    // updates the tile entity for the sake of things that detect when contents change (such as comparators)
    if(parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }
    // make sure the chunk knows data changed
    parent.markDirtyFast();
  }
}
