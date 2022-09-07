package slimeknights.tconstruct.tables.client.inventory.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.SliderWidget;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.RenderUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class InfoPanelWidget implements Widget, GuiEventListener, NarratableEntry {

  private static final int BORDER_SIZE = 4;

  private static final int INNER_WIDTH = 118;
  private static final int INNER_HEIGHT = 75;

  public static final int DEFAULT_WIDTH = INNER_WIDTH + 2 * BORDER_SIZE, DEFAULT_HEIGHT = INNER_HEIGHT + 2 * BORDER_SIZE;

  /** Default caption displayed until one is set */
  private static final Component DEFAULT_CAPTION = TConstruct.makeTranslation("gui", "caption").withStyle(ChatFormatting.UNDERLINE);

  protected static ResourceLocation BACKGROUND_IMAGE = TConstruct.getResource("textures/gui/panel.png");

  protected static final ElementScreen TOP_LEFT = new ElementScreen(0, 0, BORDER_SIZE, BORDER_SIZE);
  protected static final ElementScreen TOP_RIGHT = new ElementScreen(INNER_WIDTH + BORDER_SIZE, 0, BORDER_SIZE, BORDER_SIZE);
  protected static final ElementScreen BOTTOM_LEFT = new ElementScreen(0, INNER_HEIGHT + BORDER_SIZE, BORDER_SIZE, BORDER_SIZE);
  protected static final ElementScreen BOTTOM_RIGHT = new ElementScreen(INNER_WIDTH + BORDER_SIZE, INNER_HEIGHT + BORDER_SIZE, BORDER_SIZE, BORDER_SIZE);

  protected static final ScalableElementScreen TOP = new ScalableElementScreen(BORDER_SIZE, 0, INNER_WIDTH, BORDER_SIZE);
  protected static final ScalableElementScreen BOTTOM = new ScalableElementScreen(BORDER_SIZE, BORDER_SIZE + INNER_HEIGHT, INNER_WIDTH, BORDER_SIZE);
  protected static final ScalableElementScreen LEFT = new ScalableElementScreen(0, BORDER_SIZE, BORDER_SIZE, INNER_HEIGHT);
  protected static final ScalableElementScreen RIGHT = new ScalableElementScreen(BORDER_SIZE + INNER_WIDTH, BORDER_SIZE, BORDER_SIZE, INNER_HEIGHT);

  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(BORDER_SIZE, BORDER_SIZE, INNER_WIDTH, INNER_HEIGHT);

  protected static final ElementScreen SLIDER_NORMAL = new ElementScreen(0, 83, 3, 5);
  protected static final ElementScreen SLIDER_HOVER = SLIDER_NORMAL.shift(SLIDER_NORMAL.w, 0);

  protected static final ScalableElementScreen SLIDER_BAR = new ScalableElementScreen(0, 88, 3, 8);
  protected static final ElementScreen SLIDER_TOP = new ElementScreen(3, 88, 3, 4);
  protected static final ElementScreen SLIDER_BOTTOM = new ElementScreen(3, 92, 3, 4);

  protected final Screen parent;
  protected final Font font;

  public final int leftPos, topPos;
  protected final int imageWidth, imageHeight;

  protected final BorderWidget border;

  protected final SliderWidget slider;

  protected Component caption;
  protected List<Component> text;
  protected List<Component> tooltips;

  protected List<Integer> tooltipLines = Lists.newLinkedList();

  protected final float textScale;

  public InfoPanelWidget(Screen parent, Style style, int leftPos, int topPos, float textScale) {
    this(parent, style, leftPos, topPos, DEFAULT_WIDTH, DEFAULT_HEIGHT, textScale);
  }

  public InfoPanelWidget(Screen parent, Style style, int leftPos, int topPos, int width, int height, float textScale) {

    this.parent = parent;
    this.font = parent.getMinecraft().font;

    this.border = style.createBorderWidget();
    this.slider = style.createSliderWidget();

    this.imageWidth = width;
    this.imageHeight = height;

    this.caption = DEFAULT_CAPTION;
    this.text = Lists.newLinkedList();

    this.textScale = textScale;

    this.leftPos = leftPos;
    this.topPos = topPos;

    this.border.setPosition(this.leftPos, this.topPos);
    this.border.setSize(this.imageWidth, this.imageHeight);
    this.slider.setPosition(this.guiRight() - this.border.w - 2, this.topPos + this.border.h + 12);
    this.slider.setSize(this.imageHeight - this.border.h * 2 - 2 - 12);
    this.updateSliderParameters();
  }

  public int guiRight() {
    return this.leftPos + this.imageWidth;
  }

  public int guiBottom() {
    return this.topPos + this.imageHeight;
  }

  public Rect2i getArea() {
    return new Rect2i(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
  }

  /** Gets the height to render fonts scaled by the text scale */
  public int getScaledFontHeight() {
    return (int)Math.ceil(this.font.lineHeight * textScale);
  }

  public int getSliderValue() {
    return this.slider.getValue();
  }

  public void setSliderValue(int value) {
    this.slider.setSliderValue(value);
  }

  public Component getCaption() {
    return caption;
  }

  public void setCaption(Component caption) {
    this.caption = caption.copy().withStyle(ChatFormatting.UNDERLINE);
    this.updateSliderParameters();
  }

  public List<Component> getText() {
    return this.text;
  }

  public void setText(Component... text) {
    List<Component> textComponents = new ArrayList<>(Arrays.asList(text));

    this.setText(textComponents, null);
  }

  public void setText(List<Component> text) {
    this.setText(text, null);
  }

  public void setText(List<Component> text, @Nullable List<Component> tooltips) {
    this.text = text;
    this.updateSliderParameters();

    this.setTooltips(tooltips);
  }

  @Nullable
  public List<Component> getTooltips() {
    return this.tooltips;
  }

  protected void setTooltips(@Nullable List<Component> tooltips) {
    this.tooltips = tooltips;
  }

  public boolean hasCaption() {
    return this.caption != null && !this.caption.getString().isEmpty();
  }

  public boolean hasTooltips() {
    return this.tooltips != null && !this.tooltips.isEmpty();
  }

  public int calcNeededHeight() {
    int neededHeight = 0;

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

    int h = imageHeight - 2 * 5; // we use 5 as border thickness

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

  protected List<FormattedCharSequence> getTotalLines() {
    int w = this.imageWidth - this.border.w * 2 + 2;

    if (!this.slider.isHidden()) {
      w -= this.slider.width + 3;
    }

    w = (int) ((float) w / this.textScale);

    List<FormattedCharSequence> lines = Lists.newLinkedList();
    this.tooltipLines.clear();

    for (Component textComponent : this.text) {
      this.tooltipLines.add(lines.size());

      if (textComponent.getString().isEmpty()) {
        lines.add(TextComponent.EMPTY.getVisualOrderText());
        continue;
      }

      lines.addAll(this.font.split(textComponent, w));
    }

    return lines;
  }

  public void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {

    if (this.tooltips == null) {
      return;
    }

    if (mouseX < this.leftPos || mouseX > this.guiRight()) {
      return;
    }

    // floating over tooltip info?
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasTooltips() && mouseX >= this.guiRight() - this.border.w - this.font.width("?") / 2 && mouseX < this.guiRight()
        && mouseY > this.topPos + 5 && mouseY < this.topPos + 5 + scaledFontHeight) {
      parent.renderTooltip(matrices, this.font.split(new TranslatableComponent("gui.tconstruct.general.hover"), 150), mouseX - 155, mouseY);
    }

    // are we hovering over an entry?
    float y = getTooltipStart(5 + this.topPos);
    float textHeight = (font.lineHeight + 0.5f) * this.textScale;
    float lowerBound = (this.topPos + this.imageHeight - 5);

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<FormattedCharSequence> iter = this.getTotalLines().listIterator(slider.getValue());

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

    int w = Mth.clamp(parent.width - mouseX - 12, 0, 200);

    if (w < 100) {
      mouseX -= 100 - w;
      w = 100;
    }

    List<FormattedCharSequence> lines = this.font.split(this.tooltips.get(i), w);

    parent.renderTooltip(matrices, lines, mouseX, (mouseY - lines.size() * this.getScaledFontHeight() / 2));
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
  public void render(PoseStack matrices, int mouseX, int mouseY, float partialTick) {
    RenderUtils.setup(BACKGROUND_IMAGE);

    this.border.draw(matrices);
    BACKGROUND.drawScaled(matrices, this.leftPos + BORDER_SIZE, this.topPos + BORDER_SIZE, this.imageWidth - 2 * BORDER_SIZE, this.imageHeight - 2 * BORDER_SIZE);

    float y = 5 + this.topPos;
    float x = 5 + this.leftPos;
    int color = 0xfff0f0f0;

    // info ? in the top right corner
    if (this.hasTooltips()) {
      this.font.draw(matrices, "?", guiRight() - this.border.w - this.font.width("?") / 2f, this.topPos + BORDER_SIZE + 1, 0xff5f5f5f);
    }

    // draw caption
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.caption) / 2;

      this.font.drawShadow(matrices, this.caption.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += scaledFontHeight + 3;
    }

    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = font.lineHeight + 0.5f;
    float lowerBound = (this.topPos + this.imageHeight - BORDER_SIZE - 1) / this.textScale;
    matrices.pushPose();
    matrices.scale(this.textScale, this.textScale, 1.0f);
    x /= this.textScale;
    y /= this.textScale;

    // render shown lines
    ListIterator<FormattedCharSequence> iter = this.getTotalLines().listIterator(this.slider.getValue());
    while (iter.hasNext()) {
      if (y + textHeight - 0.5f > lowerBound) {
        break;
      }

      FormattedCharSequence line = iter.next();
      this.font.drawShadow(matrices, line, x, y, color);
      y += textHeight;
    }

    matrices.popPose();

    RenderUtils.setup(BACKGROUND_IMAGE);
    this.slider.update(mouseX, mouseY);
    this.slider.draw(matrices);
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return this.leftPos - 1 <= mouseX && mouseX < this.guiRight() + 1 && this.topPos - 1 <= mouseY && mouseY < this.guiBottom() + 1;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
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

  /**
   * Cannot be replaced with {@code GuiEventListener.mouseReleased()} at this moment, and must be called directly by the screen that uses this panel.
   * The reason is that {@code GuiEventListener.mouseReleased()} only gets called when the mouse is within the area of this widget,
   * while this widget must call {@code this.slider.handleMouseReleased();} when the mouse button is released regardless of the mouse position for the slider to work correctly.
   */
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    this.slider.handleMouseReleased();
    return mouseX >= this.slider.xPos && mouseY >= this.slider.yPos && mouseX <= this.slider.xPos + this.slider.width && mouseY <= this.slider.yPos + this.slider.height;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollData) {
    if (!this.slider.isEnabled()) {
      return false;
    }

    return this.slider.mouseScrolled(scrollData, true);
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
  }

  /**
   * Defines texture coordinates for different styles for the info panel.
   */
  public enum Style {
    PLAIN(0, 0, 0),
    WOOD(INNER_WIDTH + 2 * BORDER_SIZE, 0, 6),
    METAL(INNER_WIDTH + 2 * BORDER_SIZE, INNER_HEIGHT + 2 * BORDER_SIZE, 12);

    private final int startU, startV, sliderU;

    Style(int startU, int startV, int sliderU) {
      this.startU = startU;
      this.startV = startV;
      this.sliderU = sliderU;
    }

    private BorderWidget createBorderWidget() {
      BorderWidget border = new BorderWidget();
      border.borderTop = TOP.shift(startU, startV);
      border.borderBottom = BOTTOM.shift(startU, startV);
      border.borderLeft = LEFT.shift(startU, startV);
      border.borderRight = RIGHT.shift(startU, startV);

      border.cornerTopLeft = TOP_LEFT.shift(startU, startV);
      border.cornerTopRight = TOP_RIGHT.shift(startU, startV);
      border.cornerBottomLeft = BOTTOM_LEFT.shift(startU, startV);
      border.cornerBottomRight = BOTTOM_RIGHT.shift(startU, startV);

      return border;
    }

    private SliderWidget createSliderWidget() {
      return new SliderWidget(SLIDER_NORMAL.shift(sliderU, 0), SLIDER_HOVER.shift(sliderU, 0), SLIDER_HOVER.shift(sliderU, 0),
        SLIDER_TOP.shift(sliderU, 0), SLIDER_BOTTOM.shift(sliderU, 0), SLIDER_BAR.shift(sliderU, 0));
    }
  }
}
