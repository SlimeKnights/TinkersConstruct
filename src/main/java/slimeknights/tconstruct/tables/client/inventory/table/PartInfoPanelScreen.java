package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;

import java.util.ListIterator;

public class PartInfoPanelScreen extends InfoPanelScreen {
  private static final String COST_KEY = Util.makeTranslationKey("gui", "part_builder.cost");
  private static final String MATERIAL_VALUE_KEY = Util.makeTranslationKey("gui", "part_builder.material_value");

  private MutableText patternCost;
  private MutableText materialValue;
  public static final MutableText EMPTY = new LiteralText("");

  public PartInfoPanelScreen(MultiModuleScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title) {
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
    this.patternCost = new TranslatableText(COST_KEY, cost).mergeStyle(TextFormatting.GOLD);
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
  public void setMaterialValue(Text value) {
    this.materialValue = new TranslatableText(MATERIAL_VALUE_KEY, value).modifyStyle(style -> style.setColor(Color.fromInt(0x7fffff)));
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
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    assert this.client != null;
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

    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.backgroundWidth / 2;
      x2 -= this.textRenderer.getWidth(this.caption) / 2;

      this.textRenderer.drawWithShadow(matrices, this.caption.copy().formatted(Formatting.UNDERLINE).asOrderedText(), (float) this.x + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw pattern cost
    if (this.hasPatternCost()) {
      int x2 = this.backgroundWidth / 2;
      x2 -= this.textRenderer.getWidth(this.patternCost) / 2;

      this.font.drawWithShadow(matrices, this.patternCost.asOrderedText(), (float) this.guiLeft + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw material value
    if (this.hasMaterialValue()) {
      int x2 = this.backgroundWidth / 2;
      x2 -= this.textRenderer.getWidth(this.materialValue) / 2;

      this.font.drawWithShadow(matrices, this.materialValue.asOrderedText(), (float) this.guiLeft + x2, y, color);
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
}
