package slimeknights.tconstruct.smeltery.tileentity.inventory;

import lombok.AllArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraftforge.fluids.capability.IFluidHandler;

@AllArgsConstructor
public class DuctTankWrapper implements IFluidHandler {
  private final IFluidHandler parent;
  private final DuctItemHandler itemHandler;


  /* Properties */

  @Override
  public int getTanks() {
    return parent.getTanks();
  }

  @Override
  public FluidVolume getFluidInTank(int tank) {
    return parent.getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return parent.getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, FluidVolume stack) {
    return stack.getFluid() == itemHandler.getFluid();
  }


  /* Interactions */

  @Override
  public int fill(FluidVolume resource, FluidAction action) {
    if (resource.isEmpty() || resource.getFluid() != itemHandler.getFluid()) {
      return 0;
    }
    return parent.fill(resource, action);
  }

  @Override
  public FluidVolume drain(int maxDrain, FluidAction action) {
    Fluid fluid = itemHandler.getFluid();
    if (fluid == Fluids.EMPTY) {
      return TinkerFluids.EMPTY;
    }
    return parent.drain(new FluidVolume(fluid, maxDrain), action);
  }

  @Override
  public FluidVolume drain(FluidVolume resource, FluidAction action) {
    if (resource.isEmpty() || resource.getFluid() != itemHandler.getFluid()) {
      return TinkerFluids.EMPTY;
    }
    return parent.drain(resource, action);
  }
}
