package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;
import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideInventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiHeatingStructureFuelTank {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

  protected final GuiSmelterySideInventory sideinventory;
  protected final TileSmeltery smeltery;

  public GuiSmeltery(ContainerSmeltery container, TileSmeltery smeltery) {
    super(container);

    this.smeltery = smeltery;

    sideinventory = new GuiSmelterySideInventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                 smeltery, smeltery.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  // this is the same for both structures, but the superclass does not have (nor need) access to the side inventory
  @Override
  public void updateScreen() {
    super.updateScreen();

    // smeltery size changed
    if(smeltery.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
      // close screen
      this.mc.thePlayer.closeScreen();
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // we don't need to add the corner since the mouse is already reletive to the corner
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    // draw the scale
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);

    // draw the tooltips, if any
    // subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
    mouseX -= cornerX;
    mouseY -= cornerY;

    // Liquids
    List<String> tooltip = GuiUtil.drawTankTooltip(smeltery.getTank(), mouseX, mouseY, 8, 16, 60, 68);
    if(tooltip != null) {
      this.drawHoveringText(tooltip, mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    // draw liquids
    GuiUtil.drawGuiTank(smeltery.getTank(), 8 + cornerX, 16 + cornerY, scala.w, scala.h, this.zLevel);

    // update fuel info
    fuelInfo = smeltery.getFuelDisplay();
    drawFuel(71, 16, 12, 52);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(mouseButton == 0) {
      GuiUtil.handleTankClick(smeltery.getTank(), mouseX - cornerX, mouseY - cornerY, 8, 16, 60, 68);
    }
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

}
