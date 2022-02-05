package slimeknights.tconstruct.smeltery.client.screen;

import javax.annotation.Nullable;

/**
 * Interface for JEI support to determine the ingredient under the mouse
 */
public interface IScreenWithFluidTank {
  /**
   * Gets the ingredient under the mouse, typically a fluid
   * @param mouseX Mouse X
   * @param mouseY Mouse Y
   * @return Ingredient under mouse, or null if no ingredient. Does not need to handle item stacks
   */
  @Nullable
  Object getIngredientUnderMouse(double mouseX, double mouseY);
}
