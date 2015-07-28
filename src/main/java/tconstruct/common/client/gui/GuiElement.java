package tconstruct.common.client.gui;

import net.minecraft.client.gui.GuiScreen;

/**
 * Represents a GUI element INSIDE the graphics.
 * The coordinates all refer to the coordinates inside the graphics!
 */
public class GuiElement {
  // this is totally completely ugly but it's a simple solution that doesn't clutter everything too much >_>
  public static int defaultTexW = 256;
  public static int defaultTexH = 256;

  public final int x;
  public final int y;
  public final int w;
  public final int h;

  public int texW;
  public int texH;

  public GuiElement(int x, int y, int w, int h, int texW, int texH) {
    this(x,y,w,h);
    setTextureSize(texW, texH);

    defaultTexW = texW;
    defaultTexH = texH;
  }

  public GuiElement(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    setTextureSize(defaultTexW, defaultTexH);
  }

  public GuiElement setTextureSize(int w, int h) {
    texW = w;
    texH = h;

    return this;
  }

  /**
   * Draws the element at the given x/y coordinates
   * @param xPos  X-Coordinate on the screen
   * @param yPos  Y-Coordinate on the screen
   */
  public int draw(int xPos, int yPos) {
    GuiScreen.drawModalRectWithCustomSizedTexture(xPos, yPos, x,y, w,h, texW, texH);
    return w;
  }

  public static class Builder {
    public int w;
    public int h;

    public Builder(int w, int h) {
      this.w = w;
      this.h = h;
    }

    public GuiElement get(int x, int y, int w, int h) {
      return new GuiElement(x,y,w,h, this.w, this.h);
    }
  }
}
