package slimeknights.tconstruct.library.utils;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtil {

  /**
   * Used to ensure that a fluidstack is valid. Basically when you return a fluidstack,
   * you should ALWAYS take the fluid from the FluidRegistry. This isn't possible in all cases for us
   * hence we replace the FluidStack with a FluidStack containing the correct fluid.
   *
   * Example: Entity X melts into a specific fluid with specific NBT. However in game Fluid X is not the default fluid
   * anymore. We change the returned stack to contain the default fluid instead of the fluid used during setup.
   *
   * @return A save FluidStack or null if there is no valid fluid for the fluidstack
   */
  public static FluidStack getValidFluidStackOrNull(FluidStack possiblyInvalidFluidstack) {
    FluidStack fluidStack = possiblyInvalidFluidstack;
    if(!FluidRegistry.isFluidDefault(fluidStack.getFluid())) {
      Fluid fluid = FluidRegistry.getFluid(fluidStack.getFluid().getName());
      if(fluid != null) {
        fluidStack = new FluidStack(fluid, fluidStack.amount, fluidStack.tag);
      } else {
        fluidStack = null;
      }
    }
    return fluidStack;
  }
}
