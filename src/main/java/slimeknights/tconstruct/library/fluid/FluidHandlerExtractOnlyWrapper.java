package slimeknights.tconstruct.library.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;

public class FluidHandlerExtractOnlyWrapper extends FluidHandlerConcatenate {

  private final IFluidHandler parent;

  public FluidHandlerExtractOnlyWrapper(IFluidHandler parent) {
    super(parent);
    this.parent = parent;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    IFluidTankProperties[] iFluidTankPropertiesArray = parent.getTankProperties();
    if(iFluidTankPropertiesArray.length > 0) {
      IFluidTankProperties fluidTankProperties = parent.getTankProperties()[0];
      return new IFluidTankProperties[]{new FluidTankProperties(fluidTankProperties.getContents(), fluidTankProperties.getCapacity(), true, false)};
    }
    return EmptyFluidHandler.EMPTY_TANK_PROPERTIES_ARRAY;
  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    return null;
  }


}
