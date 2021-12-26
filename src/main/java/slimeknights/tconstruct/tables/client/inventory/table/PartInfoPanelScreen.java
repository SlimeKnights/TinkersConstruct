package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;

import java.util.ListIterator;

public class PartInfoPanelScreen extends InfoPanelScreen {
  private static final String COST_KEY = TConstruct.makeTranslationKey("gui", "part_builder.cost");
  private static final String MATERIAL_VALUE_KEY = TConstruct.makeTranslationKey("gui", "part_builder.material_value");

  private ITextComponent patternCost;
  private ITextComponent materialValue;

  public PartInfoPanelScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title);
    this.patternCost = StringTextComponent.EMPTY;
    this.materialValue = StringTextComponent.EMPTY;
  }

  /* Pattern cost */

  /**
   * Clears the pattern cost text
   */
  public void clearPatternCost() {
    this.patternCost = StringTextComponent.EMPTY;
    this.updateSliderParameters();
  }

  /**
   * Sets the pattern cost
   * @param cost  Pattern cost
   */
  public void setPatternCost(int cost) {
    this.patternCost = new TranslationTextComponent(COST_KEY, cost).withStyle(TextFormatting.GOLD);
    this.updateSliderParameters();
  }

  /** If true, has pattern cost text */
  private boolean hasPatternCost() {
    return this.patternCost != null && this.patternCost != StringTextComponent.EMPTY;
  }

  /* Material value */

  /**
   * Sets the material value
   * @param value  Value text
   */
  public void setMaterialValue(ITextComponent value) {
    this.materialValue = new TranslationTextComponent(MATERIAL_VALUE_KEY, value).withStyle(style -> style.withColor(Color.fromRgb(0x7fffff)));
    this.updateSliderParameters();
  }

  /**
   * Clears the material value
   */
  public void clearMaterialValue() {
    this.materialValue = StringTextComponent.EMPTY;
    this.updateSliderParameters();
  }

  /** If true, has material value text */
  private boolean hasMaterialValue() {
    return this.materialValue != null && this.materialValue != StringTextComponent.EMPTY;
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
  protected void renderBg(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bind(BACKGROUND_IMAGE);

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

      this.font.drawShadow(matrices, this.caption.plainCopy().withStyle(TextFormatting.UNDERLINE).getVisualOrderText(), (float) this.leftPos + x2, y, color);
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
      this.font.drawShadow(matrices, line, x, y, color);
      y += textHeight;
    }

    RenderSystem.scalef(1f / textScale, 1f / textScale, 1.0f);

    this.minecraft.getTextureManager().bind(BACKGROUND_IMAGE);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.slider.update(mouseX, mouseY);
    this.slider.draw(matrices);
  }
}
