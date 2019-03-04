package slimeknights.tconstruct.library.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

import slimeknights.tconstruct.smeltery.tileentity.TileCasting;

public class FluidHandlerCasting implements IFluidHandler {

  private final FluidTankAnimated tank;
  private final TileCasting tileCasting;

  public FluidHandlerCasting(TileCasting tileCasting, FluidTankAnimated fluidTank) {
    this.tileCasting = tileCasting;
    this.tank = fluidTank;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    return tank.getTankProperties();
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    // this is where all the action happens
    if(resource == null || tileCasting.isStackInSlot(1)) {
      return 0;
    }
    Fluid fluid = resource.getFluid();

    // if empty, find a new recipe
    if(this.tank.getFluidAmount() == 0) {
      int capacity = tileCasting.initNewCasting(fluid, doFill);
      if(capacity > 0) {
        // new tank with the wanted capacity so we can simulate fill with the correct capacity
        IFluidTank calcTank = new FluidTank(resource.getFluid(), 0, capacity);

        // no extra checks needed for the tank since it's empty and we have to set the capacity anyway
        if(doFill) {
          tank.setCapacity(capacity);
          tank.setFluid(new FluidStack(resource.getFluid(), 0));
          calcTank = tank;
        }

        return calcTank.fill(resource, doFill);
      }
    }

    // non-empty tank. just try to fill
    return tank.fill(resource, doFill);
  }

  @Nullable
  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    if(resource == null || tank.getFluidAmount() == 0) {
      return null;
    }
    FluidStack fluidStack = tank.getFluid();
    assert fluidStack != null;
    if(fluidStack.getFluid() != resource.getFluid()) {
      return null;
    }

    // same fluid, k
    return this.drain(resource.amount, doDrain);
  }

  @Nullable
  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    FluidStack amount = tank.drain(maxDrain, doDrain);
    if(amount != null && doDrain) {
      if(tank.getFluidAmount() == 0) {
        tileCasting.reset();
      }
    }

    return amount;
  }
}
