package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import slimeknights.tconstruct.library.fluid.FluidHandlerExtractOnlyWrapper;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * Drains allow access to the bottommost liquid in the smeltery.
 * They can insert and drain liquids from the smeltery.
 */
public class TileDrain extends TileSmelteryComponent {

  private FluidHandlerExtractOnlyWrapper drainFluidHandler;
  private WeakReference<ISmelteryTankHandler> oldSmelteryTank;

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      ISmelteryTankHandler te = getSmelteryTankHandler();
      return te != null && te.getTank() != null;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      ISmelteryTankHandler te = this.getSmelteryTankHandler();
      if(te == null) {
        return super.getCapability(capability, facing);
      }

      SmelteryTank tank = te.getTank();
      if (tank == null) {
        return super.getCapability(capability, facing);
      }

      if(facing == null) {
        // check if the TE's equal rather than just the position
        // otherwise we could still be referencing a TE from a smeltery that was broken and replaced (garbage collector being slow to grab the TE)
        if(drainFluidHandler == null || oldSmelteryTank.get() == null
            || oldSmelteryTank == null || !drainFluidHandler.hasParent()
            || !te.equals(oldSmelteryTank.get())) {
          drainFluidHandler = new FluidHandlerExtractOnlyWrapper(tank);
          oldSmelteryTank = new WeakReference<>(te);
        }
        return (T) drainFluidHandler;
      }
      return (T) tank;
    }
    return super.getCapability(capability, facing);
  }
}
