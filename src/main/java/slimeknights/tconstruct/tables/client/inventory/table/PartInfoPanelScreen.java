package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontColor;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;

import java.util.List;
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
    this.updateSliderParameters();;
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

    if (this.hasCaption()) {
      neededHeight += this.font.FONT_HEIGHT;
      neededHeight += 3;
    }

    if (this.hasPatternCost()) {
      neededHeight += this.font.FONT_HEIGHT;
      neededHeight += 3;
    }

    if (this.hasMaterialValue()) {
      neededHeight += this.font.FONT_HEIGHT;
      neededHeight += 3;
    }

    neededHeight += (this.font.FONT_HEIGHT + 0.5f) * this.getTotalLines().size();

    return neededHeight;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    if (this.tooltips == null) {
      return;
    }

    if (mouseX < this.guiLeft || mouseX > this.guiRight()) {
      return;
    }

    // floating over tooltip info?
    if (this.hasTooltips() && mouseX >= this.guiRight() - this.border.w - this.font.getStringWidth("?") / 2 && mouseX < this.guiRight() && mouseY > this.guiTop + 5 && mouseY < this.guiTop + 5 + this.font.FONT_HEIGHT) {
      int w = MathHelper.clamp(this.width - mouseX - 12, 10, 200);
      this.renderTooltip(matrices, this.font.func_238425_b_(new TranslationTextComponent("gui.tconstruct.general.hover"), w), mouseX - guiLeft, mouseY - guiTop);
    }

    // are we hovering over an entry?
    float y = 5 + this.guiTop;

    if (this.hasCaption()) {
      y += this.font.FONT_HEIGHT + 3;
    }

    if (this.hasPatternCost()) {
      y += this.font.FONT_HEIGHT + 3;
    }

    if (this.hasMaterialValue()) {
      y += this.font.FONT_HEIGHT + 3;
    }

    float textHeight = this.font.FONT_HEIGHT * this.textScale + 0.5f;
    float lowerBound = (this.guiTop + this.ySize - 5) / this.textScale;

    // get the index of the currently hovered line
    int index = -1;
    ListIterator<ITextProperties> iter = this.getTotalLines().listIterator(slider.getValue());

    while (iter.hasNext()) {
      if (y + textHeight > lowerBound) {
        break;
      }

      if (mouseY > y && mouseY <= y + textHeight) {
        index = iter.nextIndex();
        break;
      } else {
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

    List<ITextProperties> lines = this.font.func_238425_b_(this.tooltips.get(i), w);

    this.renderTooltip(matrices, lines, mouseX - this.guiLeft, mouseY - this.guiTop - lines.size() * this.font.FONT_HEIGHT / 2);
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

    // draw caption
    if (this.hasCaption()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.func_238414_a_(this.caption) / 2;

      this.font.func_238407_a_(matrices, this.caption.mergeStyle(TextFormatting.UNDERLINE), (float) this.guiLeft + x2, y, color);
      y += this.font.FONT_HEIGHT + 3;
    }

    // Draw pattern cost
    if (this.hasPatternCost()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.func_238414_a_(this.patternCost) / 2;

      this.font.func_238407_a_(matrices, this.patternCost.mergeStyle(TextFormatting.GOLD), (float) this.guiLeft + x2, y, color);
      y += this.font.FONT_HEIGHT + 3;
    }

    // Draw material value
    if (this.hasMaterialValue()) {
      int x2 = this.xSize / 2;
      x2 -= this.font.func_238414_a_(this.materialValue) / 2;

      this.font.func_238407_a_(matrices, this.materialValue.modifyStyle(style -> style.setColor(CustomFontColor.getColor("7fffff"))), (float) this.guiLeft + x2, y, color);
      y += this.font.FONT_HEIGHT + 3;
    }

    if (this.text == null || this.text.size() == 0) {
      // no text to draw
      return;
    }

    float textHeight = this.font.FONT_HEIGHT * this.textScale + 0.5f;
    float lowerBound = (this.guiTop + this.ySize - 5) / this.textScale;
    RenderSystem.scalef(this.textScale, this.textScale, 1.0f);
    x /= this.textScale;
    y /= this.textScale;

    // render shown lines
    ListIterator<ITextProperties> iter = this.getTotalLines().listIterator(this.slider.getValue());
    while (iter.hasNext()) {
      if (y + textHeight - 0.5f > lowerBound) {
        break;
      }

      ITextProperties line = iter.next();
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
