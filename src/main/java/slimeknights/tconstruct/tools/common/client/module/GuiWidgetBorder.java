package slimeknights.tconstruct.tools.common.client.module;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiWidget;

public class GuiWidgetBorder extends GuiWidget {

  // all elements based on generic gui
  public GuiElement cornerTopLeft = GuiGeneric.cornerTopLeft;
  public GuiElement cornerTopRight = GuiGeneric.cornerTopRight;
  public GuiElement cornerBottomLeft = GuiGeneric.cornerBottomLeft;
  public GuiElement cornerBottomRight = GuiGeneric.cornerBottomRight;

  public GuiElementScalable borderTop = GuiGeneric.borderTop;
  public GuiElementScalable borderBottom = GuiGeneric.borderBottom;
  public GuiElementScalable borderLeft = GuiGeneric.borderLeft;
  public GuiElementScalable borderRight = GuiGeneric.borderRight;

  protected static final GuiElementScalable textBackground = new GuiElementScalable(7 + 18, 7, 18, 10);

  public int w = borderLeft.w;
  public int h = borderTop.h;

  /** Sets the size so that the given point is the upper left corner of the inside */
  public void setPosInner(int x, int y) {
    setPosition(x - cornerTopLeft.w, y - cornerTopLeft.h);
  }

  /** Sets the size so that it surrounds the given area */
  public void sedSizeInner(int width, int height) {
    setSize(width + borderLeft.w + borderRight.w, height + borderTop.h + borderBottom.h);
  }

  public int getWidthWithBorder(int width) {
    return width + borderRight.w + borderLeft.w;
  }

  public int getHeightWithBorder(int height) {
    return height + borderTop.h + borderBottom.h;
  }

  public void updateParent(GuiModule gui) {
    gui.guiLeft -= borderLeft.w;
    gui.guiTop -= borderTop.h;

    gui.xSize += borderLeft.w + borderRight.w;
    gui.ySize += borderTop.h + borderBottom.h;
  }

  @Override
  public void draw() {
    int x = xPos;
    int y = yPos;
    int midW = width - borderLeft.w - borderRight.w;
    int midH = height - borderTop.h - borderBottom.h;

    // top row
    x += cornerTopLeft.draw(x, y);
    x += borderTop.drawScaledX(x, y, midW);
    cornerTopRight.draw(x, y);

    // center row
    x = xPos;
    y += borderTop.h;
    x += borderLeft.drawScaledY(x, y, midH);
    x += midW;
    borderRight.drawScaledY(x, y, midH);

    // bottom row
    x = xPos;
    y += midH;
    x += cornerBottomLeft.draw(x, y);
    x += borderBottom.drawScaledX(x, y, midW);
    cornerBottomRight.draw(x, y);
  }
}
