package slimeknights.tconstruct.tables.client.inventory.table;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import java.util.List;
import java.util.function.Function;

public class PartBuilderScreen extends BaseStationScreen<PartBuilderTileEntity, PartBuilderContainer> {
  private static final ITextComponent INFO_TEXT = Util.makeTranslation("gui", "part_builder.info");
  private static final ITextComponent TRAIT_TITLE = Util.makeTranslation("gui", "part_builder.trait").mergeStyle(TextFormatting.UNDERLINE);
  private static final IFormattableTextComponent UNCRAFTABLE_MATERIAL = Util.makeTranslation("gui", "part_builder.uncraftable").mergeStyle(TextFormatting.RED);
  private static final IFormattableTextComponent UNCRAFTABLE_MATERIAL_TOOLTIP = Util.makeTranslation("gui", "part_builder.uncraftable.tooltip");

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  /** Part builder side panel */
  protected PartInfoPanelScreen infoPanelScreen;
  /** Current scrollbar position */
  private float sliderProgress = 0.0F;
  /** Is {@code true} if the player clicked on the scroll wheel in the GUI */
  private boolean clickedOnScrollBar;

  /**
   * The index of the first recipe to display.
   * The number of recipes displayed at any time is 12 (4 recipes per row, and 3 rows). If the player scrolled down one
   * row, this value would be 4 (representing the index of the first slot on the second row).
   */
  private int recipeIndexOffset = 0;

  public PartBuilderScreen(PartBuilderContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.infoPanelScreen = new PartInfoPanelScreen(this, container, playerInventory, title);
    this.infoPanelScreen.setTextScale(7/9f);
    this.infoPanelScreen.ySize = this.ySize;
    this.addModule(this.infoPanelScreen);
    addChestSideInventory();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, BACKGROUND);

    // draw slot icons
    this.drawIconEmpty(matrices, this.container.getPatternSlot(), Icons.PATTERN);
    this.drawIconEmpty(matrices, this.container.getInputSlot(), Icons.INGOT);

    // draw scrollbar
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(BACKGROUND);
    this.blit(matrices, this.cornerX + 126, this.cornerY + 15 + (int) (41.0F * this.sliderProgress), 176 + (this.canScroll() ? 0 : 12), 0, 12, 15);
    this.drawRecipesBackground(matrices, mouseX, mouseY, this.cornerX + 51, this.cornerY + 15);
    this.drawRecipesItems(matrices, this.cornerX + 51, this.cornerY + 15);

    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);
  }

  /**
   * Gets the button at the given mouse location
   * @param mouseX  X position of button
   * @param mouseY  Y position of button
   * @return  Button index, or -1 if none
   */
  private int getButtonAt(int mouseX, int mouseY) {
    List<Pattern> buttons = tile.getSortedButtons();
    if (!buttons.isEmpty()) {
      int x = this.cornerX + 51;
      int y = this.cornerY + 15;
      int maxIndex = Math.min((this.recipeIndexOffset + 12), buttons.size());
      for (int l = this.recipeIndexOffset; l < maxIndex; ++l) {
        int relative = l - this.recipeIndexOffset;
        double buttonX = mouseX - (double) (x + relative % 4 * 18);
        double buttonY = mouseY - (double) (y + relative / 4 * 18);
        if (buttonX >= 0.0D && buttonY >= 0.0D && buttonX < 18.0D && buttonY < 18.0D) {
          return l;
        }
      }
    }
    return -1;
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrixStack, mouseX, mouseY);

    // determime which button we are hovering
    List<Pattern> buttons = tile.getSortedButtons();
    if (!buttons.isEmpty()) {
      int index = getButtonAt(mouseX, mouseY);
      if (index >= 0) {
        renderTooltip(matrixStack, buttons.get(index).getDisplayName(), mouseX, mouseY);
      }
    }
  }

  /** Draw backgrounds for all patterns */
  private void drawRecipesBackground(MatrixStack matrices, int mouseX, int mouseY, int left, int top) {
    int max = Math.min(this.recipeIndexOffset + 12, this.getPartRecipeCount());
    for (int i = this.recipeIndexOffset; i < max; ++i) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * 18;
      int y = top + (relative / 4) * 18;
      int u = this.ySize;
      if (i == this.tile.getSelectedIndex()) {
        u += 18;
      } else if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
        u += 36;
      }
      this.blit(matrices, x, y, 0, u, 18, 18);
    }
  }

  /** Draw slot icons for all patterns */
  private void drawRecipesItems(MatrixStack matrices, int left, int top) {
    // use block texture list
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    Function<ResourceLocation, TextureAtlasSprite> spriteGetter = this.minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    // iterate all recipes
    List<Pattern> list = this.tile.getSortedButtons();
    int max = Math.min(this.recipeIndexOffset + 12, this.getPartRecipeCount());
    for (int i = this.recipeIndexOffset; i < max; ++i) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * 18 + 1;
      int y = top + (relative / 4) * 18 + 1;
      // get the sprite for the pattern and draw
      Pattern pattern = list.get(i);
      TextureAtlasSprite sprite = spriteGetter.apply(pattern.getTexture());
      blit(matrices, x, y, 100, 16, 16, sprite);
    }
  }

  @Override
  public void updateDisplay() {
    // if we can no longer scroll, reset scrollbar progress
    // fixes the case where we added an item and lost recipes
    if (!canScroll()) {
      this.sliderProgress = 0.0F;
      this.recipeIndexOffset = 0;
    }

    // update part recipe cost
    IPartBuilderRecipe partRecipe = this.tile.getPartRecipe();
    if (partRecipe != null) {
      this.infoPanelScreen.setPatternCost(partRecipe.getCost());
    } else {
      this.infoPanelScreen.clearPatternCost();
    }

    // update material
    MaterialRecipe materialRecipe = this.tile.getMaterialRecipe();
    if (materialRecipe != null) {
      this.setDisplayForMaterial(materialRecipe);
    } else {
      // default text
      this.infoPanelScreen.setCaption(this.getTitle());
      this.infoPanelScreen.setText(INFO_TEXT);
      this.infoPanelScreen.clearMaterialValue();
    }
  }

  /**
   * Updates the data in the material display
   * @param materialRecipe  New material recipe
   */
  private void setDisplayForMaterial(MaterialRecipe materialRecipe) {
    IMaterial material = materialRecipe.getMaterial();
    this.infoPanelScreen.setCaption(new TranslationTextComponent(material.getTranslationKey()).modifyStyle(style -> style.setColor(material.getColor())));

    // determine how much material we have
    // get exact number of material, rather than rounded
    float value = materialRecipe.getMaterialValue(this.tile.getInventoryWrapper());
    IFormattableTextComponent formatted = new StringTextComponent(Util.df.format(value));

    // if we have a part recipe, mark material red when not enough
    IPartBuilderRecipe partRecipe = this.tile.getPartRecipe();
    if (partRecipe != null && value < partRecipe.getCost()) {
      formatted = formatted.mergeStyle(TextFormatting.DARK_RED);
    }
    this.infoPanelScreen.setMaterialValue(formatted);

    // update stats and traits
    List<ITextComponent> stats = Lists.newLinkedList();
    List<ITextComponent> tips = Lists.newArrayList();

    // add warning that the material is uncraftable
    if (!material.isCraftable()) {
      stats.add(UNCRAFTABLE_MATERIAL);
      stats.add(StringTextComponent.EMPTY);
      tips.add(UNCRAFTABLE_MATERIAL_TOOLTIP);
      tips.add(StringTextComponent.EMPTY);
    }

    for (IMaterialStats stat : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
      List<ITextComponent> info = stat.getLocalizedInfo();

      if (!info.isEmpty()) {
        stats.add(stat.getLocalizedName().mergeStyle(TextFormatting.UNDERLINE));
        tips.add(StringTextComponent.EMPTY);

        stats.addAll(info);
        tips.addAll(stat.getLocalizedDescriptions());

        List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(material.getIdentifier(), stat.getIdentifier());
        if (!traits.isEmpty()) {
          for (ModifierEntry trait : traits) {
            Modifier mod = trait.getModifier();
            stats.add(mod.getDisplayName(trait.getLevel()));
            tips.add(mod.getDescription());
          }
        }

        stats.add(StringTextComponent.EMPTY);
        tips.add(StringTextComponent.EMPTY);
      }
    }

    // remove last line if empty
    if (!stats.isEmpty() && stats.get(stats.size() - 1).getString().isEmpty()) {
      stats.remove(stats.size() - 1);
      tips.remove(tips.size() - 1);
    }

    this.infoPanelScreen.setText(stats, tips);
  }


  /* Scrollbar logic */

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    this.clickedOnScrollBar = false;

    if (this.infoPanelScreen.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    List<Pattern> buttons = tile.getSortedButtons();
    if (!buttons.isEmpty()) {
      // handle button click
      int index = getButtonAt((int)mouseX, (int)mouseY);
      assert this.minecraft != null && this.minecraft.player != null;
      if (index >= 0 && this.container.enchantItem(this.minecraft.player, index)) {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
        assert this.minecraft.playerController != null;
        this.minecraft.playerController.sendEnchantPacket((this.container).windowId, index);
        return true;
      }

      // scrollbar position
      int x = this.cornerX + 126;
      int y = this.cornerY + 15;
      if (mouseX >= x && mouseX < (x + 12) && mouseY >= y && mouseY < (y + 54)) {
        this.clickedOnScrollBar = true;
      }
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unknown) {
    if (this.infoPanelScreen.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    if (this.clickedOnScrollBar && this.canScroll()) {
      int i = this.cornerY + 14;
      int j = i + 54;
      this.sliderProgress = ((float) mouseY - i - 7.5F) / ((float) (j - i) - 15.0F);
      this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((this.sliderProgress * this.getHiddenRows()) + 0.5D) * 4;
      return true;
    } else {
      return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unknown);
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    //if (this.infoPanelScreen.handleMouseScrolled(mouseX, mouseY, delta)) {
    //  return false;
    //}
    if (super.mouseScrolled(mouseX, mouseY, delta)) {
      return true;
    }

    if (this.canScroll()) {
      int i = this.getHiddenRows();
      this.sliderProgress = MathHelper.clamp((float) (this.sliderProgress - delta / i), 0.0F, 1.0F);
      this.recipeIndexOffset = (int) ((this.sliderProgress * (float) i) + 0.5f) * 4;
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.infoPanelScreen.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    return super.mouseReleased(mouseX, mouseY, state);
  }


  /* Update error logic */

  @Override
  public void error(ITextComponent message) {
    this.infoPanelScreen.setCaption(COMPONENT_ERROR);
    this.infoPanelScreen.setText(message);
  }

  @Override
  public void warning(ITextComponent message) {
    this.infoPanelScreen.setCaption(COMPONENT_WARNING);
    this.infoPanelScreen.setText(message);
  }


  /* Helpers */

  /** Gets the number of part recipes */
  private int getPartRecipeCount() {
    return tile.getSortedButtons().size();
  }

  /** If true, we can scroll */
  private boolean canScroll() {
    return this.getPartRecipeCount() > 12;
  }

  /** Gets the number of hidden part recipe rows */
  private int getHiddenRows() {
    return (this.getPartRecipeCount() + 4 - 1) / 4 - 3;
  }
}
