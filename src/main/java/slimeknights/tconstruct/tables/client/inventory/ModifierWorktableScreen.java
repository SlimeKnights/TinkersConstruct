package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.block.entity.table.ModifierWorktableBlockEntity;
import slimeknights.tconstruct.tables.menu.ModifierWorktableContainerMenu;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import java.util.Collections;
import java.util.List;

public class ModifierWorktableScreen extends ToolTableScreen<ModifierWorktableBlockEntity,ModifierWorktableContainerMenu> {
  protected static final Component TITLE = TConstruct.makeTranslation("gui", "modifier_worktable.title");
  protected static final Component TABLE_INFO = TConstruct.makeTranslation("gui", "modifier_worktable.info");
  private static final Component MODIFIERS = TConstruct.makeTranslation("gui", "tinker_station.modifiers");
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/worktable.png");
  private static final Pattern[] INPUT_PATTERNS = {
    new Pattern(TConstruct.MOD_ID, "pickaxe"),
    new Pattern(TConstruct.MOD_ID, "ingot"),
    new Pattern(TConstruct.MOD_ID, "quartz")
  };

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
  private static final int SLIDER_LEFT = 103;
  /** Furthest top position of the slider */
  private static final int SLIDER_TOP = 15;
  // modifiers
  /** Furthest left position of the modifiers */
  private static final int MODIFIER_LEFT = 28;
  /** Furthest top position of the modifiers */
  private static final int MODIFIER_TOP = 15;
  /** Largest modifiers index to display */
  private static final int MAX_MODIFIER = 16;
  /** Width and height of a modifiers button */
  private static final int MODIFIER_SIZE = 18;
  /** U coordinate of the modifiers button texture */
  private static final int MODIFIER_U = 176;
  /** V coordinate of the first modifiers button texture */
  private static final int MODIFIER_V_START = 15;

  /** Current scrollbar position */
  private float sliderProgress = 0.0F;
  /** Is {@code true} if the player clicked on the scroll wheel in the GUI */
  private boolean clickedOnScrollBar;

  /**
   * The index of the first recipe to display.
   * The number of recipes displayed at any time is 16 (4 recipes per row, and 4 rows). If the player scrolled down one
   * row, this value would be 4 (representing the index of the first slot on the second row).
   */
  private int modifierIndexOffset = 0;

  public ModifierWorktableScreen(ModifierWorktableContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);

    this.imageHeight = 184;

    this.tinkerInfo.yOffset = 0;
    this.modifierInfo.yOffset = this.tinkerInfo.imageHeight + 4;

    if (addChestSideInventory(playerInventory)) {
      enableArmorStandPreview = false;
    }
  }

  @Override
  protected void init() {
    super.init();
    if (tile != null) {
      LazyToolStack lazyResult = tile.getResult();
      if (lazyResult != null) {
        updateArmorStandPreview(lazyResult.getStack());
      } else {
        updateArmorStandPreview(menu.getSlot(ModifierWorktableBlockEntity.TINKER_SLOT).getItem());
      }
    }

    this.setupArmorStandPreview(-55, 134, 50);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(graphics, BACKGROUND);

    // draw scrollbar
    graphics.blit(BACKGROUND, this.cornerX + SLIDER_LEFT, this.cornerY + SLIDER_TOP + (int) (SROLLABLE_AREA * this.sliderProgress), canScroll() ? HANDLE_U : HANDLE_U_DISABLE, 0, SLIDER_WIDTH, HANDLE_HEIGHT);
    this.drawModifierBackgrounds(graphics, mouseX, mouseY, this.cornerX + MODIFIER_LEFT, this.cornerY + MODIFIER_TOP);

    // draw slot icons
    List<Slot> slots = this.getMenu().getInputSlots();
    int max = Math.min(slots.size(), INPUT_PATTERNS.length);
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    for (int i = 0; i < max; i++) {
      this.drawIconEmpty(graphics, slots.get(i), INPUT_PATTERNS[i]);
    }
    this.drawModifierIcons(graphics, this.cornerX + MODIFIER_LEFT, this.cornerY + MODIFIER_TOP);

    super.renderBg(graphics, partialTicks, mouseX, mouseY);

    renderArmorStand(graphics);
  }

  /**
   * Gets the button at the given mouse location
   * @param mouseX  X position of button
   * @param mouseY  Y position of button
   * @return  Button index, or -1 if none
   */
  private int getButtonAt(int mouseX, int mouseY) {
    if (tile != null) {
      List<ModifierEntry> buttons = tile.getCurrentButtons();
      if (!buttons.isEmpty()) {
        int x = this.cornerX + MODIFIER_LEFT;
        int y = this.cornerY + MODIFIER_TOP;
        int maxIndex = Math.min((this.modifierIndexOffset + MAX_MODIFIER), buttons.size());
        for (int i = this.modifierIndexOffset; i < maxIndex; ++i) {
          int relative = i - this.modifierIndexOffset;
          double buttonX = mouseX - (double)(x + relative % 4 * MODIFIER_SIZE);
          double buttonY = mouseY - (double)(y + relative / 4 * MODIFIER_SIZE);
          if (buttonX >= 0 && buttonY >= 0 && buttonX < MODIFIER_SIZE && buttonY < MODIFIER_SIZE) {
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
      List<ModifierEntry> buttons = tile.getCurrentButtons();
      if (!buttons.isEmpty()) {
        int index = getButtonAt(mouseX, mouseY);
        if (index >= 0) {
          graphics.renderTooltip(this.font, buttons.get(index).getDisplayName(), mouseX, mouseY);
        }
      }
    }
  }

  /** Draw backgrounds for all modifiers */
  private void drawModifierBackgrounds(GuiGraphics graphics, int mouseX, int mouseY, int left, int top) {
    if (tile != null) {
      int selectedIndex = this.tile.getSelectedIndex();
      int max = Math.min(this.modifierIndexOffset + MAX_MODIFIER, this.getModifierCount());
      for (int i = this.modifierIndexOffset; i < max; i++) {
        int relative = i - this.modifierIndexOffset;
        int x = left + relative % 4 * MODIFIER_SIZE;
        int y = top + (relative / 4) * MODIFIER_SIZE;
        int v = MODIFIER_V_START;
        if (i == selectedIndex) {
          v += MODIFIER_SIZE;
        } else if (mouseX >= x && mouseY >= y && mouseX < x + MODIFIER_SIZE && mouseY < y + MODIFIER_SIZE) {
          v += 2 * MODIFIER_SIZE;
        }
        graphics.blit(BACKGROUND, x, y, MODIFIER_U, v, MODIFIER_SIZE, MODIFIER_SIZE);
      }
    }
  }

  /** Draw slot icons for all patterns */
  private void drawModifierIcons(GuiGraphics graphics, int left, int top) {
    // use block texture list
    if (tile != null) {
      assert this.minecraft != null;
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      // iterate all recipes
      List<ModifierEntry> list = this.tile.getCurrentButtons();
      int max = Math.min(this.modifierIndexOffset + MAX_MODIFIER, this.getModifierCount());
      for (int i = this.modifierIndexOffset; i < max; ++i) {
        int relative = i - this.modifierIndexOffset;
        int x = left + relative % 4 * MODIFIER_SIZE + 1;
        int y = top + (relative / 4) * MODIFIER_SIZE + 1;
        ModifierIconManager.renderIcon(graphics, list.get(i).getModifier(), x, y, 100, 16);
      }
    }
  }

  @Override
  public void updateDisplay() {
    if (canScroll()) {
      // if we can still scroll, make sure the scroll bar is in a valid position
      this.modifierIndexOffset = Math.min(this.modifierIndexOffset, getModifierCount() - MAX_MODIFIER);
      this.sliderProgress = this.modifierIndexOffset / 4f / this.getHiddenRows();
    } else {
      // if we can no longer scroll, reset scrollbar progress
      this.sliderProgress = 0;
      this.modifierIndexOffset = 0;
    }

    if (tile != null) {
      LazyToolStack lazyResult = tile.getResult();
      // set armor stand preview to input or result
      if (lazyResult == null) {
        updateArmorStandPreview(menu.getSlot(ModifierWorktableBlockEntity.TINKER_SLOT).getItem());
      } else {
        updateArmorStandPreview(lazyResult.getStack());
      }


      // if we have a message, just stop now
      Component message = tile.getCurrentMessage();
      if (!message.getString().isEmpty()) {
        message(message);
        return;
      }

      if (lazyResult == null) {
        updateArmorStandPreview(menu.getSlot(ModifierWorktableBlockEntity.TINKER_SLOT).getItem());
        message(TABLE_INFO);
        return;
      }

      // reuse logic from tinker station for final result
      updateToolPanel(lazyResult);

      this.modifierInfo.setCaption(Component.empty());
      this.modifierInfo.setText(Component.empty());
      ToolStack result = lazyResult.getTool();
      if (result.hasTag(TinkerTags.Items.MODIFIABLE)) {
        updateModifierPanel(result);
      } else {
        // modifier crystals can show their modifier, along with anything else with a modifier there
        ModifierId modifierId = ModifierCrystalItem.getModifier(lazyResult.getStack());
        if (modifierId != null) {
          Modifier modifier = ModifierManager.getValue(modifierId);
          modifierInfo.setCaption(MODIFIERS);
          modifierInfo.setText(Collections.singletonList(modifier.getDisplayName()), Collections.singletonList(modifier.getDescription()));
        }
      }
    }
  }

  /* Scrollbar logic */

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    this.clickedOnScrollBar = false;

    if (this.tinkerInfo.handleMouseClicked(mouseX, mouseY, mouseButton)
        || this.modifierInfo.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    if (tile != null && !tile.getCurrentButtons().isEmpty()) {
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
    if (this.tinkerInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        || this.modifierInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    if (this.clickedOnScrollBar && this.canScroll()) {
      int barStart = this.cornerY + SLIDER_TOP;
      int barEnd = barStart + BAR_HEIGHT;
      this.sliderProgress = ((float) mouseY - barStart - 7.5F) / (barEnd - barStart - SLIDER_TOP);
      this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
      this.modifierIndexOffset = Math.round(this.sliderProgress * this.getHiddenRows()) * 4;
      return true;
    }

    return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unknown);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (this.tinkerInfo.handleMouseScrolled(mouseX, mouseY, delta)
        || this.modifierInfo.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }
    if (super.mouseScrolled(mouseX, mouseY, delta)) {
      return true;
    }

    if (this.canScroll()) {
      int hidden = this.getHiddenRows();
      this.sliderProgress = Mth.clamp((float) (this.sliderProgress - delta / hidden), 0, 1);
      this.modifierIndexOffset = Math.round(this.sliderProgress * hidden) * 4;
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.tinkerInfo.handleMouseReleased(mouseX, mouseY, state)
        || this.modifierInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }
    return super.mouseReleased(mouseX, mouseY, state);
  }


  /* Update error logic */

  @Override
  public void error(Component message) {
    this.tinkerInfo.setCaption(COMPONENT_ERROR);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(Component.empty());
    this.modifierInfo.setText(Component.empty());
  }

  @Override
  public void warning(Component message) {
    this.tinkerInfo.setCaption(COMPONENT_WARNING);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(Component.empty());
    this.modifierInfo.setText(Component.empty());
  }

  private Component getInfoTitle() {
    if (tile != null) {
      IModifierWorktableRecipe recipe = tile.getCurrentRecipe();
      if (recipe != null) {
        return recipe.getTitle();
      }
    }
    return TITLE;
  }

  /** Displays a message with the default title */
  public void message(Component message) {
    this.tinkerInfo.setCaption(getInfoTitle());
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(Component.empty());
    this.modifierInfo.setText(Component.empty());
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (TinkerStationScreen.needsDisplayUpdate(keyCode)) {
      updateDisplay();
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (TinkerStationScreen.needsDisplayUpdate(keyCode)) {
      updateDisplay();
    }
    return super.keyReleased(keyCode, scanCode, modifiers);
  }


  /* Helpers */

  /** Gets the number of modifiers */
  private int getModifierCount() {
    return tile == null ? 0 : tile.getCurrentButtons().size();
  }

  /** If true, we can scroll */
  private boolean canScroll() {
    return this.getModifierCount() > MAX_MODIFIER;
  }

  /** Gets the number of hidden modifier button rows */
  private int getHiddenRows() {
    return (this.getModifierCount() + 3) / 4 - 4;
  }
}
