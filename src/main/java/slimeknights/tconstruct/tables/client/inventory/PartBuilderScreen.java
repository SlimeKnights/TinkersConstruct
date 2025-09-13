package slimeknights.tconstruct.tables.client.inventory;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;
import slimeknights.tconstruct.tables.menu.PartBuilderContainerMenu;

import java.util.List;
import java.util.function.Function;

public class PartBuilderScreen extends BaseTabbedScreen<PartBuilderBlockEntity,PartBuilderContainerMenu> {
  private static final Component INFO_TEXT = TConstruct.makeTranslation("gui", "part_builder.info");
  private static final Component TRAIT_TITLE = TConstruct.makeTranslation("gui", "part_builder.trait").withStyle(ChatFormatting.UNDERLINE);
  private static final MutableComponent UNCRAFTABLE_MATERIAL = TConstruct.makeTranslation("gui", "part_builder.uncraftable").withStyle(ChatFormatting.RED);
  private static final MutableComponent UNCRAFTABLE_MATERIAL_TOOLTIP = TConstruct.makeTranslation("gui", "part_builder.uncraftable.tooltip");
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/part_builder.png");
  // locations
  // slider
  /** Texture U for the handle texture */
  private static final int HANDLE_U = 176;
  /** Texture U for the handle texture when the scrollbar is disabled */
  private static final int HANDLE_U_DISABLE = 188;
  /** Width of the slider handle */
  private static final int SLIDER_WIDTH = 12;
  /** Height of the slider handle */
  private static final int HANDLE_HEIGHT = 15;
  /** Height of the full slider bar */
  private static final int BAR_HEIGHT = 72;
  /** Height of the scrollable bar area */
  private static final int SROLLABLE_AREA = BAR_HEIGHT + 2 - HANDLE_HEIGHT;
  /** Furthest left position of slider */
  private static final int SLIDER_LEFT = 126;
  /** Furthest top position of the slider */
  private static final int SLIDER_TOP = 15;
  // patterns
  /** Furthest left position of the patterns */
  private static final int PATTERN_LEFT = 51;
  /** Furthest top position of the patterns */
  private static final int PATTERN_TOP = 15;
  /** Largest pattern index to display */
  private static final int MAX_PATTERN = 16;
  /** Width and height of a pattern button */
  private static final int PATTERN_SIZE = 18;
  /** U coordinate of the pattern textures */
  private static final int PATTERN_U = 176;
  /** V coordinate of the first pattern texture */
  private static final int PATTERN_V_START = 15;

  /** Part builder side panel */
  protected PartInfoPanelScreen infoPanelScreen;
  /** Current scrollbar position */
  private float sliderProgress = 0.0F;
  /** Is {@code true} if the player clicked on the scroll wheel in the GUI */
  private boolean clickedOnScrollBar;

  /**
   * The index of the first recipe to display.
   * The number of recipes displayed at any time is 16 (4 recipes per row, and 4 rows). If the player scrolled down one
   * row, this value would be 4 (representing the index of the first slot on the second row).
   */
  private int recipeIndexOffset = 0;

  public PartBuilderScreen(PartBuilderContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);

    this.imageHeight = 184;

    this.infoPanelScreen = new PartInfoPanelScreen(this, container, playerInventory, title);
    this.infoPanelScreen.setTextScale(7/9f);
    this.infoPanelScreen.imageHeight = this.imageHeight;
    this.addModule(this.infoPanelScreen);
    addChestSideInventory(playerInventory);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(graphics, BACKGROUND);

    // draw scrollbar
    graphics.blit(BACKGROUND, this.cornerX + SLIDER_LEFT, this.cornerY + SLIDER_TOP + (int) (SROLLABLE_AREA * this.sliderProgress), canScroll() ? HANDLE_U : HANDLE_U_DISABLE, 0, SLIDER_WIDTH, HANDLE_HEIGHT);
    this.drawRecipesBackground(graphics, mouseX, mouseY, this.cornerX + PATTERN_LEFT, this.cornerY + PATTERN_TOP);

    // draw slot icons
    this.drawIconEmpty(graphics, this.getMenu().getPatternSlot(), Icons.PATTERN);
    this.drawIconEmpty(graphics, this.getMenu().getInputSlot(), Icons.INGOT);
    this.drawRecipesItems(graphics, this.cornerX + PATTERN_LEFT, this.cornerY + PATTERN_TOP);

    super.renderBg(graphics, partialTicks, mouseX, mouseY);
  }

  /**
   * Gets the button at the given mouse location
   * @param mouseX  X position of button
   * @param mouseY  Y position of button
   * @return  Button index, or -1 if none
   */
  private int getButtonAt(int mouseX, int mouseY) {
    if (tile != null) {
      List<Pattern> buttons = tile.getSortedButtons();
      if (!buttons.isEmpty()) {
        int x = this.cornerX + PATTERN_LEFT;
        int y = this.cornerY + PATTERN_TOP;
        int maxIndex = Math.min((this.recipeIndexOffset + MAX_PATTERN), buttons.size());
        for (int i = this.recipeIndexOffset; i < maxIndex; ++i) {
          int relative = i - this.recipeIndexOffset;
          double buttonX = mouseX - (double)(x + relative % 4 * PATTERN_SIZE);
          double buttonY = mouseY - (double)(y + relative / 4 * PATTERN_SIZE);
          if (buttonX >= 0 && buttonY >= 0 && buttonX < PATTERN_SIZE && buttonY < PATTERN_SIZE) {
            return i;
          }
        }
      }
    }
    return -1;
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    // determime which button we are hovering
    if (tile != null) {
      List<Pattern> buttons = tile.getSortedButtons();
      if (!buttons.isEmpty()) {
        int index = getButtonAt(mouseX, mouseY);
        if (index >= 0) {
          graphics.renderTooltip(this.font, buttons.get(index).getDisplayName(), mouseX, mouseY);
        }
      }
    }
  }

  /** Draw backgrounds for all patterns */
  private void drawRecipesBackground(GuiGraphics graphics, int mouseX, int mouseY, int left, int top) {
    if (tile == null) {
      return;
    }
    int max = Math.min(this.recipeIndexOffset + MAX_PATTERN, this.getPartRecipeCount());
    for (int i = this.recipeIndexOffset; i < max; i++) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * PATTERN_SIZE;
      int y = top + (relative / 4) * PATTERN_SIZE;
      int v = PATTERN_V_START;
      if (i == this.tile.getSelectedIndex()) {
        v += PATTERN_SIZE;
      } else if (mouseX >= x && mouseY >= y && mouseX < x + PATTERN_SIZE && mouseY < y + PATTERN_SIZE) {
        v += 2 * PATTERN_SIZE;
      }
      graphics.blit(BACKGROUND, x, y, PATTERN_U, v, PATTERN_SIZE, PATTERN_SIZE);
    }
  }

  /** Draw slot icons for all patterns */
  private void drawRecipesItems(GuiGraphics graphics, int left, int top) {
    // use block texture list
    assert this.minecraft != null;
    assert this.tile != null;
    Function<ResourceLocation, TextureAtlasSprite> spriteGetter = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
    // iterate all recipes
    List<Pattern> list = this.tile.getSortedButtons();
    int max = Math.min(this.recipeIndexOffset + MAX_PATTERN, this.getPartRecipeCount());
    for (int i = this.recipeIndexOffset; i < max; ++i) {
      int relative = i - this.recipeIndexOffset;
      int x = left + relative % 4 * PATTERN_SIZE + 1;
      int y = top + (relative / 4) * PATTERN_SIZE + 1;
      // get the sprite for the pattern and draw
      Pattern pattern = list.get(i);
      graphics.blit(x, y, 100, 16, 16, spriteGetter.apply(pattern.getTexture()));
    }
  }

  @Override
  public void updateDisplay() {
    if (canScroll()) {
      // if we can still scroll, make sure the scroll bar is in a valid position
      this.recipeIndexOffset = Math.min(this.recipeIndexOffset, getPartRecipeCount() - MAX_PATTERN);
      this.sliderProgress = this.recipeIndexOffset / 4f / this.getHiddenRows();
    } else {
      // if we can no longer scroll, reset scrollbar progress
      this.sliderProgress = 0;
      this.recipeIndexOffset = 0;
    }

    assert this.tile != null;

    // update material
    IMaterialValue materialRecipe = this.tile.getMaterialRecipe();
    if (materialRecipe != null) {
      this.setDisplayForMaterial(materialRecipe);
    } else {
      // default text
      this.infoPanelScreen.setCaption(this.getTitle());
      this.infoPanelScreen.setText(INFO_TEXT);
      this.infoPanelScreen.clearMaterialValue();
    }

    // update part recipe cost
    IPartBuilderRecipe partRecipe = this.tile.getPartRecipe();
    boolean skipCost = false;
    if (partRecipe == null) {
      partRecipe = this.tile.getFirstRecipe();
      skipCost = true;
    }
    if (partRecipe != null) {
      int cost = partRecipe.getCost();
      if (cost > 0 && !skipCost) {
        this.infoPanelScreen.setPatternCost(cost);
      } else {
        this.infoPanelScreen.clearPatternCost();
      }
      Component title = partRecipe.getTitle();
      if (title != null) {
        this.infoPanelScreen.setCaption(title);
        this.infoPanelScreen.setText(partRecipe.getText(this.tile.getInventoryWrapper()));
      }
    } else {
      this.infoPanelScreen.clearPatternCost();
    }
  }

  /**
   * Updates the data in the material display
   * @param materialRecipe  New material recipe
   */
  private void setDisplayForMaterial(IMaterialValue materialRecipe) {
    if (this.tile == null) {
      return;
    }
    MaterialVariant materialVariant = materialRecipe.getMaterial();
    this.infoPanelScreen.setCaption(MaterialTooltipCache.getColoredDisplayName(materialVariant.getVariant()));

    // determine how much material we have
    // get exact number of material, rather than rounded
    float value = materialRecipe.getMaterialValue(this.tile.getInventoryWrapper());
    MutableComponent formatted = Component.literal(Util.COMMA_FORMAT.format(value));

    // if we have a part recipe, mark material red when not enough
    IPartBuilderRecipe partRecipe = this.tile.getPartRecipe();
    if (partRecipe != null && value < partRecipe.getCost()) {
      formatted = formatted.withStyle(ChatFormatting.DARK_RED);
    }
    this.infoPanelScreen.setMaterialValue(formatted);

    // update stats and traits
    List<Component> stats = Lists.newLinkedList();
    List<Component> tips = Lists.newArrayList();

    // add warning that the material is uncraftable
    if (!materialVariant.get().isCraftable()) {
      stats.add(UNCRAFTABLE_MATERIAL);
      stats.add(Component.empty());
      tips.add(UNCRAFTABLE_MATERIAL_TOOLTIP);
      tips.add(Component.empty());
    }

    MaterialId id = materialVariant.getId();
    for (IMaterialStats stat : MaterialRegistry.getInstance().getAllStats(id)) {
      List<Component> info = stat.getLocalizedInfo();

      if (!info.isEmpty()) {
        stats.add(stat.getLocalizedName().withStyle(ChatFormatting.UNDERLINE));
        tips.add(Component.empty());

        stats.addAll(info);
        tips.addAll(stat.getLocalizedDescriptions());

        List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(id, stat.getIdentifier());
        if (!traits.isEmpty()) {
          for (ModifierEntry trait : traits) {
            if (trait.isBound()) {
              Modifier mod = trait.getModifier();
              stats.add(mod.getDisplayName(trait.getLevel()));
              tips.add(mod.getDescription(trait.getLevel()));
            }
          }
        }

        stats.add(Component.empty());
        tips.add(Component.empty());
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

    if (this.infoPanelScreen.handleMouseClicked(mouseX, mouseY, mouseButton) || this.tile == null) {
      return false;
    }

    List<Pattern> buttons = tile.getSortedButtons();
    if (!buttons.isEmpty()) {
      // handle button click
      int index = getButtonAt((int)mouseX, (int)mouseY);
      assert this.minecraft != null && this.minecraft.player != null;
      if (index >= 0 && this.getMenu().clickMenuButton(this.minecraft.player, index)) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
        assert this.minecraft.gameMode != null;
        this.minecraft.gameMode.handleInventoryButtonClick(this.getMenu().containerId, index);
        return true;
      }

      // scrollbar position
      int x = this.cornerX + SLIDER_LEFT;
      int y = this.cornerY + SLIDER_TOP;
      if (mouseX >= x && mouseX < (x + SLIDER_WIDTH) && mouseY >= y && mouseY < (y + BAR_HEIGHT)) {
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
      int barStart = this.cornerY + SLIDER_TOP;
      int barEnd = barStart + BAR_HEIGHT;
      this.sliderProgress = Mth.clamp(((float) mouseY - barStart - 7.5f) / (barEnd - barStart - SLIDER_TOP), 0, 1);
      this.recipeIndexOffset = Math.round(this.sliderProgress * this.getHiddenRows()) * 4;
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
      int hidden = this.getHiddenRows();
      this.sliderProgress = Mth.clamp((float) (this.sliderProgress - delta / hidden), 0, 1);
      this.recipeIndexOffset = Math.round(this.sliderProgress * hidden) * 4;
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
  public void error(Component message) {
    this.infoPanelScreen.setCaption(COMPONENT_ERROR);
    this.infoPanelScreen.setText(message);
  }

  @Override
  public void warning(Component message) {
    this.infoPanelScreen.setCaption(COMPONENT_WARNING);
    this.infoPanelScreen.setText(message);
  }


  /* Helpers */

  /** Gets the number of part recipes */
  private int getPartRecipeCount() {
    return tile == null ? 0 : tile.getSortedButtons().size();
  }

  /** If true, we can scroll */
  private boolean canScroll() {
    return this.getPartRecipeCount() > MAX_PATTERN;
  }

  /** Gets the number of hidden part recipe rows */
  private int getHiddenRows() {
    return (this.getPartRecipeCount() + 3) / 4 - 4;
  }
}
