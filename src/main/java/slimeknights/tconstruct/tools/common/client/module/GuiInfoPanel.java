package slimeknights.tconstruct.tools.common.client.module;

import com.google.common.collect.Lists;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.ListIterator;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.client.gui.GuiWidgetSlider;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

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

  private GuiWidgetBorder border = new GuiWidgetBorder();

  private FontRenderer fontRenderer = ClientProxy.fontRenderer;
  private GuiWidgetSlider slider = new GuiWidgetSlider(sliderNormal, sliderHover, sliderHover, sliderTop, sliderBot, sliderBar);

  protected String caption;
  protected List<String> text;
  protected List<String> tooltips;
  private TIntList tooltipLines = new TIntLinkedList();

  public float textScale = 1.0f;


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
    text = Lists.newLinkedList();

    super.fontRenderer = fontRenderer;
  }

  @Override
  public void setWorldAndResolution(Minecraft mc, int width, int height) {
    super.setWorldAndResolution(mc, width, height);
    super.fontRenderer = fontRenderer;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    border.setPosition(guiLeft, guiTop);
    border.setSize(xSize, ySize);
    slider.setPosition(guiRight() - border.w - 2, guiTop + border.h + 12);
    slider.setSize(this.ySize - border.h * 2 - 2 - 12);
    updateSliderParameters();
  }

  public void setCaption(String caption) {
    this.caption = caption;
    updateSliderParameters();
  }

  public void setText(String... text) {
    setText(Lists.newArrayList(text), null);
  }

  public void setText(List<String> text) {
    setText(text, null);
  }

  public void setText(List<String> text, List<String> tooltips) {
    // convert \n in localized text to actual newlines
    if(text != null) {
      text = Lists.newArrayList(text);
      for(int i = 0; i < text.size(); i++) {
        text.set(i, LocUtils.convertNewlines(text.get(i)));
      }
    }
    this.text = text;
    updateSliderParameters();

    setTooltips(tooltips);
  }

  protected void setTooltips(List<String> tooltips) {
    // convert \n in localized text to actual newlines
    if(tooltips != null) {
      for(int i = 0; i < tooltips.size(); i++) {
        tooltips.set(i, LocUtils.convertNewlines(tooltips.get(i)));
      }
    }
    this.tooltips = tooltips;
  }

  public boolean hasCaption() {
    return caption != null && !caption.isEmpty();
  }

  public boolean hasTooltips() {
    return tooltips != null && !tooltips.isEmpty();
  }


  public int calcNeededHeight() {
    int neededHeight = 0;

    if(hasCaption()) {
      neededHeight += fontRenderer.FONT_HEIGHT;
      neededHeight += 3;
    }

    neededHeight += (fontRenderer.FONT_HEIGHT + 0.5f) * getTotalLines().size();

    return neededHeight;
  }

  protected void updateSliderParameters() {
    // we assume slider not shown
    slider.hide();

    int h = ySize - 2 * 5; // we use 5 as border thickness

    // check if we can display all lines
    if(calcNeededHeight() <= h)
    // can display all, stay hidden
    {
      return;
    }

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
    int w = xSize - border.w * 2 + 2;
    if(!slider.isHidden()) {
      w -= slider.width + 3;
    }

    w = (int) ((float) w / textScale);

    List<String> lines = Lists.newLinkedList();
    tooltipLines.clear();
    for(String line : text) {
      tooltipLines.add(lines.size());
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
    slider = new GuiWidgetSlider(sliderNormal.shift(xd, yd),
                                 sliderHover.shift(xd, yd),
                                 sliderHover.shift(xd, yd),
                                 sliderTop.shift(xd, yd),
                                 sliderBot.shift(xd, yd),
                                 sliderBar.shift(xd, yd));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    if(tooltips == null) {
      return;
    }
    if(mouseX < guiLeft || mouseX > guiRight()) {
      return;
    }

    // floating over tooltip info?
    if(hasTooltips()
       && mouseX >= guiRight() - border.w - fontRenderer.getCharWidth('?') / 2 && mouseX < guiRight()
       && mouseY > guiTop + 5 && mouseY < guiTop + 5 + fontRenderer.FONT_HEIGHT) {
      int w = MathHelper.clamp(this.width - mouseX - 12, 10, 200);
      drawHoveringText(fontRenderer.listFormattedStringToWidth(Util.translate("gui.general.hover"), w), mouseX - guiLeft, mouseY - guiTop);
    }

    // are we hovering over an entry?
    float y = 5 + guiTop;

    if(hasCaption()) {
      y += fontRenderer.FONT_HEIGHT + 3;
    }

    float textHeight = fontRenderer.FONT_HEIGHT * textScale + 0.5f;
    float lowerBound = (guiTop + ySize - 5) / textScale;

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<String> iter = getTotalLines().listIterator(slider.getValue());
    while(iter.hasNext()) {
      if(y + textHeight > lowerBound) {
        break;
      }

      if(mouseY > y && mouseY <= y + textHeight) {
        index = iter.nextIndex();
        break;
      }
      else {
        iter.next();
      }
      y += textHeight;
    }

    // no line hovered
    if(index < 0) {
      return;
    }

    // get the tooltip index from the hovered line
    int i = 0;
    while(tooltipLines.size() > i && index > tooltipLines.get(i)) {
      i++;
    }

    if(i >= tooltips.size() || tooltips.get(i) == null) {
      return;
    }

    int w = MathHelper.clamp(this.width - mouseX - 12, 0, 200);
    if(w < 100) {
      mouseX -= 100 - w;
      w = 100;
    }

    List<String> lines = fontRenderer.listFormattedStringToWidth(tooltips.get(i), w);

    drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop - lines.size() * fontRenderer.FONT_HEIGHT / 2);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.mc.getTextureManager().bindTexture(BACKGROUND);

    border.draw();
    background.drawScaled(guiLeft + 4, guiTop + 4, xSize - 8, ySize - 8);

    float y = 5 + guiTop;
    float x = 5 + guiLeft;
    int color = 0xfff0f0f0;


    // info ? in the top right corner
    if(hasTooltips()) {
      fontRenderer.drawString("?", guiRight() - border.w - fontRenderer.getCharWidth('?') / 2, guiTop + 5, 0xff5f5f5f, false);
    }

    // draw caption
    if(hasCaption()) {
      int x2 = xSize / 2;
      x2 -= fontRenderer.getStringWidth(caption) / 2;
      fontRenderer.drawStringWithShadow(TextFormatting.UNDERLINE + TextFormatting
          .getTextWithoutFormattingCodes(caption), guiLeft + x2, y, color);
      y += fontRenderer.FONT_HEIGHT + 3;
    }


    if(text == null || text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = fontRenderer.FONT_HEIGHT * textScale + 0.5f;
    float lowerBound = (guiTop + ySize - 5) / textScale;
    GlStateManager.scale(textScale, textScale, 1.0f);
    x /= textScale;
    y /= textScale;

    // render shown lines
    ListIterator<String> iter = getTotalLines().listIterator(slider.getValue());
    while(iter.hasNext()) {
      if(y + textHeight - 0.5f > lowerBound) {
        break;
      }

      String line = iter.next();
      fontRenderer.drawStringWithShadow(line, x, y, color);
      y += textHeight;
    }

    GlStateManager.scale(1f / textScale, 1f / textScale, 1.0f);

    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    slider.update(mouseX, mouseY, !isMouseOverFullSlot(mouseX, mouseY) && isMouseInModule(mouseX, mouseY));
    slider.draw();
  }
}
