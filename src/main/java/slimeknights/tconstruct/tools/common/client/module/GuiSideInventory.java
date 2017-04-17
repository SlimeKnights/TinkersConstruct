package slimeknights.tconstruct.tools.common.client.module;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.client.gui.GuiWidgetSlider;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.library.Util;

// a side inventory to be displayed to the left or right of another GUI
@SideOnly(Side.CLIENT)
public class GuiSideInventory extends GuiModule {

  protected GuiElementScalable overlap = GuiGeneric.overlap;
  protected GuiElement overlapTopLeft = GuiGeneric.overlapTopLeft;
  protected GuiElement overlapTopRight = GuiGeneric.overlapTopRight;
  protected GuiElement overlapBottomLeft = GuiGeneric.overlapBottomLeft;
  protected GuiElement overlapBottomRight = GuiGeneric.overlapBottomRight;
  protected GuiElement overlapTop = new GuiElement(7, 0, 7, 7, 64, 64); // same as borderTop but only 7 wide

  protected GuiElementScalable textBackground = GuiGeneric.textBackground;

  protected GuiElementScalable slot = GuiGeneric.slot;
  protected GuiElementScalable slotEmpty = GuiGeneric.slotEmpty;

  protected GuiElement sliderNormal = GuiGeneric.sliderNormal;
  protected GuiElement sliderLow = GuiGeneric.sliderLow;
  protected GuiElement sliderHigh = GuiGeneric.sliderHigh;
  protected GuiElement sliderTop = GuiGeneric.sliderTop;
  protected GuiElement sliderBottom = GuiGeneric.sliderBottom;
  protected GuiElementScalable sliderBackground = GuiGeneric.sliderBackground;

  // we use the chest gui as a preset for our parts
  protected static final ResourceLocation
      GUI_INVENTORY =
      Util.getResource("textures/gui/generic.png");

  protected GuiWidgetBorder border = new GuiWidgetBorder();

  protected int columns;
  protected int slotCount;

  protected int firstSlotId;
  protected int lastSlotId;

  protected int yOffset = 5;
  protected int xOffset;
  protected boolean connected;

  protected GuiWidgetSlider
      slider =
      new GuiWidgetSlider(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);

  public GuiSideInventory(GuiMultiModule parent, Container container, int slotCount, int columns) {
    this(parent, container, slotCount, columns, false, false);
  }

  public GuiSideInventory(GuiMultiModule parent, Container container, int slotCount, int columns, boolean rightSide, boolean connected) {
    super(parent, container, rightSide, false);

    this.connected = connected;

    this.columns = columns;
    this.slotCount = slotCount;

    this.xSize = columns * slot.w + border.w * 2;
    this.ySize = calcCappedYSize(slot.h * 10);

    if(connected) {
      if(right) {
        border.cornerTopLeft = overlapTopLeft;
        border.borderLeft = overlap;
        border.cornerBottomLeft = overlapBottomLeft;
      }
      else {
        border.cornerTopRight = overlapTopRight;
        border.borderRight = overlap;
        border.cornerBottomRight = overlapBottomRight;
      }
    }

    yOffset = 0;

    updateSlots();
  }

  protected boolean shouldDrawName() {
    if(this.inventorySlots instanceof BaseContainer) {
      return ((BaseContainer) this.inventorySlots).getInventoryDisplayName() != null;
    }

    return false;
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    if(slot.getSlotIndex() >= slotCount) {
      return false;
    }
    // all visible
    if(!slider.isEnabled()) {
      return true;
    }

    return firstSlotId <= slot.getSlotIndex() && lastSlotId > slot.getSlotIndex();
  }

  @Override
  public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
    return super.isMouseOverSlot(slotIn, mouseX, mouseY) && shouldDrawSlot(slotIn);
  }

  public void updateSlotCount(int newSlotCount) {
    // don't do extra stuff if it's not needed
    if(slotCount == newSlotCount) {
      return;
    }

    this.slotCount = newSlotCount;
    // called twice to get correct slider calculation
    updatePosition(parent.cornerX, parent.cornerY, parent.realWidth, parent.realHeight);
    updatePosition(parent.cornerX, parent.cornerY, parent.realWidth, parent.realHeight);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    // at most as big as the parent
    this.ySize = calcCappedYSize(parentSizeY - 10);
    // slider needed?
    if(getDisplayedRows() < getTotalRows()) {
      slider.enable();
      this.xSize = columns * slot.w + slider.width + 2 * border.w;
    }
    else {
      slider.disable();
      this.xSize = columns * slot.w + border.w * 2;
    }

    // update position
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // connected needs to move to the side
    if(connected) {
      if(yOffset == 0) {
        if(right) {
          border.cornerTopLeft = overlapTop;
        }
        else {
          border.cornerTopRight = overlapTop;
        }
      }

      xOffset = (border.w - 1) * (right ? -1 : 1);
      guiLeft += xOffset;
    }
    else {
      xOffset = 0;
    }

    // move it a bit
    this.guiTop += yOffset;

    border.setPosition(guiLeft, guiTop);
    border.setSize(xSize, ySize);

    int y = guiTop + border.h;
    int h = ySize - border.h * 2;

    if(shouldDrawName()) {
      y += textBackground.h;
      h -= textBackground.h;
    }
    slider.setPosition(guiLeft + columns * slot.w + border.w, y);
    slider.setSize(h);
    slider.setSliderParameters(0, getTotalRows() - getDisplayedRows(), 1);

    updateSlots();
  }

  private int getDisplayedRows() {
    return slider.height / slot.h;
  }

  private int getTotalRows() {
    int total = slotCount / columns;
    if(slotCount % columns != 0) {
      total++;
    }

    return total;
  }

  private int calcCappedYSize(int max) {
    int h = slot.h * getTotalRows();

    h = border.getHeightWithBorder(h);

    if(shouldDrawName()) {
      h += textBackground.h;
    }

    // not higher than the max
    while(h > max) {
      h -= slot.h;
    }
    return h;
  }

  // updates slot visibility
  protected void updateSlots() {
    firstSlotId = slider.getValue() * columns;
    lastSlotId = Math.min(slotCount, firstSlotId + getDisplayedRows() * columns);

    int xd = border.w + xOffset;
    int yd = border.h + yOffset;

    if(shouldDrawName()) {
      yd += textBackground.h;
    }

    for(Object o : inventorySlots.inventorySlots) {
      Slot slot = (Slot) o;
      if(shouldDrawSlot(slot)) {
        // calc position of the slot
        int offset = slot.getSlotIndex() - firstSlotId;
        int x = (offset % columns) * this.slot.w;
        int y = (offset / columns) * this.slot.h;

        slot.xPos = xd + x + 1;
        slot.yPos = yd + y + 1;

        if(this.right) {
          slot.xPos += parent.realWidth;
        }
        else {
          slot.xPos -= this.xSize;
        }
      }
      else {
        slot.xPos = 0;
        slot.yPos = 0;
      }
    }
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    if(shouldDrawName()) {
      String name = ((BaseContainer) inventorySlots).getInventoryDisplayName();
      this.fontRenderer.drawString(name, border.w, border.h - 1, 0x404040);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    guiLeft += border.w;
    guiTop += border.h;

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(GUI_INVENTORY);

    int x = guiLeft;// + border.w;
    int y = guiTop;// + border.h;
    int midW = xSize - border.w * 2;
    int midH = ySize - border.h * 2;

    border.draw();

    if(shouldDrawName()) {
      textBackground.drawScaledX(x, y, midW);
      y += textBackground.h;
    }

    this.mc.getTextureManager().bindTexture(GUI_INVENTORY);
    drawSlots(x, y);
/*
    // draw the connection to the main thing
    if(right) {
      x = guiLeft;
      y = guiTop;

      if(guiTop == parent.cornerY) {
        borderTop.drawScaledX(x, y, overlapTopLeft.w);
      }
      else {
        overlapTopLeft.draw(x, y);
      }
      y += cornerTopLeft.h;
      overlap.drawScaledY(x, y, midH);
      y += midH;
      if(guiBottom() == parent.cornerX + parent.realHeight) {
        borderBottom.drawScaledX(x, y, overlapBottomLeft.w);
      }
      else {
        overlapBottomLeft.draw(x, y);
      }
    }
    else if(slider.isEnabled()) {
      y = guiTop;

      borderTop.drawScaledX(x, y, cornerTopRight.w);
      y += cornerTopRight.h;
      overlap.drawScaledY(x, y, midH);
      y += midH;
      borderBottom.drawScaledX(x, y, cornerBottomRight.w);

      x = guiRight() - 1;
      y = guiTop;
      if(guiTop == parent.cornerY) {
        borderTop.drawScaledX(x, y, overlapTopRight.w);
      }
      else {
        overlapTopRight.draw(x, y);
      }
      y += cornerTopRight.h;
      overlap.drawScaledY(x, y, midH);
      y += midH;
      if(guiBottom() == parent.cornerX + parent.realHeight) {
        borderBottom.drawScaledX(x, y, overlapBottomRight.w);
      }
      else {
        overlapBottomRight.draw(x, y);
      }
    }
*/
    // slider
    if(slider.isEnabled()) {
      slider.update(mouseX, mouseY, !isMouseOverFullSlot(mouseX, mouseY) && isMouseInModule(mouseX, mouseY));
      slider.draw();

      updateSlots();
    }

    guiLeft -= border.w;
    guiTop -= border.h;
  }

  protected int drawSlots(int xPos, int yPos) {
    int width = columns * slot.w;
    int height = ySize - border.h * 2;

    int fullRows = (lastSlotId - firstSlotId) / columns;
    int y;
    for(y = 0; y < fullRows * slot.h && y < height; y += slot.h) {
      slot.drawScaledX(xPos, yPos + y, width);
    }

    // draw partial row and unused slots
    int slotsLeft = (lastSlotId - firstSlotId) % columns;
    if(slotsLeft > 0) {
      slot.drawScaledX(xPos, yPos + y, slotsLeft * slot.w);
      // empty slots that don't exist
      slotEmpty.drawScaledX(xPos + slotsLeft * slot.w, yPos + y, width - slotsLeft * slot.w);
    }

    return width;
  }
}
