package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.TConstruct;

public class PartInfoPanelWidget extends InfoPanelWidget {
  private static final String COST_KEY = TConstruct.makeTranslationKey("gui", "part_builder.cost");
  private static final String MATERIAL_VALUE_KEY = TConstruct.makeTranslationKey("gui", "part_builder.material_value");

  private Component patternCost;
  private Component materialValue;

  public PartInfoPanelWidget(Screen parent, int leftPos, int topPos, int width, int height, float textScale) {
    super(parent, Style.PLAIN, leftPos, topPos, width, height, textScale);
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
  protected int getCaptionsHeight() {
    int scaledFontHeight = this.getScaledFontHeight();
    int height = super.getCaptionsHeight();
    if (this.hasPatternCost())
      height += scaledFontHeight + 3;
    if (this.hasMaterialValue())
      height += scaledFontHeight + 3;
    return height;
  }

  @Override
  protected float drawCaptions(PoseStack matrices, float y, int color) {

    y = super.drawCaptions(matrices, y, color);
    int scaledFontHeight = this.getScaledFontHeight();

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
    return y;
  }
}
