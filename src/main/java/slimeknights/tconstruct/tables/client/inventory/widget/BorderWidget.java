package slimeknights.tconstruct.tables.client.inventory.widget;

import net.minecraft.client.gui.GuiGraphics;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.Widget;
import slimeknights.tconstruct.tables.client.inventory.module.GenericScreen;

public class BorderWidget extends Widget {
  // all elements based on generic screen
  public ElementScreen cornerTopLeft = GenericScreen.cornerTopLeft;
  public ElementScreen cornerTopRight = GenericScreen.cornerTopRight;
  public ElementScreen cornerBottomLeft = GenericScreen.cornerBottomLeft;
  public ElementScreen cornerBottomRight = GenericScreen.cornerBottomRight;

  public ScalableElementScreen borderTop = GenericScreen.borderTop;
  public ScalableElementScreen borderBottom = GenericScreen.borderBottom;
  public ScalableElementScreen borderLeft = GenericScreen.borderLeft;
  public ScalableElementScreen borderRight = GenericScreen.borderRight;

  // TODO: this is not used, was it supposed to be used?
  //protected static final ScalableElementScreen textBackground = new ScalableElementScreen(7 + 18, 7, 18, 10);

  public int w = borderLeft.w;
  public int h = borderTop.h;

  /** Sets the size so that the given point is the upper left corner of the inside */
  public void setPosInner(int x, int y) {
    this.setPosition(x - this.cornerTopLeft.w, y - this.cornerTopLeft.h);
  }

  /** Sets the size so that it surrounds the given area */
  public void sedSizeInner(int width, int height) {
    this.setSize(width + this.borderLeft.w + this.borderRight.w, height + this.borderTop.h + this.borderBottom.h);
  }

  public int getWidthWithBorder(int width) {
    return width + this.borderRight.w + this.borderLeft.w;
  }

  public int getHeightWithBorder(int height) {
    return height + this.borderTop.h + this.borderBottom.h;
  }

  public void updateParent(ModuleScreen<?,?> gui) {
    gui.leftPos -= this.borderLeft.w;
    gui.topPos -= this.borderTop.h;

    gui.imageWidth += this.borderLeft.w + this.borderRight.w;
    gui.imageHeight += this.borderTop.h + this.borderBottom.h;
  }

  @Override
  public void draw(GuiGraphics graphics) {
    int x = this.xPos;
    int y = this.yPos;
    int midW = this.width - this.borderLeft.w - this.borderRight.w;
    int midH = this.height - this.borderTop.h - this.borderBottom.h;

    // top row
    x += drawX(this.cornerTopLeft, graphics, x, y);
    x += this.borderTop.drawScaledX(graphics, x, y, midW);
    this.cornerTopRight.draw(graphics, x, y);

    // center row
    x = this.xPos;
    y += this.borderTop.h;
    x += this.borderLeft.drawScaledY(graphics, x, y, midH);
    x += midW;
    this.borderRight.drawScaledY(graphics, x, y, midH);

    // bottom row
    x = this.xPos;
    y += midH;
    x += drawX(this.cornerBottomLeft, graphics, x, y);
    x += this.borderBottom.drawScaledX(graphics, x, y, midW);
    this.cornerBottomRight.draw(graphics, x, y);
  }

  /** Draws an element ans returns its width */
  private int drawX(ElementScreen element, GuiGraphics graphics, int x, int y) {
    element.draw(graphics, x, y);
    return element.w;
  }
}
