package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.fluid.FluidHandlerExtractOnlyWrapper;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

/**
 * Drains allow access to the bottommost liquid in the smeltery.
 * They can insert and drain liquids from the smeltery.
 */
public class TileDrain extends TileSmelteryComponent {

  private FluidHandlerExtractOnlyWrapper drainFluidHandler;
  private WeakReference<TileEntity> oldSmelteryTank;

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      TileEntity te = getMaster();
      return te != null && te instanceof ISmelteryTankHandler;
    }
    return super.hasCapability(capability, facing);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      TileEntity te = getMaster();
      if(te == null || !(te instanceof ISmelteryTankHandler)) {
        return super.getCapability(capability, facing);
      }

      SmelteryTank tank = ((ISmelteryTankHandler) te).getTank();

      if(facing == null) {
        // check if the TE's equal rather than just the position
        // otherwise we could still be referencing a TE from a smeltery that was broken and replaced (garbage collector being slow to grab the TE)
        if(drainFluidHandler == null || oldSmelteryTank.get() == null
            || oldSmelteryTank == null || !drainFluidHandler.hasParent()
            || !oldSmelteryTank.get().equals(te)) {
          drainFluidHandler = new FluidHandlerExtractOnlyWrapper(tank);
          oldSmelteryTank = new WeakReference<TileEntity>(te);
        }
        return (T) drainFluidHandler;
      }
      return (T) tank;
    }
    return super.getCapability(capability, facing);
  }
}
