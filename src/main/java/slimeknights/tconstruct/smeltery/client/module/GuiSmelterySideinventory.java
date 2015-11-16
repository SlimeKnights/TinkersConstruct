package slimeknights.tconstruct.smeltery.client.module;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.tools.client.module.GuiSideInventory;

public class GuiSmelterySideinventory extends GuiSideInventory {

  public static final ResourceLocation SLOT_LOCATION = GuiSmeltery.BACKGROUND;

  public GuiSmelterySideinventory(GuiMultiModule parent, Container container, int slotCount, int columns) {
    super(parent, container, slotCount, columns, false, true);

    GuiElement.defaultTexH = 256;
    GuiElement.defaultTexW = 256;
    slot = new GuiElementScalable(0, 166, 22, 18);
    slotEmpty = new GuiElementScalable(22, 166, 22, 18);
    yOffset = 0;
  }

  @Override
  protected boolean shouldDrawName() {
    return false;
  }

  @Override
  protected void updateSlots() {
    // adjust for the heat bar
    xOffset += 4;
    super.updateSlots();
    xOffset -= 4;
  }

  @Override
  protected int drawSlots(int xPos, int yPos) {
    this.mc.getTextureManager().bindTexture(SLOT_LOCATION);
    int ret = super.drawSlots(xPos, yPos);
    this.mc.getTextureManager().bindTexture(GUI_INVENTORY);
    return ret;
  }
}
