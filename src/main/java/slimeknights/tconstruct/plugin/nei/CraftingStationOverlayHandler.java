package slimeknights.tconstruct.plugin.nei;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import codechicken.nei.recipe.DefaultOverlayHandler;
import slimeknights.tconstruct.tools.client.GuiCraftingStation;

public class CraftingStationOverlayHandler extends DefaultOverlayHandler {

  // we can move from player inventory as well as extra-chest inventory!
  @Override
  public boolean canMoveFrom(Slot slot, GuiContainer gui) {
    if(gui instanceof GuiCraftingStation) {
      GuiCraftingStation guiStation = (GuiCraftingStation) gui;
      if(guiStation.isSlotInChestInventory(slot)) {
        return true;
      }
    }
    return super.canMoveFrom(slot, gui);
  }
}
