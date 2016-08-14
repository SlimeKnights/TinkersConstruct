package slimeknights.tconstruct.library.fluid;

import java.lang.ref.WeakReference;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;

public class FluidHandlerExtractOnlyWrapper extends FluidHandlerConcatenate {

  // we hold a weak reference as we don't want the drains when storing the wrapper to keep old smeltery TEs from being collected
  // if you need this functionality in another tank, implement it directly rather than using a wrapper
  private final WeakReference<IFluidHandler> parent;

  public FluidHandlerExtractOnlyWrapper(IFluidHandler parent) {
    super(parent);
    this.parent = new WeakReference<IFluidHandler>(parent);
  }

  // checks if the parent is no longer available, for example the smeltery containing the tank was removed
  public boolean hasParent() {
    return parent.get() != null;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    if(parent.get() != null) {
      IFluidTankProperties[] iFluidTankPropertiesArray = parent.get().getTankProperties();
      if(iFluidTankPropertiesArray.length > 0) {
        IFluidTankProperties fluidTankProperties = parent.get().getTankProperties()[0];
        return new IFluidTankProperties[]{new FluidTankProperties(fluidTankProperties.getContents(), fluidTankProperties.getCapacity(), true, false)};
      }
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
