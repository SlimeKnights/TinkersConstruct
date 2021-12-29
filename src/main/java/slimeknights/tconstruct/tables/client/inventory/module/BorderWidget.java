package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.vertex.PoseStack;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.Widget;

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

  protected static final ScalableElementScreen textBackground = new ScalableElementScreen(7 + 18, 7, 18, 10);

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

  public void updateParent(ModuleScreen gui) {
    gui.leftPos -= this.borderLeft.w;
    gui.topPos -= this.borderTop.h;

    gui.imageWidth += this.borderLeft.w + this.borderRight.w;
    gui.imageHeight += this.borderTop.h + this.borderBottom.h;
  }

  @Override
  public void draw(PoseStack matrices) {
    int x = this.xPos;
    int y = this.yPos;
    int midW = this.width - this.borderLeft.w - this.borderRight.w;
    int midH = this.height - this.borderTop.h - this.borderBottom.h;

    // top row
    x += this.cornerTopLeft.draw(matrices, x, y);
    x += this.borderTop.drawScaledX(matrices, x, y, midW);
    this.cornerTopRight.draw(matrices, x, y);

    // center row
    x = this.xPos;
    y += this.borderTop.h;
    x += this.borderLeft.drawScaledY(matrices, x, y, midH);
    x += midW;
    this.borderRight.drawScaledY(matrices, x, y, midH);

    // bottom row
    x = this.xPos;
    y += midH;
    x += this.cornerBottomLeft.draw(matrices, x, y);
    x += this.borderBottom.drawScaledX(matrices, x, y, midW);
    this.cornerBottomRight.draw(matrices, x, y);
  }
}
