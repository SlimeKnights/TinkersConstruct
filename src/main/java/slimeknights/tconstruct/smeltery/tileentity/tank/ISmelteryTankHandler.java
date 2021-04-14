package slimeknights.tconstruct.smeltery.tileentity.tank;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import slimeknights.tconstruct.fluids.IFluidHandler;

import java.util.List;

// TODO: reassess
public interface ISmelteryTankHandler extends BlockEntityProvider {
  /**
   * Updates the fluids in the tank with data from the packet, should only be called client side
   */
  void updateFluidsFromPacket(List<FluidVolume> fluids);

  /**
   * Gets the smeltery tank
   * @return  Tank instance
   */
  SmelteryTank getTank();

  /**
   * Gets the fluid capability for this smeltery. Provided here as the smeltery itself does not expose this
   * @return  Fluid capability
   */
  IFluidHandler getFluidCapability();

  /**
   * Called when the tank adds or removes a fluid to notify listeners
   * @param type   Type of the change
   * @param fluid  Fluid changed, may be empty if multiple fluids change (order change for example)
   */
  default void notifyFluidsChanged(FluidChange type, Fluid fluid) {}

  /**
   * Adds a listener to the display listeners list
   * @param listener  Listener
   */
  void addDisplayListener(IDisplayFluidListener listener);

  default BlockEntity getTileEntity() {
    return (BlockEntity) this;
  }

  /** Simple enum to make {@link #notifyFluidsChanged(FluidChange, Fluid)} more readible */
  enum FluidChange {
    /** Fluid was added to the tank */
    ADDED,
    /** Fluid size changed */
    CHANGED,
    /** Fluid was removed from the block */
    REMOVED,
    /** Sent client side to signify the bottom most fluid is different */
    ORDER_CHANGED;
  }
}
