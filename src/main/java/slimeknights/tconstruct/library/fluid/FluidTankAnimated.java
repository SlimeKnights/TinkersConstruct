package slimeknights.tconstruct.library.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankAnimated extends FluidTank {

  public float renderOffset;
  private final TileEntity parent;

  public FluidTankAnimated(int capacity, TileEntity parent) {
    super(capacity);
    this.parent = parent;
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    int amount = super.fill(resource, doFill);
    if(amount > 0 && doFill) {
      sendUpdate(amount);
    }
    return amount;
  }

  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    FluidStack fluid = super.drain(resource, doDrain);
    if(fluid != null && doDrain) {
      sendUpdate(-fluid.amount);
    }
    return fluid;
  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    FluidStack fluid = super.drain(maxDrain, doDrain);
    if(fluid != null && doDrain) {
      sendUpdate(-fluid.amount);
    }
    return fluid;
  }

  protected void sendUpdate(int amount) {
    if(amount != 0) {
      renderOffset += amount;
      World world = parent.getWorld();
      if(!world.isRemote && world instanceof WorldServer) {
        TinkerNetwork.sendToClients((WorldServer) world, parent.getPos(), new FluidUpdatePacket(parent.getPos(), this.getFluid()));
      }
    }
  }

  @Override
  protected void onContentsChanged() {
    // updates the tile entity for the sake of things that detect when contents change (such as comparators)
    if(parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }
  }
}
