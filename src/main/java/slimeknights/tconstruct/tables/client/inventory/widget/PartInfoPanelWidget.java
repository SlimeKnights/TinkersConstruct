package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.RenderUtils;

import java.util.ListIterator;

public class PartInfoPanelWidget extends InfoPanelWidget {
  private static final String COST_KEY = TConstruct.makeTranslationKey("gui", "part_builder.cost");
  private static final String MATERIAL_VALUE_KEY = TConstruct.makeTranslationKey("gui", "part_builder.material_value");

  private Component patternCost;
  private Component materialValue;

  public PartInfoPanelWidget(MultiModuleScreen<?> parent) {
    super(parent);
    this.patternCost = TextComponent.EMPTY;
    this.materialValue = TextComponent.EMPTY;
  }

  /* Pattern cost */

  /**
   * Clears the pattern cost text
   */
  public void clearPatternCost() {
    this.patternCost = TextComponent.EMPTY;
    this.updateSliderParameters();
  }

  /**
   * Sets the pattern cost
   * @param cost  Pattern cost
   */
  public void setPatternCost(int cost) {
    this.patternCost = new TranslatableComponent(COST_KEY, cost).withStyle(ChatFormatting.GOLD);
    this.updateSliderParameters();
  }

  /** If true, has pattern cost text */
  private boolean hasPatternCost() {
    return this.patternCost != null && this.patternCost != TextComponent.EMPTY;
  }

  public Component getPatternCost() {
    return this.patternCost;
  }

  public void setRawPatternCost(Component component) {
    this.patternCost = component;
    this.updateSliderParameters();
  }

  /* Material value */

  /**
   * Sets the material value
   * @param value  Value text
   */
  public void setMaterialValue(Component value) {
    this.materialValue = new TranslatableComponent(MATERIAL_VALUE_KEY, value).withStyle(style -> style.withColor(TextColor.fromRgb(0x7fffff)));
    this.updateSliderParameters();
  }

  /**
   * Clears the material value
   */
  public void clearMaterialValue() {
    this.materialValue = TextComponent.EMPTY;
    this.updateSliderParameters();
  }

  /** If true, has material value text */
  private boolean hasMaterialValue() {
    return this.materialValue != null && this.materialValue != TextComponent.EMPTY;
  }

  public Component getMaterialValue() {
    return this.materialValue;
  }

  public void setRawMaterialValue(Component component) {
    this.materialValue = component;
    this.updateSliderParameters();
  }

  @Override
  public int calcNeededHeight() {
    int neededHeight = 0;

    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      neededHeight += scaledFontHeight + 3;
    }

    if (this.hasPatternCost()) {
      neededHeight += scaledFontHeight + 3;
    }

    if (this.hasMaterialValue()) {
      neededHeight += scaledFontHeight + 3;
    }

    neededHeight += (scaledFontHeight + 0.5f) * this.getTotalLines().size();

    return neededHeight;
  }

  @Override
  protected float getTooltipStart(float y) {
    y = super.getTooltipStart(y);
    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasPatternCost()) {
      y += scaledFontHeight + 3;
    }
    if (this.hasMaterialValue()) {
      y += scaledFontHeight + 3;
    }
    return y;
  }

  @Override
  public void render(PoseStack matrices, int mouseX, int mouseY, float partialTick) {
    RenderUtils.setup(BACKGROUND_IMAGE);

    this.border.draw(matrices);
    BACKGROUND.drawScaled(matrices, this.leftPos + 4, this.topPos + 4, this.imageWidth - 8, this.imageHeight - 8);

    float y = 5 + this.topPos;
    float x = 5 + this.leftPos;
    int color = 0xfff0f0f0;

    // info ? in the top right corner
    if (this.hasTooltips()) {
      this.font.draw(matrices, "?", guiRight() - this.border.w - this.font.width("?") / 2f, this.topPos + 5, 0xff5f5f5f);
    }

    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.caption) / 2;

      this.font.drawShadow(matrices, this.caption.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw pattern cost
    if (this.hasPatternCost()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.patternCost) / 2;

      this.font.drawShadow(matrices, this.patternCost.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw material value
    if (this.hasMaterialValue()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.materialValue) / 2;

      this.font.drawShadow(matrices, this.materialValue.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += scaledFontHeight + 3;
    }

    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = font.lineHeight + 0.5f;
    float lowerBound = (this.topPos + this.imageHeight - 5) / this.textScale;
    //RenderSystem.scalef(this.textScale, this.textScale, 1.0f);
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
    //RenderSystem.scalef(1f / textScale, 1f / textScale, 1.0f);

    //this.minecraft.getTextureManager().bind(BACKGROUND_IMAGE);
    RenderUtils.setup(BACKGROUND_IMAGE);
    this.slider.update(mouseX, mouseY);
    this.slider.draw(matrices);
  }
}
