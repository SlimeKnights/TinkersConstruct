package tconstruct.tools.client.module;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiElementScalable;
import tconstruct.common.client.gui.GuiModule;
import tconstruct.common.client.gui.GuiPartSlider;
import tconstruct.library.Util;
import tconstruct.common.client.gui.GuiMultiModule;

// a side inventory to be displayed to the left or right of another GUI
@SideOnly(Side.CLIENT)
public class GuiSideInventory extends GuiModule {

  // first one sets default texture w/h
  private static final GuiElement cornerTopLeft = new GuiElement(0, 0, 7, 7, 64, 64);
  private static final GuiElement cornerTopRight = new GuiElement(64 - 7, 0, 7, 7);
  private static final GuiElement cornerBottomLeft = new GuiElement(0, 64 - 7, 7, 7);
  private static final GuiElement cornerBottomRight = new GuiElement(64 - 7, 64 - 7, 7, 7);

  private static final GuiElementScalable borderTop = new GuiElementScalable(7, 0, 64 - 7 - 7, 7);
  private static final GuiElementScalable borderBottom = new GuiElementScalable(7, 64 - 7, 64 - 7 - 7, 7);
  private static final GuiElementScalable borderLeft = new GuiElementScalable(0, 7, 7, 64 - 7 - 7);
  private static final GuiElementScalable borderRight = new GuiElementScalable(64 - 7, 7, 7, 64 - 7 - 7);

  private static final GuiElementScalable slot = new GuiElementScalable(7, 7, 18, 18);
  private static final GuiElementScalable slotEmpty = new GuiElementScalable(7 + 18, 7, 18, 18);

  private static final GuiElement sliderNormal = new GuiElement(7, 25, 12, 15);
  private static final GuiElement sliderLow = new GuiElement(7 + 12, 25, 12, 15);
  private static final GuiElement sliderHigh = new GuiElement(7 + 12 + 12, 25, 12, 15);
  private static final GuiElement sliderTop = new GuiElement(43, 7, 14, 1);
  private static final GuiElement sliderBottom = new GuiElement(43, 38, 14, 1);
  private static final GuiElementScalable sliderBackground = new GuiElementScalable(43, 8, 14, 30);

  // we use the chest gui as a preset for our parts
  private static final ResourceLocation
      GUI_INVENTORY =
      Util.getResource("textures/gui/generic.png");

  private int columns;
  private int slotCount;

  private int firstSlotId;
  private int lastSlotId;

  // distance of upper left corner to first slot
  private int xd;
  private int yd;

  private GuiPartSlider
      slider =
      new GuiPartSlider(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);

  public GuiSideInventory(GuiMultiModule parent, Container container, int slotCount, int columns) {
    this(parent, container, slotCount, columns, false);
  }

  public GuiSideInventory(GuiMultiModule parent, Container container, int slotCount, int columns, boolean rightSide) {
    super(parent, container, rightSide, false);

    this.columns = columns;
    this.slotCount = slotCount;

    this.xSize = columns * slot.h + borderLeft.w + borderRight.w;
    this.ySize = calcCappedYSize(999999);


    this.xd = -6;
    this.yd = 8;

    updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    // all visible
    if(!slider.isEnabled())
      return true;

    return firstSlotId <= slot.getSlotIndex() && lastSlotId > slot.getSlotIndex();
  }

  @Override
  public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
    return super.isMouseOverSlot(slotIn, mouseX, mouseY) && shouldDrawSlot(slotIn);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // at most as big as the parent
    this.ySize = calcCappedYSize(parentSizeY - borderTop.h - borderBottom.h);

    // update slider or disable it
    if(getDisplayedRows() < getTotalRows()) {
      slider.setEnabled(true);
      slider.setPosition(guiRight() - 6, guiTop + borderTop.h);
      slider.setSize(ySize - borderTop.h - borderBottom.h);

      // update slider values
      slider.setSliderParameters(0, getTotalRows() - getDisplayedRows(), 1);
      updateSlots();
    }
    else {
      slider.setEnabled(false);
      updateSlots();
    }
  }

  private int getDisplayedRows() {
    return slider.height/slot.h;
  }

  private int getTotalRows() {
    int total = slotCount / columns;
    if(slotCount % columns != 0)
      total++;

    return total;
  }

  private int calcCappedYSize(int max) {
    int h = borderTop.h + borderBottom.h + slot.h * getTotalRows();

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

    for(Object o : inventorySlots.inventorySlots) {
      Slot slot = (Slot) o;
      if(shouldDrawSlot(slot)) {
        // calc position of the slot
        int offset = slot.getSlotIndex() - firstSlotId;
        int x = (offset % columns) * GuiSideInventory.slot.w;
        int y = (offset / columns) * GuiSideInventory.slot.h;

        slot.xDisplayPosition = x + xd - GuiSideInventory.slot.w * columns;
        slot.yDisplayPosition = y + yd;

        if(this.right) {
          slot.xDisplayPosition += parent.xSize;
        }
      }
      else {
        slot.xDisplayPosition = 0;
        slot.yDisplayPosition = 0;
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(GUI_INVENTORY);

    int x = guiLeft;
    int y = guiTop;
    int midW = xSize - borderLeft.w - borderRight.w;
    int midH = ySize - borderTop.h - borderBottom.h;

    // upper row
    x += cornerTopLeft.draw(x, y);
    x += borderTop.drawScaledX(x, y, midW);
    cornerTopRight.draw(x, y);

    // center row
    x = guiLeft;
    y += cornerTopLeft.h;
    x += borderLeft.drawScaledY(x, y, midH);
    x += drawSlots(x, y);
    borderRight.drawScaledY(x, y, midH);

    // bottom row
    x = guiLeft;
    y += midH;
    x += cornerBottomLeft.draw(x, y);
    x += borderBottom.drawScaledX(x, y, midW);
    cornerBottomRight.draw(x, y);

    // slider
    if(slider.isEnabled()) {
      slider.update(mouseX, mouseY);
      slider.draw();

      updateSlots();
    }
  }

  protected int drawSlots(int xPos, int yPos) {
    int width = columns * slot.w;
    int height = ySize - borderTop.h - borderBottom.h;

    int fullRows = (lastSlotId - firstSlotId)/columns;
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

  // same as drawTextureModalRect but with our texture size
  public void drawPart(int x, int y, int textureX, int textureY, int width, int height) {
    // our texture size is 64/64 instead of 256/256 so we have to use this function since the normal one is hardcoded to 256/256
    drawModalRectWithCustomSizedTexture(x, y, textureX, textureY, width, height, 64, 64);
  }
}
