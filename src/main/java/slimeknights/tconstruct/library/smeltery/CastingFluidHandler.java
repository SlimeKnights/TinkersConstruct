package slimeknights.tconstruct.library.smeltery;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

import javax.annotation.Nonnull;

public class CastingFluidHandler implements IFluidHandler {

  private final FluidTankAnimated tank;
  private final CastingTileEntity tile;

  public CastingFluidHandler(CastingTileEntity tile, FluidTankAnimated tank) {
    this.tile = tile;
    this.tank = tank;
  }
  @Override
  public int getTanks() {
    return 1;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return this.tank.getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    System.out.println(String.format("isFluidValid=%s", this.tank.isFluidValid(tank, stack)));
    return this.tank.isFluidValid(tank, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || tile.isStackInSlot(1)) {
      return 0;
    }
    Fluid fluid = resource.getFluid();
    int capacity = tile.initNewCasting(fluid, action);
    if (capacity > 0) {
//      IFluidTank calcTank = new FluidTank(capacity);

      if (action == FluidAction.SIMULATE && tank.getCapacity() == 0) {
        tank.setCapacity(capacity);
        tank.setFluid(new FluidStack(resource.getFluid(), 0));
//        calcTank = tank;
//        return tank.fill(resource, action);
      }
    }

    return tank.fill(resource, action);
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || tank.getFluidAmount() == 0) {
      return FluidStack.EMPTY;
    }
    FluidStack fluidStack = tank.getFluid();
    assert !fluidStack.isEmpty();
    if (fluidStack.getFluid() != resource.getFluid()) {
      return FluidStack.EMPTY;
    }

    return this.drain(resource.getAmount(), action);
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack amount = tank.drain(maxDrain, action);
    if (!amount.isEmpty() && action == FluidAction.EXECUTE) {
      if (tank.getFluidAmount() == 0) {
        tile.reset();
      }
    }

    return amount;
  }
}
