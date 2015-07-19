package tconstruct.common.client.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiElementScalable extends GuiElement {

  public GuiElementScalable(int x, int y, int w, int h) {
    super(x, y, w, h);
  }

  public int drawScaledX(int xPos, int yPos, int width) {
    for(int i = 0; i < width / w; i++) {
      draw(xPos + i * w, yPos);
    }
    // remainder that doesn't fit total width
    int remainder = width % w;
    if(remainder > 0) {
      GuiScreen.drawModalRectWithCustomSizedTexture(xPos + width - remainder, yPos, x, y, remainder, h, texW, texH);
    }

    return width;
  }

  public int drawScaledY(int xPos, int yPos, int height) {
    for(int i = 0; i < height / h; i++) {
      draw(xPos, yPos + i * h);
    }
    // remainder that doesn't fit total width
    int remainder = height % h;
    if(remainder > 0) {
      GuiScreen.drawModalRectWithCustomSizedTexture(xPos, yPos + height - remainder, x, y, w, remainder, texW, texH);
    }

    return w;
  }
}
