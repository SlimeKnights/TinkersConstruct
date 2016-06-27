package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.fluid.FluidHandlerExtractOnlyWrapper;

/**
 * Drains allow access to the bottommost liquid in the smeltery.
 * They can insert and drain liquids from the smeltery.
 */
public class TileDrain extends TileSmelteryComponent {

  private FluidHandlerExtractOnlyWrapper drainFluidHandler;
  private WeakReference<TileEntity> oldSmeltery;

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return getSmeltery() != null;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      TileSmeltery smeltery = getSmeltery();
      if(facing == null) {
        if(drainFluidHandler == null || oldSmeltery == null || oldSmeltery.get() == null || !oldSmeltery.get().getPos().equals(smeltery.getPos())) {
          drainFluidHandler = new FluidHandlerExtractOnlyWrapper(smeltery.getTank());
          oldSmeltery = new WeakReference<TileEntity>(smeltery);
        }
        return (T) drainFluidHandler;
      }
      return (T) smeltery.getTank();
    }
    return super.getCapability(capability, facing);
  }
}
