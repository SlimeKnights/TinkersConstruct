package slimeknights.tconstruct.smeltery.client.module;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.client.module.GuiSideInventory;

public class GuiSmelterySideinventory extends GuiSideInventory {

  public static final ResourceLocation SLOT_LOCATION = GuiSmeltery.BACKGROUND;

  protected final TileSmeltery smeltery;

  protected GuiElement progressBar = new GuiElementScalable(176, 150, 3, 16, 256, 256);
  protected GuiElement unprogressBar = new GuiElementScalable(179, 150, 3, 16);

  public GuiSmelterySideinventory(GuiMultiModule parent, Container container, TileSmeltery smeltery, int slotCount, int columns) {
    super(parent, container, slotCount, columns, false, true);

    this.smeltery = smeltery;

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

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    this.mc.getTextureManager().bindTexture(SLOT_LOCATION);

    // draw the "heat" bars for each slot
    for(Slot slot : (List<Slot>)inventorySlots.inventorySlots) {
      if(slot.getHasStack() && shouldDrawSlot(slot)) {
        float progress = smeltery.getMeltingProgress(slot.getSlotIndex());

        if(progress == Float.NaN || progress > 1f) {
          continue;
        }

        GuiElement bar = progressBar;
        if(progress < 0) {
          bar = unprogressBar;
          progress = 1f;
        }

        int height = Math.round(progress * bar.h);
        int x = slot.xDisplayPosition - 10 + this.xSize;
        int y = slot.yDisplayPosition + bar.h - height;

        GuiScreen.drawModalRectWithCustomSizedTexture(x, y, bar.x, bar.y + bar.h - height, bar.w, height, bar.texW, bar.texH);
      }
    }
  }
}
