package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class PartInfoPanelWidget extends InfoPanelWidget {

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

  public void setPatternCost(Component component) {
    this.patternCost = component;
    this.updateSliderParameters();
  }

  /** If true, has pattern cost text */
  private boolean hasPatternCost() {
    return this.patternCost != null && this.patternCost != TextComponent.EMPTY;
  }

  public Component getPatternCost() {
    return this.patternCost;
  }

  /* Material value */

  public void setMaterialValue(Component value) {
    this.materialValue = value;
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

  @Override
  protected int getCaptionsHeight() {
    int height = super.getCaptionsHeight();
    if (this.hasPatternCost())
      height += this.font.lineHeight + 1;
    if (this.hasMaterialValue())
      height += this.font.lineHeight + 1;
    return height;
  }

  @Override
  protected float drawCaptions(PoseStack matrices, float y, int color) {

    y = super.drawCaptions(matrices, y, color);

    // Draw pattern cost
    if (this.hasPatternCost()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.patternCost) / 2;

      this.font.drawShadow(matrices, this.patternCost.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += this.font.lineHeight + 1;
    }

    // Draw material value
    if (this.hasMaterialValue()) {
      int x2 = this.imageWidth / 2;
      x2 -= this.font.width(this.materialValue) / 2;

      this.font.drawShadow(matrices, this.materialValue.getVisualOrderText(), (float) this.leftPos + x2, y, color);
      y += this.font.lineHeight + 1;
    }
    return y;
  }
}
