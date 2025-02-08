package slimeknights.tconstruct.smeltery.client.screen.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;

/** Helper to handle clicking the fluid within a tank */
public interface ClickableTankModule {
  /** Gets the menu for this tank */
  AbstractContainerMenu getMenu();

  /** If true, this tank is hovered */
  boolean isHovered(int checkX, int checkY);

  /** Checks if the fluid is hovered. Assumes {@link #isHovered(int, int)} is true. */
  boolean isFluidHovered(int checkY);

  /** Called when the tank module is clicked to determine if an action happened */
  default boolean tryClick(int checkX, int checkY, int button, int indexOffset) {
    AbstractContainerMenu menu = getMenu();
    if (isHovered(checkX, checkY) && !menu.getCarried().isEmpty()) {
      int index = button + indexOffset;
      Minecraft minecraft = Minecraft.getInstance();
      assert minecraft.player != null && minecraft.gameMode != null;
      if (menu.clickMenuButton(minecraft.player, index)) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
        return true;
      }
    }
    return false;
  }
}
