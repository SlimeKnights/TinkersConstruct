package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.SliderWidget;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class InfoPanelScreen extends ModuleScreen {

  private static final int resW = 118;
  private static final int resH = 75;

  protected static ResourceLocation BACKGROUND_IMAGE = Util.getResource("textures/gui/panel.png");

  protected static final ElementScreen TOP_LEFT = new ElementScreen(0, 0, 4, 4, 256, 256);
  protected static final ElementScreen TOP_RIGHT = new ElementScreen(resW + 4, 0, 4, 4);
  protected static final ElementScreen BOTTOM_LEFT = new ElementScreen(0, resH + 4, 4, 4);
  protected static final ElementScreen BOTTOM_RIGHT = new ElementScreen(resW + 4, resH + 4, 4, 4);

  protected static final ScalableElementScreen TOP = new ScalableElementScreen(4, 0, resW, 4);
  protected static final ScalableElementScreen BOTTOM = new ScalableElementScreen(4, 4 + resH, resW, 4);
  protected static final ScalableElementScreen LEFT = new ScalableElementScreen(0, 4, 4, resH);
  protected static final ScalableElementScreen RIGHT = new ScalableElementScreen(4 + resW, 4, 4, resH);

  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(4, 4, resW, resH);

  protected static final ElementScreen SLIDER_NORMAL = new ElementScreen(0, 83, 3, 5);
  protected static final ElementScreen SLIDER_HOVER = SLIDER_NORMAL.shift(SLIDER_NORMAL.w, 0);

  protected static final ScalableElementScreen SLIDER_BAR = new ScalableElementScreen(0, 88, 3, 8);
  protected static final ElementScreen SLIDER_TOP = new ElementScreen(3, 88, 3, 4);
  protected static final ElementScreen SLIDER_BOTTOM = new ElementScreen(3, 92, 3, 4);

  protected BorderWidget border = new BorderWidget();

  protected SliderWidget slider = new SliderWidget(SLIDER_NORMAL, SLIDER_HOVER, SLIDER_HOVER, SLIDER_TOP, SLIDER_BOTTOM, SLIDER_BAR);

  protected ITextComponent caption;
  protected List<ITextComponent> text;
  protected List<ITextComponent> tooltips;

  protected List<Integer> tooltipLines = Lists.newLinkedList();

  @Setter
  protected float textScale = 1.0f;
  public InfoPanelScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, true, false);

    this.border.borderTop = TOP;
    this.border.borderBottom = BOTTOM;
    this.border.borderLeft = LEFT;
    this.border.borderRight = RIGHT;

    this.border.cornerTopLeft = TOP_LEFT;
    this.border.cornerTopRight = TOP_RIGHT;
    this.border.cornerBottomLeft = BOTTOM_LEFT;
    this.border.cornerBottomRight = BOTTOM_RIGHT;

    this.xSize = resW + 8;
    this.ySize = resH + 8;

    this.caption = new TranslationTextComponent("gui.tconstruct.caption");
    this.text = Lists.newLinkedList();
  }

  /** Gets the height to render fonts scaled by the text scale */
  public int getScaledFontHeight() {
    return (int)Math.ceil(this.font.FONT_HEIGHT * textScale);
  }

  @Override
  public void init(Minecraft mc, int width, int height) {
    super.init(mc, width, height);
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

  public void setCaption(ITextComponent caption) {
    this.caption = (IFormattableTextComponent) caption;
    this.updateSliderParameters();
  }

  public void setText(ITextComponent... text) {
    List<ITextComponent> textComponents = new ArrayList<>(Arrays.asList(text));

    this.setText(textComponents, null);
  }

  public void setText(List<ITextComponent> text) {
    this.setText(text, null);
  }

  public void setText(List<ITextComponent> text, @Nullable List<ITextComponent> tooltips) {
    this.text = text;
    this.updateSliderParameters();

    this.setTooltips(tooltips);
  }

  protected void setTooltips(@Nullable List<ITextComponent> tooltips) {
    this.tooltips = tooltips;
  }

  public boolean hasCaption() {
    return this.caption != null && !this.caption.getString().isEmpty();
  }

  public boolean hasTooltips() {
    return this.tooltips != null && !this.tooltips.isEmpty();
  }

  public boolean hasInitialized() {
    return this.font != null;
  }

  public int calcNeededHeight() {
    int neededHeight = 0;

    if (!this.hasInitialized()) {
      return height;
    }

    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      neededHeight += scaledFontHeight;
      neededHeight += 3;
    }

    neededHeight += (scaledFontHeight + 0.5f) * this.getTotalLines().size();

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
    int scaledFontHeight = this.getScaledFontHeight();
    int neededHeight = this.calcNeededHeight(); // recalc because width changed due to slider
    int hiddenRows = (neededHeight - h) / scaledFontHeight;

    if ((neededHeight - h) % scaledFontHeight > 0) {
      hiddenRows++;
    }

    this.slider.setSliderParameters(0, hiddenRows, 1);
  }

  protected List<IReorderingProcessor> getTotalLines() {
    int w = this.xSize - this.border.w * 2 + 2;

    if (!this.slider.isHidden()) {
      w -= this.slider.width + 3;
    }

    w = (int) ((float) w / this.textScale);

    List<IReorderingProcessor> lines = Lists.newLinkedList();
    this.tooltipLines.clear();

    for (ITextComponent textComponent : this.text) {
      this.tooltipLines.add(lines.size());

      if (textComponent.getString().isEmpty()) {
        lines.add(StringTextComponent.EMPTY.func_241878_f());
        continue;
      }

      lines.addAll(this.font.trimStringToWidth(textComponent, w));
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
    this.border.borderTop = TOP.shift(xd, yd);
    this.border.borderBottom = BOTTOM.shift(xd, yd);
    this.border.borderLeft = LEFT.shift(xd, yd);
    this.border.borderRight = RIGHT.shift(xd, yd);

    this.border.cornerTopLeft = TOP_LEFT.shift(xd, yd);
    this.border.cornerTopRight = TOP_RIGHT.shift(xd, yd);
    this.border.cornerBottomLeft = BOTTOM_LEFT.shift(xd, yd);
    this.border.cornerBottomRight = BOTTOM_RIGHT.shift(xd, yd);
  }

  private void shiftSlider(int xd, int yd) {
    this.slider = new SliderWidget(SLIDER_NORMAL.shift(xd, yd), SLIDER_HOVER.shift(xd, yd), SLIDER_HOVER.shift(xd, yd), SLIDER_TOP.shift(xd, yd), SLIDER_BOTTOM.shift(xd, yd), SLIDER_BAR.shift(xd, yd));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
   // no-op
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

    if (this.tooltips == null) {
      return;
    }

    if (mouseX < this.guiLeft || mouseX > this.guiRight()) {
      return;
    }

    // floating over tooltip info?
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasTooltips() && mouseX >= this.guiRight() - this.border.w - this.font.getStringWidth("?") / 2 && mouseX < this.guiRight()
        && mouseY > this.guiTop + 5 && mouseY < this.guiTop + 5 + scaledFontHeight) {
      this.renderTooltip(matrices, this.font.trimStringToWidth(new TranslationTextComponent("gui.tconstruct.general.hover"), 150), mouseX - 155, mouseY);
    }

    // are we hovering over an entry?
    float y = getTooltipStart(5 + this.guiTop);
    float textHeight = (font.FONT_HEIGHT + 0.5f) * this.textScale;
    float lowerBound = (this.guiTop + this.ySize - 5);

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<IReorderingProcessor> iter = this.getTotalLines().listIterator(slider.getValue());

    while (iter.hasNext()) {
      if (y + textHeight > lowerBound) {
        break;
      }

      if (mouseY > y && mouseY <= y + textHeight) {
        index = iter.nextIndex();
        break;
      }
      else {
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

    if (i >= this.tooltips.size() || this.tooltips.get(i).getString().isEmpty()) {
      return;
    }

    int w = MathHelper.clamp(this.width - mouseX - 12, 0, 200);

    if (w < 100) {
      mouseX -= 100 - w;
      w = 100;
    }

    List<IReorderingProcessor> lines = this.font.trimStringToWidth(this.tooltips.get(i), w);

    this.renderTooltip(matrices, lines, mouseX, (mouseY - lines.size() * this.getScaledFontHeight() / 2));
  }

  /**
   * Gets the location of the first tooltip for info tooltips
   */
  protected float getTooltipStart(float y) {
    if (this.hasCaption()) {
      y += this.getScaledFontHeight() + 3;
    }
    return y;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(BACKGROUND_IMAGE);

    this.border.draw(matrices);
    BACKGROUND.drawScaled(matrices, this.guiLeft + 4, this.guiTop + 4, this.xSize - 8, this.ySize - 8);

    float y = 5 + this.guiTop;
    float x = 5 + this.guiLeft;
    int color = 0xfff0f0f0;

    // info ? in the top right corner
    if (this.hasTooltips()) {
      this.font.drawString(matrices, "?", guiRight() - this.border.w - this.font.getStringWidth("?") / 2f, this.guiTop + 5, 0xff5f5f5f);
    }

    // draw caption
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.getStringPropertyWidth(this.caption) / 2;

      this.font.func_238407_a_(matrices, this.caption.copyRaw().mergeStyle(TextFormatting.UNDERLINE).func_241878_f(), (float) this.guiLeft + x2, y, color);
      y += scaledFontHeight + 3;
    }

    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = font.FONT_HEIGHT + 0.5f;
    float lowerBound = (this.guiTop + this.ySize - 5) / this.textScale;
    RenderSystem.scalef(this.textScale, this.textScale, 1.0f);
    x /= this.textScale;
    y /= this.textScale;

    // render shown lines
    ListIterator<IReorderingProcessor> iter = this.getTotalLines().listIterator(this.slider.getValue());
    while (iter.hasNext()) {
      if (y + textHeight - 0.5f > lowerBound) {
        break;
      }

      IReorderingProcessor line = iter.next();
      this.font.func_238407_a_(matrices, line, x, y, color);
      y += textHeight;
    }

    RenderSystem.scalef(1f / textScale, 1f / textScale, 1.0f);

    this.minecraft.getTextureManager().bindTexture(BACKGROUND_IMAGE);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.slider.update(mouseX, mouseY);
    this.slider.draw(matrices);
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    if (mouseButton == 0) {
      if (mouseX >= this.slider.xPos && mouseY >= this.slider.yPos && mouseX <= this.slider.xPos + this.slider.width && mouseY <= this.slider.yPos + this.slider.height) {
        this.slider.handleMouseClicked((int) mouseX, (int) mouseY, mouseButton);
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    this.slider.handleMouseReleased();
    return mouseX >= this.slider.xPos && mouseY >= this.slider.yPos && mouseX <= this.slider.xPos + this.slider.width && mouseY <= this.slider.yPos + this.slider.height;
  }

  @Override
  public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollData) {
    if (!this.slider.isEnabled() || !this.isMouseInModule((int) mouseX, (int) mouseY) || this.isMouseOverFullSlot(mouseX, mouseY)) {
      return false;
    }

    return this.slider.mouseScrolled(scrollData, true);
  }
}
