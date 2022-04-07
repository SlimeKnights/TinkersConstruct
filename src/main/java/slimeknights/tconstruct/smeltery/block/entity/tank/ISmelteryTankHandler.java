package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

// TODO: reassess
public interface ISmelteryTankHandler {
  /**
   * Updates the fluids in the tank with data from the packet, should only be called client side
   */
  void updateFluidsFromPacket(List<FluidStack> fluids);

  /**
   * Gets the smeltery tank
   * @return  Tank instance
   */
  SmelteryTank<?> getTank();

  /**
   * Gets the fluid capability for this smeltery. Provided here as the smeltery itself does not expose this
   * @return  Fluid capability
   */
  LazyOptional<IFluidHandler> getFluidCapability();

  /**
   * Called when the tank adds or removes a fluid to notify listeners
   * @param type   Type of the change
   * @param fluid  Fluid changed, may be empty if multiple fluids change (order change for example)
   */
  default void notifyFluidsChanged(FluidChange type, FluidStack fluid) {}

  /**
   * Adds a listener to the display listeners list
   * @param listener  Listener
   */
  void addDisplayListener(IDisplayFluidListener listener);

  /** Simple enum to make {@link #notifyFluidsChanged(FluidChange, FluidStack)} more readible */
  enum FluidChange {
    /** Fluid was added to the tank */
    ADDED,
    /** Fluid size changed */
    CHANGED,
    /** Fluid was removed from the block */
    REMOVED,
    /** Sent client side to signify the bottom most fluid is different */
    ORDER_CHANGED
  }
}
