package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;

import java.util.ListIterator;

public class PartInfoPanelScreen extends InfoPanelScreen {

  private IFormattableTextComponent patternCost;
  private IFormattableTextComponent materialValue;
  public static final IFormattableTextComponent EMPTY = new StringTextComponent("");

  public PartInfoPanelScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title);
    this.patternCost = PartInfoPanelScreen.EMPTY;
    this.materialValue = PartInfoPanelScreen.EMPTY;
  }

  /* Pattern cost */

  /**
   * Clears the pattern cost text
   */
  public void clearPatternCost() {
    this.patternCost = PartInfoPanelScreen.EMPTY;
    this.updateSliderParameters();
  }

  /**
   * Sets the pattern cost
   * @param cost  Pattern cost
   */
  public void setPatternCost(int cost) {
    this.patternCost = new TranslationTextComponent("gui.tconstruct.part_builder.cost", cost);
    this.updateSliderParameters();
  }

  /** If true, has pattern cost text */
  private boolean hasPatternCost() {
    return this.patternCost != null && this.patternCost != PartInfoPanelScreen.EMPTY;
  }

  /* Material value */

  /**
   * Sets the material value
   * @param value  Value text
   */
  public void setMaterialValue(ITextComponent value) {
    this.materialValue = new TranslationTextComponent("gui.tconstruct.part_builder.material_value", value);
    this.updateSliderParameters();
  }

  /**
   * Clears the material value
   */
  public void clearMaterialValue() {
    this.materialValue = PartInfoPanelScreen.EMPTY;
    this.updateSliderParameters();
  }

  /** If true, has material value text */
  private boolean hasMaterialValue() {
    return this.materialValue != null && this.materialValue != PartInfoPanelScreen.EMPTY;
  }

  @Override
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

    if (this.hasPatternCost()) {
      neededHeight += scaledFontHeight;
      neededHeight += 3;
    }

    if (this.hasMaterialValue()) {
      neededHeight += scaledFontHeight;
      neededHeight += 3;
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
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    assert this.minecraft != null;
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

    int scaledFontHeight = this.getScaledFontHeight();
    if (this.hasCaption()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.getStringPropertyWidth(this.caption) / 2;

      this.font.func_238407_a_(matrices, this.caption.copyRaw().mergeStyle(TextFormatting.UNDERLINE).func_241878_f(), (float) this.guiLeft + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw pattern cost
    if (this.hasPatternCost()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.getStringPropertyWidth(this.patternCost) / 2;

      this.font.func_238407_a_(matrices, this.patternCost.mergeStyle(TextFormatting.GOLD).func_241878_f(), (float) this.guiLeft + x2, y, color);
      y += scaledFontHeight + 3;
    }

    // Draw material value
    if (this.hasMaterialValue()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.getStringPropertyWidth(this.materialValue) / 2;

      this.font.func_238407_a_(matrices, this.materialValue.modifyStyle(style -> style.setColor(Color.fromInt(0x7fffff))).func_241878_f(), (float) this.guiLeft + x2, y, color);
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
}
