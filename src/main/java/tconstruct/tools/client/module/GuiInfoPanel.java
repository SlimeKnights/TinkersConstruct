package tconstruct.tools.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiElementScalable;
import tconstruct.common.client.gui.GuiModule;
import tconstruct.common.client.gui.GuiMultiModule;
import tconstruct.common.client.gui.GuiPartSlider;
import tconstruct.library.Util;

public class GuiInfoPanel extends GuiModule {
  private static int resW = 118;
  private static int resH = 75;

  private static ResourceLocation BACKGROUND = Util.getResource("textures/gui/panel.png");

  private static GuiElement topLeft  = new GuiElement(0,0, 4, 4, 256,256);
  private static GuiElement topRight = new GuiElement(resW+4,0, 4, 4);
  private static GuiElement botLeft  = new GuiElement(0, resH+4, 4, 4);
  private static GuiElement botRight = new GuiElement(resW+4, resH+4, 4, 4);

  private static GuiElementScalable top   = new GuiElementScalable(4, 0, resW, 4);
  private static GuiElementScalable bot   = new GuiElementScalable(4, 4 + resH, resW, 4);
  private static GuiElementScalable left  = new GuiElementScalable(0, 4, 4, resH);
  private static GuiElementScalable right = new GuiElementScalable(4 + resW, 4, 4, resH);

  private static GuiElementScalable background = new GuiElementScalable(4,4, resW, resH);

  private static GuiElement sliderNormal = new GuiElement(0, 83, 3, 5);
  private static GuiElement sliderHover = sliderNormal.shift(sliderNormal.w, 0);

  private static GuiElementScalable sliderBar = new GuiElementScalable(0, 88, 3, 4);

  private GuiPartBorder border = new GuiPartBorder();

  private FontRenderer fontRenderer;
  private GuiPartSlider slider = new GuiPartSlider(sliderNormal, sliderHover, sliderHover, sliderBar, sliderBar, sliderBar);

  protected String[] text;

  public GuiInfoPanel(GuiMultiModule parent, Container container) {
    super(parent, container, true, false);

    border.borderTop = top;
    border.borderBottom = bot;
    border.borderLeft = left;
    border.borderRight = right;

    border.cornerTopLeft = topLeft;
    border.cornerTopRight = topRight;
    border.cornerBottomLeft = botLeft;
    border.cornerBottomRight = botRight;

    this.xSize = resW + 8;
    this.ySize = resH + 8;

    text = new String[]{"Caption"};
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    border.setPosition(guiLeft, guiTop);
    border.setSize(xSize, ySize);
  }

  public void setText(String[] text) {
    this.text = text;
  }

  public GuiInfoPanel wood() {
    shift(resW + 8, 0);
    shiftSlider(6, 0);
    return this;
  }

  public GuiInfoPanel metal() {
    shift(resW + 8, resH + 8);
    shiftSlider(12, 0);
    return this;
  }

  private void shift(int xd, int yd) {
    border.borderTop = top.shift(xd, yd);
    border.borderBottom = bot.shift(xd, yd);
    border.borderLeft = left.shift(xd, yd);
    border.borderRight = right.shift(xd, yd);

    border.cornerTopLeft = topLeft.shift(xd, yd);
    border.cornerTopRight = topRight.shift(xd, yd);
    border.cornerBottomLeft = botLeft.shift(xd, yd);
    border.cornerBottomRight = botRight.shift(xd, yd);
  }

  private void shiftSlider(int xd, int yd) {
    slider = new GuiPartSlider(sliderNormal.shift(xd, yd),
                               sliderHover.shift(xd, yd),
                               sliderHover.shift(xd, yd),
                               sliderBar.shift(xd, yd),
                               sliderBar.shift(xd, yd),
                               sliderBar.shift(xd, yd));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.mc.getTextureManager().bindTexture(BACKGROUND);

    border.draw();
    background.drawScaled(guiLeft + 4, guiTop + 4, xSize - 8, ySize - 8);

    if(text == null || text.length == 0) {
      // no text to draw
      return;
    }

    if(fontRenderer == null) {
      fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    }

    int y = 4;
    int x = 5;
    int color = 0xfff0f0f0;

    // draw caption
    String caption = text[0];
    x = xSize/2;
    x -= fontRenderer.getStringWidth(caption)/2;
    fontRenderer.drawStringWithShadow(EnumChatFormatting.UNDERLINE + caption, guiLeft + x, guiTop + y, color);
    x = 5;
    y += 12;



    // draw remainder (while possible)
    for(int i = 1; i < text.length; i++) {
      // null equals newline
      if(text[i] == null) {
        y += fontRenderer.FONT_HEIGHT;
        continue;
      }

      // same as drawSplitString except that we know the rendered height
      List<String> parts = fontRenderer.listFormattedStringToWidth(text[i], xSize-10);

      y += 2;

      for(String str : parts) {
        if(y + fontRenderer.FONT_HEIGHT > ySize - 4)
          break;
        fontRenderer.drawStringWithShadow(str, guiLeft + x, guiTop + y, color);
        y += fontRenderer.FONT_HEIGHT;
      }
    }
  }
}
