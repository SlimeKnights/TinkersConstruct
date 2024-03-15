package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;

import java.util.ListIterator;

public class PartInfoPanelScreen extends InfoPanelScreen {
  private static final String COST_KEY = TConstruct.makeTranslationKey("gui", "part_builder.cost");
  private static final String MATERIAL_VALUE_KEY = TConstruct.makeTranslationKey("gui", "part_builder.material_value");

  private Component patternCost;
  private Component materialValue;

  public PartInfoPanelScreen(MultiModuleScreen parent, AbstractContainerMenu container, Inventory playerInventory, Component title) {
    super(parent, container, playerInventory, title);
    this.patternCost = Component.empty();
    this.materialValue = Component.empty();
  }

  /* Pattern cost */

  /**
   * Clears the pattern cost text
   */
  public void clearPatternCost() {
    this.patternCost = Component.empty();
    this.updateSliderParameters();
  }

  /**
   * Sets the pattern cost
   * @param cost  Pattern cost
   */
  public void setPatternCost(int cost) {
    this.patternCost = Component.translatable(COST_KEY, cost).withStyle(ChatFormatting.GOLD);
    this.updateSliderParameters();
  }

  /** If true, has pattern cost text */
  private boolean hasPatternCost() {
    return this.patternCost != null && this.patternCost != Component.empty();
  }

  /* Material value */

  /**
   * Sets the material value
   * @param value  Value text
   */
  public void setMaterialValue(Component value) {
    this.materialValue = Component.translatable(MATERIAL_VALUE_KEY, value).withStyle(style -> style.withColor(TextColor.fromRgb(0x7fffff)));
    this.updateSliderParameters();
  }

  /**
   * Clears the material value
   */
  public void clearMaterialValue() {
    this.materialValue = Component.empty();
    this.updateSliderParameters();
  }

  /** If true, has material value text */
  private boolean hasMaterialValue() {
    return this.materialValue != null && this.materialValue != Component.empty();
  }

  @Override
  public int calcNeededHeight() {
    int neededHeight = 0;

    if (!this.hasInitialized()) {
      return height;
    }

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
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
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
