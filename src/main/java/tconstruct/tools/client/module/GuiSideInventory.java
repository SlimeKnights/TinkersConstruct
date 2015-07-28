package tconstruct.tools.client.module;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiElementScalable;
import tconstruct.library.Util;

// a side inventory to be displayed to the left or right of another GUI
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

  //protected final IInventory inventory;

  private int columns;
  private int slotCount;

  private GuiSlider
      slider =
      new GuiSlider(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);

  public GuiSideInventory(Container container, int slotCount, int columns) {
    super(container, false, false);

    this.columns = columns;
    this.slotCount = slotCount;

    this.xSize = columns * slot.h + borderLeft.w + borderRight.w;
    this.ySize = calcCappedYSize(999999);

//    this.inventory = inventory;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // at most as big as the parent
    this.ySize = calcCappedYSize(parentSizeY - borderTop.h - borderBottom.h);

    // update slider (if needed)
    if(ySize < parentSizeY - borderTop.h - borderBottom.h) {
      slider.setEnabled(true);
      //slider.setPosition(guiRight() - borderRight.w, guiTop + borderTop.h);
      slider.setPosition(guiRight() - 6, guiTop + borderTop.h);
      slider.setSize(ySize - borderTop.h - borderBottom.h);
    }
    else {
      slider.setEnabled(false);
    }
  }

  private int calcCappedYSize(int max) {
    int h = borderTop.h + borderBottom.h + slot.h * (slotCount / columns);
    if(slotCount % columns != 0) {
      h += slot.h;
    }
    while(h > max) {
      h -= slot.h;
    }
    return h;
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
    slider.update(mouseX, mouseY);
    slider.draw();
  }

  protected int drawSlots(int xPos, int yPos) {
    int width = columns * slot.w;
    int height = ySize - borderTop.h - borderBottom.h;

    int fullRows = slotCount / columns;
    int y;
    for(y = 0; y < fullRows * slot.h && y < height; y += slot.h) {
      slot.drawScaledX(xPos, yPos + y, width);
    }

    // draw partial row and unused slots
    int slotsLeft = slotCount % columns;
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
