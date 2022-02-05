package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

/**
 * Interface for blocks to be notified when the smeltery has a new bottommost fluid
 */
public interface IDisplayFluidListener {
  /** Property for fluid models */
  ModelProperty<FluidStack> PROPERTY = new ModelProperty<>();

  /**
   * Called when the display fluid changes0
   * @param fluid New display fluid, is safe to store (will not be modified)
   */
  void notifyDisplayFluidUpdated(FluidStack fluid);

  /**
   * Gets the position of the listener
   * @return  Position of listener
   */
  BlockPos getListenerPos();

  /** Makes the fluid contain 1000mb, or {@link FluidStack#EMPTY} if empty */
  static FluidStack normalizeFluid(FluidStack fluid) {
    if (fluid.isEmpty()) {
      return FluidStack.EMPTY;
    }
    fluid = fluid.copy();
    fluid.setAmount(FluidAttributes.BUCKET_VOLUME);
    return fluid;
  }
}
