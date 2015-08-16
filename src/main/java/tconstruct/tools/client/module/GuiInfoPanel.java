package tconstruct.tools.client.module;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.ListIterator;

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

  private static GuiElementScalable sliderBar = new GuiElementScalable(0, 88, 3, 8);
  private static GuiElement sliderTop = new GuiElement(3, 88, 3, 4);
  private static GuiElement sliderBot = new GuiElement(3, 92, 3, 4);

  private GuiPartBorder border = new GuiPartBorder();

  private FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
  private GuiPartSlider slider = new GuiPartSlider(sliderNormal, sliderHover, sliderHover, sliderTop, sliderBot, sliderBar);

  protected String caption;
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

    caption = "Caption";
    text = new String[]{"Text"};
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    border.setPosition(guiLeft, guiTop);
    border.setSize(xSize, ySize);
    slider.setPosition(guiRight() - slider.width - border.w, guiTop + border.h + 12);
    slider.setSize(this.ySize - border.h * 2 - 2 - 12);
    updateSliderParameters();
  }

  public void setCaption(String caption) {
    this.caption = caption;
    updateSliderParameters();
  }

  public void setText(String... text) {
    // convert \n in localized text to actual newlines
    for(int i = 0; i < text.length; i++) {
      int j = 0;
      while((j = text[i].indexOf("\\n")) >= 0)
      {
        text[i] = text[i].substring(0, j) + '\n' + text[i].substring(j+2);
      }
    }
    this.text = text;
    updateSliderParameters();
  }

  public boolean hasCaption() {
    return caption != null && !caption.isEmpty();
  }


  public int calcNeededHeight() {
    int neededHeight = 0;

    if(hasCaption()) {
      neededHeight += fontRenderer.FONT_HEIGHT;
      neededHeight += 3;
    }

    neededHeight += fontRenderer.FONT_HEIGHT * getTotalLines().size();

    return neededHeight;
  }

  protected void updateSliderParameters() {
    // we assume slider not shown
    slider.hide();

    int h = ySize - border.h*2;

    // check if we can display all lines
    if(calcNeededHeight() <= h)
      // can display all, stay hidden
      return;

    // we need the slider
    slider.show();
    // check how many lines we can show
    int neededHeight = calcNeededHeight(); // recalc because width changed due to slider
    int hiddenRows = (neededHeight - h) / fontRenderer.FONT_HEIGHT;
    if((neededHeight - h) % fontRenderer.FONT_HEIGHT > 0) {
      hiddenRows++;
    }

    slider.setSliderParameters(0, hiddenRows, 1);
  }

  protected List<String> getTotalLines() {
    int w = xSize - border.w*2 - 2;
    if(!slider.isHidden()) {
      w -= 5;
    }

    List<String> lines = Lists.newLinkedList();
    for(String line : text) {
      // empty line
      if(line == null || line.isEmpty()) {
        lines.add("");
        continue;
      }

      lines.addAll(fontRenderer.listFormattedStringToWidth(line, w));
    }

    return lines;
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
                               sliderTop.shift(xd, yd),
                               sliderBot.shift(xd, yd),
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

    int y = 4;
    int x = 5;
    int w = xSize-10;
    int color = 0xfff0f0f0;

    // draw caption
    if(hasCaption()) {
      int x2 = xSize / 2;
      x2 -=fontRenderer.getStringWidth(caption) / 2;
      fontRenderer.drawStringWithShadow(EnumChatFormatting.UNDERLINE + caption, guiLeft + x2, guiTop + y, color);
      y += fontRenderer.FONT_HEIGHT + 3;
    }

    // render shown lines
    ListIterator<String> iter = getTotalLines().listIterator(slider.getValue());
    while(iter.hasNext()) {
      if(y + fontRenderer.FONT_HEIGHT > ySize - border.h) {
        break;
      }

      String line = iter.next();
      fontRenderer.drawStringWithShadow(line, guiLeft + x, guiTop + y, color);
      y += fontRenderer.FONT_HEIGHT;
    }

    this.mc.getTextureManager().bindTexture(BACKGROUND);
    slider.update(mouseX, mouseY);
    slider.draw();
  }
}
