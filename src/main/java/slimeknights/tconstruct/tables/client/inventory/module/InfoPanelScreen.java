package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.SliderWidget;
import slimeknights.tconstruct.library.Util;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class InfoPanelScreen extends ModuleScreen {

  private static final int resW = 118;
  private static final int resH = 75;

  protected static Identifier BACKGROUND_IMAGE = Util.getResource("textures/gui/panel.png");

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

  protected Text caption;
  protected List<Text> text;
  protected List<Text> tooltips;

  protected List<Integer> tooltipLines = Lists.newLinkedList();

  @Setter
  protected float textScale = 1.0f;
  public InfoPanelScreen(MultiModuleScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title) {
    super(parent, container, playerInventory, title, true, false);

    this.border.borderTop = TOP;
    this.border.borderBottom = BOTTOM;
    this.border.borderLeft = LEFT;
    this.border.borderRight = RIGHT;

    this.border.cornerTopLeft = TOP_LEFT;
    this.border.cornerTopRight = TOP_RIGHT;
    this.border.cornerBottomLeft = BOTTOM_LEFT;
    this.border.cornerBottomRight = BOTTOM_RIGHT;

    this.backgroundWidth = resW + 8;
    this.backgroundHeight = resH + 8;

    this.caption = new TranslatableText("gui.tconstruct.caption");
    this.text = Lists.newLinkedList();
  }

  /** Gets the height to render fonts scaled by the text scale */
  public int getScaledFontHeight() {
    return (int)Math.ceil(this.textRenderer.fontHeight * textScale);
  }

  @Override
  public void init(MinecraftClient mc, int width, int height) {
    super.init(mc, width, height);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    this.border.setPosition(this.x, this.y);
    this.border.setSize(this.backgroundWidth, this.backgroundHeight);
    this.slider.setPosition(this.guiRight() - this.border.w - 2, this.y + this.border.h + 12);
    this.slider.setSize(this.backgroundHeight - this.border.h * 2 - 2 - 12);
    this.updateSliderParameters();
  }

  public void setCaption(Text caption) {
    this.caption = (MutableText) caption;
    this.updateSliderParameters();
  }

  public void setText(Text... text) {
    List<Text> textComponents = new ArrayList<>(Arrays.asList(text));

    this.setText(textComponents, null);
  }

  public void setText(List<Text> text) {
    this.setText(text, null);
  }

  public void setText(List<Text> text, @Nullable List<Text> tooltips) {
    this.text = text;
    this.updateSliderParameters();

    this.setTooltips(tooltips);
  }

  protected void setTooltips(@Nullable List<Text> tooltips) {
    this.tooltips = tooltips;
  }

  public boolean hasCaption() {
    return this.caption != null && !this.caption.getString().isEmpty();
  }

  public boolean hasTooltips() {
    return this.tooltips != null && !this.tooltips.isEmpty();
  }

  public boolean hasInitialized() {
    return this.textRenderer != null;
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

    int h = backgroundHeight - 2 * 5; // we use 5 as border thickness

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

  protected List<OrderedText> getTotalLines() {
    int w = this.backgroundWidth - this.border.w * 2 + 2;

    if (!this.slider.isHidden()) {
      w -= this.slider.width + 3;
    }

    w = (int) ((float) w / this.textScale);

    List<OrderedText> lines = Lists.newLinkedList();
    this.tooltipLines.clear();

    for (Text textComponent : this.text) {
      this.tooltipLines.add(lines.size());

      if (textComponent.getString().isEmpty()) {
        lines.add(LiteralText.EMPTY.asOrderedText());
        continue;
      }

      lines.addAll(this.textRenderer.wrapLines(textComponent, w));
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
  protected void drawForeground(MatrixStack matrixStack, int x, int y) {
   // no-op
  }

  @Override
  protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawMouseoverTooltip(matrices, mouseX, mouseY);

    if (this.tooltips == null) {
      return;
    }

    if (mouseX < this.x || mouseX > this.guiRight()) {
      return;
    }

    // floating over tooltip info?
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasTooltips() && mouseX >= this.guiRight() - this.border.w - this.textRenderer.getWidth("?") / 2 && mouseX < this.guiRight()
        && mouseY > this.y + 5 && mouseY < this.y + 5 + scaledFontHeight) {
      this.renderOrderedTooltip(matrices, this.textRenderer.wrapLines(new TranslatableText("gui.tconstruct.general.hover"), 150), mouseX - 155, mouseY);
    }

    // are we hovering over an entry?
    float y = getTooltipStart(5 + this.y);
    float textHeight = (textRenderer.fontHeight + 0.5f) * this.textScale;
    float lowerBound = (this.y + this.backgroundHeight - 5);

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<OrderedText> iter = this.getTotalLines().listIterator(slider.getValue());

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

    List<OrderedText> lines = this.textRenderer.wrapLines(this.tooltips.get(i), w);

    this.renderOrderedTooltip(matrices, lines, mouseX, (mouseY - lines.size() * this.getScaledFontHeight() / 2));
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
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.client.getTextureManager().bindTexture(BACKGROUND_IMAGE);

    this.border.draw(matrices);
    BACKGROUND.drawScaled(matrices, this.x + 4, this.y + 4, this.backgroundWidth - 8, this.backgroundHeight - 8);

    float y = 5 + this.y;
    float x = 5 + this.x;
    int color = 0xfff0f0f0;

    // info ? in the top right corner
    if (this.hasTooltips()) {
      this.textRenderer.draw(matrices, "?", guiRight() - this.border.w - this.textRenderer.getWidth("?") / 2f, this.y + 5, 0xff5f5f5f);
    }

    // draw caption
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.backgroundWidth / 2;
      x2 -= this.textRenderer.getWidth(this.caption) / 2;

      this.textRenderer.drawWithShadow(matrices, this.caption.copy().formatted(Formatting.UNDERLINE).asOrderedText(), (float) this.x + x2, y, color);
      y += scaledFontHeight + 3;
    }

    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = textRenderer.fontHeight + 0.5f;
    float lowerBound = (this.y + this.backgroundHeight - 5) / this.textScale;
    RenderSystem.scalef(this.textScale, this.textScale, 1.0f);
    x /= this.textScale;
    y /= this.textScale;

    // render shown lines
    ListIterator<OrderedText> iter = this.getTotalLines().listIterator(this.slider.getValue());
    while (iter.hasNext()) {
      if (y + textHeight - 0.5f > lowerBound) {
        break;
      }

      OrderedText line = iter.next();
      this.textRenderer.drawWithShadow(matrices, line, x, y, color);
      y += textHeight;
    }

    RenderSystem.scalef(1f / textScale, 1f / textScale, 1.0f);

    this.client.getTextureManager().bindTexture(BACKGROUND_IMAGE);
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
