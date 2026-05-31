package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Interface for blocks to be notified when the smeltery has a new bottommost fluid
 */
public interface IDisplayFluidListener {
  /**
   * Called when the display fluid changes0
   * @param fluid New display fluid, is safe to store (will not be modified)
   */
  void notifyDisplayFluidUpdated(FluidStack fluid);
}
