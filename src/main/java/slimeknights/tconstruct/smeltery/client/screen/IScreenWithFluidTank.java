package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.client.renderer.Rect2i;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Interface for JEI support to determine the ingredient under the mouse
 */
public interface IScreenWithFluidTank {
  /**
   * Gets the ingredient under the mouse, typically a fluid
   * @param mouseX Mouse X
   * @param mouseY Mouse Y
   * @return Fluid under mouse, or empty if no fluid.
   */
  @Nullable
  FluidLocation getFluidUnderMouse(int mouseX, int mouseY);

  /** Return from the fluid under mouse, maps to a JEI fluid */
  record FluidLocation(FluidStack fluid, Rect2i location) {}
}
