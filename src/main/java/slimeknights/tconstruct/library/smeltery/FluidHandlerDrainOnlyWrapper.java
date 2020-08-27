package slimeknights.tconstruct.library.smeltery;

import com.sun.istack.internal.NotNull;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.AlloyTankTileEntity;

public class FluidHandlerDrainOnlyWrapper implements IFluidHandler {

  private final FluidTankAnimated tank;
  private final AlloyTankTileEntity tile;

  public FluidHandlerDrainOnlyWrapper(AlloyTankTileEntity tile, FluidTankAnimated tank) {
    this.tile = tile;
    this.tank = tank;
  }
  @Override
  public int getTanks() {
    return 1;
  }

  @NotNull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return this.tank.getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
    return this.tank.isFluidValid(tank, stack);
  }

  // Can't fill (bucket stuff in)
  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0;
  }

  @NotNull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || tank.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return drain(resource.getAmount(), action);
  }

  @NotNull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    int drained = maxDrain;
    if (this.tank.getFluidAmount() < drained)  {
      drained = this.tank.getFluidAmount();
    }
    FluidStack stack = new FluidStack(this.tank.getFluid().getFluid(), drained);
    if (action.execute() && drained > 0) {
      this.tank.getFluid().shrink(drained);
    }
    return stack;
  }
}
