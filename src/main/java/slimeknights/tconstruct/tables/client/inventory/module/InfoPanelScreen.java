package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.SliderWidget;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.Util;

import java.util.List;
import java.util.ListIterator;

public class InfoPanelScreen extends ModuleScreen {

  private static int resW = 118;
  private static int resH = 75;

  private static ResourceLocation BACKGROUND = Util.getResource("textures/gui/panel.png");

  private static ElementScreen topLeft = new ElementScreen(0, 0, 4, 4, 256, 256);
  private static ElementScreen topRight = new ElementScreen(resW + 4, 0, 4, 4);
  private static ElementScreen botLeft = new ElementScreen(0, resH + 4, 4, 4);
  private static ElementScreen botRight = new ElementScreen(resW + 4, resH + 4, 4, 4);

  private static ScalableElementScreen top = new ScalableElementScreen(4, 0, resW, 4);
  private static ScalableElementScreen bot = new ScalableElementScreen(4, 4 + resH, resW, 4);
  private static ScalableElementScreen left = new ScalableElementScreen(0, 4, 4, resH);
  private static ScalableElementScreen right = new ScalableElementScreen(4 + resW, 4, 4, resH);

  private static ScalableElementScreen background = new ScalableElementScreen(4, 4, resW, resH);

  private static ElementScreen sliderNormal = new ElementScreen(0, 83, 3, 5);
  private static ElementScreen sliderHover = sliderNormal.shift(sliderNormal.w, 0);

  private static ScalableElementScreen sliderBar = new ScalableElementScreen(0, 88, 3, 8);
  private static ElementScreen sliderTop = new ElementScreen(3, 88, 3, 4);
  private static ElementScreen sliderBot = new ElementScreen(3, 92, 3, 4);

  private BorderWidget border = new BorderWidget();

  private FontRenderer font = Minecraft.getInstance().fontRenderer;
  private SliderWidget slider = new SliderWidget(sliderNormal, sliderHover, sliderHover, sliderTop, sliderBot, sliderBar);

  protected String caption;
  protected List<String> text;
  protected List<String> tooltips;

  private List<Integer> tooltipLines = Lists.newLinkedList();

  public float textScale = 1.0f;

  public InfoPanelScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, true, false);

    this.border.borderTop = top;
    this.border.borderBottom = bot;
    this.border.borderLeft = left;
    this.border.borderRight = right;

    this.border.cornerTopLeft = topLeft;
    this.border.cornerTopRight = topRight;
    this.border.cornerBottomLeft = botLeft;
    this.border.cornerBottomRight = botRight;

    this.xSize = resW + 8;
    this.ySize = resH + 8;

    this.caption = new TranslationTextComponent("gui.tconstruct.caption").getFormattedText();
    this.text = Lists.newLinkedList();

    super.font = this.font;
  }

  @Override
  public void init(Minecraft mc, int width, int height) {
    super.init(mc, width, height);
    super.font = this.font;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    this.border.setPosition(this.guiLeft, this.guiTop);
    this.border.setSize(this.xSize, this.ySize);
    this.slider.setPosition(this.guiRight() - this.border.w - 2, this.guiTop + this.border.h + 12);
    this.slider.setSize(this.ySize - this.border.h * 2 - 2 - 12);
    this.updateSliderParameters();
  }

  public void setCaption(String caption) {
    this.caption = caption;
    this.updateSliderParameters();
  }

  public void setText(String... text) {
    this.setText(Lists.newArrayList(text), null);
  }

  public void setText(List<String> text) {
    this.setText(text, null);
  }

  public void setText(List<String> text, List<String> tooltips) {
    // convert \n in localized text to actual newlines
    if (text != null) {
      text = Lists.newArrayList(text);

      for (int i = 0; i < text.size(); i++) {
        text.set(i, LocUtils.convertNewlines(text.get(i)));
      }
    }

    this.text = text;
    this.updateSliderParameters();

    this.setTooltips(tooltips);
  }

  protected void setTooltips(List<String> tooltips) {
    // convert \n in localized text to actual newlines
    if (tooltips != null) {
      for (int i = 0; i < tooltips.size(); i++) {
        tooltips.set(i, LocUtils.convertNewlines(tooltips.get(i)));
      }
    }

    this.tooltips = tooltips;
  }

  public boolean hasCaption() {
    return this.caption != null && !this.caption.isEmpty();
  }

  public boolean hasTooltips() {
    return this.tooltips != null && !this.tooltips.isEmpty();
  }

  public int calcNeededHeight() {
    int neededHeight = 0;

    if (hasCaption()) {
      neededHeight += this.font.FONT_HEIGHT;
      neededHeight += 3;
    }

    neededHeight += (this.font.FONT_HEIGHT + 0.5f) * this.getTotalLines().size();

    return neededHeight;
  }

  protected void updateSliderParameters() {
    // we assume slider not shown
    this.slider.hide();

    int h = ySize - 2 * 5; // we use 5 as border thickness

    // check if we can display all lines
    if (this.calcNeededHeight() <= h)
    // can display all, stay hidden
    {
      return;
    }

    // we need the slider
    this.slider.show();
    // check how many lines we can show
    int neededHeight = this.calcNeededHeight(); // recalc because width changed due to slider
    int hiddenRows = (neededHeight - h) / this.font.FONT_HEIGHT;

    if ((neededHeight - h) % this.font.FONT_HEIGHT > 0) {
      hiddenRows++;
    }

    this.slider.setSliderParameters(0, hiddenRows, 1);
  }

  protected List<String> getTotalLines() {
    int w = this.xSize - this.border.w * 2 + 2;

    if (!this.slider.isHidden()) {
      w -= this.slider.width + 3;
    }

    w = (int) ((float) w / this.textScale);

    List<String> lines = Lists.newLinkedList();

    tooltipLines.clear();

    for (String line : text) {
      tooltipLines.add(lines.size());
      // empty line
      if (line == null || line.isEmpty()) {
        lines.add("");
        continue;
      }

      lines.addAll(this.font.listFormattedStringToWidth(line, w));
    }

    return lines;
  }

  public InfoPanelScreen wood() {
    this.shift(resW + 8, 0);
    this.shiftSlider(6, 0);
    return this;
  }

  public InfoPanelScreen metal() {
    this.shift(resW + 8, resH + 8);
    this.shiftSlider(12, 0);
    return this;
  }

  private void shift(int xd, int yd) {
    this.border.borderTop = top.shift(xd, yd);
    this.border.borderBottom = bot.shift(xd, yd);
    this.border.borderLeft = left.shift(xd, yd);
    this.border.borderRight = right.shift(xd, yd);

    this.border.cornerTopLeft = topLeft.shift(xd, yd);
    this.border.cornerTopRight = topRight.shift(xd, yd);
    this.border.cornerBottomLeft = botLeft.shift(xd, yd);
    this.border.cornerBottomRight = botRight.shift(xd, yd);
  }

  private void shiftSlider(int xd, int yd) {
    this.slider = new SliderWidget(sliderNormal.shift(xd, yd), sliderHover.shift(xd, yd), sliderHover.shift(xd, yd), sliderTop.shift(xd, yd), sliderBot.shift(xd, yd), sliderBar.shift(xd, yd));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    if (this.tooltips == null) {
      return;
    }

    if (mouseX < this.guiLeft || mouseX > this.guiRight()) {
      return;
    }

    // floating over tooltip info?
    if (this.hasTooltips() && mouseX >= this.guiRight() - this.border.w - this.font.getCharWidth('?') / 2 && mouseX < this.guiRight() && mouseY > this.guiTop + 5 && mouseY < this.guiTop + 5 + this.font.FONT_HEIGHT) {
      int w = MathHelper.clamp(this.width - mouseX - 12, 10, 200);
      this.renderTooltip(this.font.listFormattedStringToWidth(new TranslationTextComponent("gui.tconstruct.general.hover").getFormattedText(), w), mouseX - guiLeft, mouseY - guiTop);
    }

    // are we hovering over an entry?
    float y = 5 + this.guiTop;

    if (this.hasCaption()) {
      y += this.font.FONT_HEIGHT + 3;
    }

    float textHeight = this.font.FONT_HEIGHT * this.textScale + 0.5f;
    float lowerBound = (this.guiTop + this.ySize - 5) / this.textScale;

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<String> iter = getTotalLines().listIterator(slider.getValue());

    while (iter.hasNext()) {
      if (y + textHeight > lowerBound) {
        break;
      }

      if (mouseY > y && mouseY <= y + textHeight) {
        index = iter.nextIndex();
        break;
      } else {
        iter.next();
      }
      y += textHeight;
    }

    // no line hovered
    if (index < 0) {
      return;
    }

    // get the tooltip index from the hovered line
    int i = 0;
    while (this.tooltipLines.size() > i && index > this.tooltipLines.get(i)) {
      i++;
    }

    if (i >= this.tooltips.size() || this.tooltips.get(i) == null) {
      return;
    }

    int w = MathHelper.clamp(this.width - mouseX - 12, 0, 200);

    if (w < 100) {
      mouseX -= 100 - w;
      w = 100;
    }

    List<String> lines = this.font.listFormattedStringToWidth(this.tooltips.get(i), w);

    this.renderTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop - lines.size() * this.font.FONT_HEIGHT / 2);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(BACKGROUND);

    this.border.draw();
    background.drawScaled(this.guiLeft + 4, this.guiTop + 4, this.xSize - 8, this.ySize - 8);

    float y = 5 + this.guiTop;
    float x = 5 + this.guiLeft;
    int color = 0xfff0f0f0;

    // info ? in the top right corner
    if (this.hasTooltips()) {
      this.font.drawString("?", guiRight() - border.w - this.font.getCharWidth('?') / 2, guiTop + 5, 0xff5f5f5f);
    }

    // draw caption
    if (this.hasCaption()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.getStringWidth(caption) / 2;

      this.font.drawStringWithShadow(TextFormatting.UNDERLINE + TextFormatting.getTextWithoutFormattingCodes(caption), guiLeft + x2, y, color);
      y += this.font.FONT_HEIGHT + 3;
    }


    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = this.font.FONT_HEIGHT * this.textScale + 0.5f;
    float lowerBound = (this.guiTop + this.ySize - 5) / this.textScale;
    RenderSystem.scalef(this.textScale, this.textScale, 1.0f);
    x /= this.textScale;
    y /= this.textScale;

    // render shown lines
    ListIterator<String> iter = getTotalLines().listIterator(this.slider.getValue());
    while (iter.hasNext()) {
      if (y + textHeight - 0.5f > lowerBound) {
        break;
      }

      String line = iter.next();
      this.font.drawStringWithShadow(line, x, y, color);
      y += textHeight;
    }

    RenderSystem.scalef(1f / textScale, 1f / textScale, 1.0f);

    this.minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.slider.update(mouseX, mouseY);
    this.slider.draw();
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    if (mouseButton == 0) {
      this.slider.handleMouseClicked((int) mouseX, (int) mouseY, mouseButton);
      return true;
    }

    return false;
  }

  @Override
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    this.slider.handleMouseReleased();
    return true;
  }

  @Override
  public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollData) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    return this.slider.mouseScrolled(scrollData, !this.isMouseOverFullSlot(mouseX, mouseY) && this.isMouseInModule((int) mouseX, (int) mouseY));
  }
}
