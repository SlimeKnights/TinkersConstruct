package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.client.module.GuiSearedFurnaceSideInventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSearedFurnace;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;

public class GuiSearedFurnace extends GuiHeatingStructureFuelTank {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/seared_furnace.png");

  protected GuiElement flame = new GuiElementScalable(176, 76, 28, 28, 256, 256);

  protected final GuiSearedFurnaceSideInventory sideinventory;
  protected final TileSearedFurnace furnace;

  public GuiSearedFurnace(ContainerSearedFurnace container, TileSearedFurnace tile) {
    super(container);

    this.furnace = tile;

    sideinventory = new GuiSearedFurnaceSideInventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                      furnace, furnace.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  // this is the same for both structures, but the superclass does not have (nor need) access to the side inventory
  @Override
  public void updateScreen() {
    super.updateScreen();

    // furnace size changed
    if(furnace.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
      // close screen
      this.mc.player.closeScreen();
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // we don't need to add the corner since the mouse is already relative to the module's corner
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    // draw the tooltips, if any
    // subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
    mouseX -= cornerX;
    mouseY -= cornerY;

    // Fuel tooltips
    if(71 <= mouseX && mouseX < 83 && 16 <= mouseY && mouseY < 68) {
      drawFuelTooltip(mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    // draw the flame, shows how much fuel is left of the last consumed liquid
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    float fuel = furnace.getFuelPercentage();
    if(fuel > 0) {
      GuiElement flame = this.flame;
      int height = 1 + Math.round(fuel * (flame.h - 1));
      int x = 26 + cornerX;
      int y = 41 + cornerY + flame.h - height;

      GuiScreen.drawModalRectWithCustomSizedTexture(x, y, flame.x, flame.y + flame.h - height, flame.w, height, flame.texW, flame.texH);
    }

    // update fuel info
    fuelInfo = furnace.getFuelDisplay();
    drawFuel(71, 16, 12, 52);
  }

}
