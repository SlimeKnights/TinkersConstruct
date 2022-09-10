package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.item.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutIcon;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.TinkerTooltipFlags;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerStationButtonsScreen;
import slimeknights.tconstruct.tables.client.inventory.widget.InfoPanelWidget;
import slimeknights.tconstruct.tables.client.inventory.widget.SlotButtonItem;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;
import slimeknights.tconstruct.tables.menu.slot.TinkerStationSlot;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity.INPUT_SLOT;
import static slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity.TINKER_SLOT;

public class TinkerStationScreen extends BaseTabbedScreen<TinkerStationBlockEntity,TinkerStationContainerMenu> {
  // titles to display
  private static final Component COMPONENTS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.components");
  private static final Component MODIFIERS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.modifiers");
  private static final Component UPGRADES_TEXT = TConstruct.makeTranslation("gui", "tinker_station.upgrades");
  private static final Component TRAITS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.traits");
  // fallback text for crafting with no named slots
  private static final Component ASCII_ANVIL = new TextComponent("\n\n")
    .append("       .\n")
    .append("     /( _________\n")
    .append("     |  >:=========`\n")
    .append("     )(  \n")
    .append("     \"\"")
    .withStyle(ChatFormatting.DARK_GRAY);

  // parameters to display the still filled slots when changing layout
  private static final int STILL_FILLED_X = 112;
  private static final int STILL_FILLED_Y = 62;
  private static final int STILL_FILLED_SPACING = 18;

  // texture
  private static final ResourceLocation TINKER_STATION_TEXTURE = TConstruct.getResource("textures/gui/tinker_station.png");
  // texture elements
  private static final ElementScreen ACTIVE_TEXT_FIELD = new ElementScreen(0, 210, 91, 12, 256, 256);
  private static final ElementScreen ITEM_COVER = new ElementScreen(176, 18, 70, 64);
  // slots
  private static final ElementScreen SLOT_BACKGROUND = new ElementScreen(176, 0, 18, 18);
  private static final ElementScreen SLOT_BORDER = new ElementScreen(194, 0, 18, 18);
  private static final ElementScreen SLOT_SPACE_TOP = new ElementScreen(0, 174 + 2, 18, 2);
  private static final ElementScreen SLOT_SPACE_BOTTOM = new ElementScreen(0, 174, 18, 2);
  // panel
  private static final ElementScreen PANEL_SPACE_LEFT = new ElementScreen(0, 174, 5, 4);
  private static final ElementScreen PANEL_SPACE_RIGHT = new ElementScreen(9, 174, 9, 4);

  private static final ElementScreen BUTTONS_BEAM = new ElementScreen(0, 180, 112, 7);
  private static final ElementScreen PANEL_BEAM = new ElementScreen(0, 180, 128, 7);

  /** Number of button columns in the UI */
  public static final int COLUMN_COUNT = 5;

  private final InfoPanelWidget.Style style;

  // configurable elements
  protected ElementScreen buttonDecorationTop = SLOT_SPACE_TOP;
  protected ElementScreen buttonDecorationBot = SLOT_SPACE_BOTTOM;
  protected ElementScreen panelDecorationL = PANEL_SPACE_LEFT;
  protected ElementScreen panelDecorationR = PANEL_SPACE_RIGHT;

  protected ElementScreen buttonsBeam = BUTTONS_BEAM;
  protected ElementScreen panelBeam = PANEL_BEAM;

  /** Gets the default layout to apply, the "repair" button */
  @Nonnull @Getter
  private final StationSlotLayout defaultLayout;
  /** Currently selected tool */
  @Nonnull
  private StationSlotLayout currentLayout;

  // components
  //protected TextFieldWidget textField;
  protected InfoPanelWidget tinkerInfo;
  protected InfoPanelWidget modifierInfo;
  protected TinkerStationButtonsScreen buttonsScreen;

  /** Maximum available slots */
  @Getter
  private final int maxInputs;
  /** How many of the available input slots are active */
  protected int activeInputs;

  public TinkerStationScreen(TinkerStationContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);

    this.buttonsScreen = new TinkerStationButtonsScreen(this, container, playerInventory, title);
    this.addModule(this.buttonsScreen);

    this.imageHeight = 174;

    // determine number of inputs
    int max = 5;
    TinkerStationBlockEntity te = container.getTile();
    if (te != null) {
      max = te.getInputCount(); // TODO: not station sensitive
    }
    this.maxInputs = max;

    // large if at least 4, todo can configure?
    if (max > 3) {
      this.metal();
      style = InfoPanelWidget.Style.METAL;
    } else {
      this.wood();
      style = InfoPanelWidget.Style.WOOD;
    }
    // apply base slot information
    if (te == null) {
      this.defaultLayout = StationSlotLayout.EMPTY;
    } else {
      this.defaultLayout = StationSlotLayoutLoader.getInstance().get(Objects.requireNonNull(te.getBlockState().getBlock().getRegistryName()));
    }
    this.currentLayout = this.defaultLayout;
    this.activeInputs = Math.min(defaultLayout.getInputCount(), max);
    this.passEvents = false;
  }

  @Override
  public void init() {

    assert this.minecraft != null;
    // TODO: pretty sure we don't need this unless we add back the renaming slot
    this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.topPos += 4;
    this.cornerY += 4;

    //this.textField = new TextFieldWidget(this.font, this.cornerX + 81, this.cornerY + 7, 91, 12, TextComponent.EMPTY);
    //this.textField.setEnableBackgroundDrawing(false);
    //this.textField.setMaxStringLength(40);

    // Center the buttons under the beam
    this.buttonsScreen.xOffset = -3;
    this.buttonsScreen.yOffset = this.buttonsBeam.h + this.buttonDecorationTop.h;

    for (ModuleScreen<?,?> module : this.modules) {
      module.topPos += 4;
    }

    super.init();

    // Center the panels under the beam
    final int panelLeft = this.cornerX + this.realWidth + (this.panelBeam.w - InfoPanelWidget.DEFAULT_WIDTH)/2;
    final int panelTop = this.cornerY + this.panelBeam.h + this.panelDecorationL.h;
    int panelHeight = (this.imageHeight - this.panelBeam.h - 2 * this.panelDecorationL.h)/2;
    this.tinkerInfo = addExtraArea(addRenderableWidget(new InfoPanelWidget(this, style,
      panelLeft, panelTop, InfoPanelWidget.DEFAULT_WIDTH, panelHeight, 8/9f)));
    this.modifierInfo = addExtraArea(addRenderableWidget(new InfoPanelWidget(this, style,
      panelLeft, panelTop + panelHeight + this.panelDecorationL.h, InfoPanelWidget.DEFAULT_WIDTH, panelHeight, 7/9f)));

    this.updateLayout();
  }

  @Override
  public void resize(Minecraft mc, int width, int height) {
    var tinkerData = tinkerInfo.getData();
    var modifierData = modifierInfo.getData();

    super.resize(mc, width, height);

    tinkerInfo.setData(tinkerData);
    modifierInfo.setData(modifierData);
  }

  @Override
  public void onClose() {
    super.onClose();

    assert this.minecraft != null;
    this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
  }

  /** Updates all slots for the current slot layout */
  public void updateLayout() {
    int stillFilled = 0;
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.getMenu().getSlot(i);
      LayoutSlot layoutSlot = currentLayout.getSlot(i);
      if (layoutSlot.isHidden()) {
        // put the position in the still filled line
        slot.x = STILL_FILLED_X - STILL_FILLED_SPACING * stillFilled;
        slot.y = STILL_FILLED_Y;
        stillFilled++;
        if (slot instanceof TinkerStationSlot tinkerSlot) {
          tinkerSlot.deactivate();
        }
      } else {
        slot.x = layoutSlot.getX();
        slot.y = layoutSlot.getY();
        if (slot instanceof TinkerStationSlot tinkerSlot) {
          tinkerSlot.activate(layoutSlot);
        }
      }
    }

    this.updateDisplay();
  }

  @Override
  public void updateDisplay() {
    if (this.tile == null) {
      return;
    }

    ItemStack toolStack = this.getMenu().getResult();

    // if we have a message, display instead of refreshing the tool
    ValidatedResult currentError = tile.getCurrentError();
    if (currentError.hasError()) {
      error(currentError.getMessage());
      return;
    }

    // normal refresh
    if (toolStack.isEmpty()) {
      toolStack = this.getMenu().getSlot(TINKER_SLOT).getItem();
    }

    // if the contained stack is modifiable, display some information
    if (toolStack.is(TinkerTags.Items.MODIFIABLE)) {
      ToolStack tool = ToolStack.from(toolStack);
      if (toolStack.getItem() instanceof ITinkerStationDisplay display) {
        this.tinkerInfo.setCaptions(display.getLocalizedName().copy().withStyle(ChatFormatting.UNDERLINE));
        // TODO: tooltips on these?
        assert minecraft != null;
        this.tinkerInfo.setText(display.getStatInformation(tool, minecraft.player, new ArrayList<>(), SafeClientAccess.getTooltipKey(), TinkerTooltipFlags.TINKER_STATION));
      }
      else {
        this.tinkerInfo.setCaptions(toolStack.getDisplayName().copy().withStyle(ChatFormatting.UNDERLINE));
        this.tinkerInfo.setText();
      }

      List<Component> modifierNames = new ArrayList<>();
      List<Component> modifierInfo = new ArrayList<>();
      Component title;
      // control displays just traits, bit trickier to do
      if (hasControlDown()) {
        title = TRAITS_TEXT;
        Map<Modifier,Integer> upgrades = tool.getUpgrades().getModifiers().stream()
                                             .collect(Collectors.toMap(ModifierEntry::getModifier, ModifierEntry::getLevel));
        for (ModifierEntry entry : tool.getModifierList()) {
          Modifier mod = entry.getModifier();
          if (mod.shouldDisplay(true)) {
            int level = entry.getLevel() - upgrades.getOrDefault(mod, 0);
            if (level > 0) {
              modifierNames.add(mod.getDisplayName(tool, level));
              modifierInfo.add(mod.getDescription(tool, level));
            }
          }
        }
      } else {
        // shift is just upgrades/abilities, otherwise all
        List<ModifierEntry> modifiers;
        if (hasShiftDown()) {
          modifiers = tool.getUpgrades().getModifiers();
          title = UPGRADES_TEXT;
        } else {
          modifiers = tool.getModifierList();
          title = MODIFIERS_TEXT;
        }
        for (ModifierEntry entry : modifiers) {
          Modifier mod = entry.getModifier();
          if (mod.shouldDisplay(true)) {
            int level = entry.getLevel();
            modifierNames.add(mod.getDisplayName(tool, level));
            modifierInfo.add(mod.getDescription(tool, level));
          }
        }
      }

      this.modifierInfo.setCaptions(title.copy().withStyle(ChatFormatting.UNDERLINE));
      this.modifierInfo.setText(modifierNames, modifierInfo);
    }
    // tool build info
    else {
      this.tinkerInfo.setCaptions(this.currentLayout.getDisplayName().copy().withStyle(ChatFormatting.UNDERLINE));
      this.tinkerInfo.setText(this.currentLayout.getDescription());

      // for each named slot, color the slot if the slot is filled
      // typically all input slots should be named, or none of them
      MutableComponent fullText = new TextComponent("");
      boolean hasComponents = false;
      for (int i = 0; i <= activeInputs; i++) {
        LayoutSlot layout = currentLayout.getSlot(i);
        String key = layout.getTranslationKey();
        if (!layout.isHidden() && !key.isEmpty()) {
          hasComponents = true;
          MutableComponent textComponent = new TextComponent(" * ");
          ItemStack slotStack = this.getMenu().getSlot(i).getItem();
          if (!layout.isValid(slotStack)) {
            textComponent.withStyle(ChatFormatting.RED);
          }
          textComponent.append(new TranslatableComponent(key)).append("\n");
          fullText.append(textComponent);
        }
      }
      // if we found any components, set the text, use the anvil if no components
      if (hasComponents) {
        this.modifierInfo.setCaptions(COMPONENTS_TEXT.copy().withStyle(ChatFormatting.UNDERLINE));
        this.modifierInfo.setText(fullText);
      } else {
        this.modifierInfo.setCaptions();
        this.modifierInfo.setText(ASCII_ANVIL);
      }
    }
  }

  @Override
  protected void drawContainerName(PoseStack matrixStack) {
    this.font.draw(matrixStack, this.getTitle(), 8.0F, 8.0F, 4210752);
  }

  private static void renderPattern(PoseStack matrices, Pattern pattern, int x, int y) {
    TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(pattern.getTexture());
    blit(matrices, x, y, 100, 16, 16, sprite);
  }

  public static void renderIcon(PoseStack matrices, LayoutIcon icon, int x, int y) {
    Pattern pattern = icon.getValue(Pattern.class);
    Minecraft minecraft = Minecraft.getInstance();
    if (pattern != null) {
      // draw pattern sprite
      RenderUtils.setup(InventoryMenu.BLOCK_ATLAS);
      RenderSystem.applyModelViewMatrix();
      renderPattern(matrices, pattern, x, y);
      return;
    }

    ItemStack stack = icon.getValue(ItemStack.class);
    if (stack != null) {
      minecraft.getItemRenderer().renderGuiItem(stack, x, y);
    }
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, TINKER_STATION_TEXTURE);

    // looks like there's a weird case where this is called before init? Not reproducible but meh.
    /* TODO: keep this?
    if (this.textField != null) {
      if (this.textField.isFocused()) {
        ACTIVE_TEXT_FIELD.draw(matrices, cornerX + 79, cornerY + 6);
      }

      // draw textField
      this.textField.render(matrices, mouseX, mouseY, partialTicks);
    }
    */

    int x = 0;
    int y = 0;

    // draw the item background
    final float scale = 3.7f;
    final float xOff = 12.5f;
    final float yOff = 22f;

    // render the background icon
    PoseStack renderPose = RenderSystem.getModelViewStack();
    renderPose.pushPose();
    renderPose.translate(xOff, yOff, 0.0F);
    renderPose.scale(scale, scale, 1.0f);
    renderIcon(matrices, currentLayout.getIcon(), (int) (this.cornerX / scale), (int) (this.cornerY / scale));
    renderPose.popPose();
    RenderSystem.applyModelViewMatrix();

    // rebind gui texture since itemstack drawing sets it to something else
    RenderUtils.setup(TINKER_STATION_TEXTURE, 1.0f, 1.0f, 1.0f, 0.82f);
    RenderSystem.enableBlend();
    //RenderSystem.enableAlphaTest();
    //RenderHelper.turnOff();
    RenderSystem.disableDepthTest();
    ITEM_COVER.draw(matrices, this.cornerX + 7, this.cornerY + 18);

    // slot backgrounds, are transparent
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.28f);
    if (!this.currentLayout.getToolSlot().isHidden()) {
      Slot slot = this.getMenu().getSlot(TINKER_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
    }
    for (int i = 0; i < this.activeInputs; i++) {
      Slot slot = this.getMenu().getSlot(i + INPUT_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
    }

    // slot borders, are opaque
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.getMenu().getSlot(i);
      if ((slot instanceof TinkerStationSlot && (!((TinkerStationSlot) slot).isDormant() || slot.hasItem()))) {
        SLOT_BORDER.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
      }
    }

    // sidebar beams
    this.buttonsBeam.draw(matrices, this.cornerX - this.buttonsBeam.w, this.cornerY);
    this.panelBeam.draw(matrices, this.cornerX + this.realWidth, this.cornerY);

    // draw the decoration for the buttons
    for (Widget widget : this.buttonsScreen.getButtons()) {
      if (widget instanceof SlotButtonItem button) {
        this.buttonDecorationTop.draw(matrices, button.x, button.y - this.buttonDecorationTop.h);
        // don't draw the bottom for the buttons in the last row
        if (button.buttonId < this.buttonsScreen.getButtons().size() - COLUMN_COUNT) {
          this.buttonDecorationBot.draw(matrices, button.x, button.y + button.getHeight());
        }
      }
    }

    // draw the decorations for the panels
    this.panelDecorationL.draw(matrices, this.tinkerInfo.getLeft() + 5, this.tinkerInfo.getTop() - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.tinkerInfo.getRight() - 5 - this.panelDecorationR.w, this.tinkerInfo.getTop() - this.panelDecorationR.h);
    this.panelDecorationL.draw(matrices, this.modifierInfo.getLeft() + 5, this.modifierInfo.getTop() - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.modifierInfo.getRight() - 5 - this.panelDecorationR.w, this.modifierInfo.getTop() - this.panelDecorationR.h);

    // render slot background icons
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.getMenu().getSlot(i);
      if (!slot.hasItem()) {
        Pattern icon = currentLayout.getSlot(i).getIcon();
        if (icon != null) {
          renderPattern(matrices, icon, this.cornerX + slot.x, this.cornerY + slot.y);
        }
      }
    }

    RenderSystem.enableDepthTest();

    super.renderBg(matrices, partialTicks, mouseX, mouseY);
  }

  @Override
  protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
    super.renderTooltip(poseStack, mouseX, mouseY);

    this.tinkerInfo.renderTooltip(poseStack, mouseX, mouseY);
    this.modifierInfo.renderTooltip(poseStack, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if(this.buttonsScreen.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    // TODO: textField
    // this.textField.mouseClicked(mouseX, mouseY, mouseButton)
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.tinkerInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return true;
    }

    if (this.modifierInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return true;
    }

    if (this.buttonsScreen.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    return super.mouseReleased(mouseX, mouseY, state);
  }

  /** Returns true if a key changed that requires a display update */
  private static boolean needsDisplayUpdate(int keyCode) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
      return true;
    }
    if (Minecraft.ON_OSX) {
      return keyCode == GLFW.GLFW_KEY_LEFT_SUPER || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER;
    }
    return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (needsDisplayUpdate(keyCode)) {
      updateDisplay();
    }

    // TODO: textField
    //boolean keyPressed = this.textField.keyPressed(keyCode, scanCode, modifiers);
    //if (keyPressed) {
      //TinkerNetwork.getInstance().sendToServer(new ToolStationTextPacket(this.textField.getText()));
      //this.container.setToolName(textField.getText());
    //}
    // keyPressed || this.textField.canWrite() ||
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (needsDisplayUpdate(keyCode)) {
      updateDisplay();
    }
    return super.keyReleased(keyCode, scanCode, modifiers);
  }

  /* TODO: textField
  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    if (!this.textField.isFocused()) {
      return super.charTyped(typedChar, keyCode);
    }
    else {
      if (keyCode == 1) {
        assert this.minecraft != null;
        assert this.minecraft.player != null;
        this.minecraft.player.closeScreen();
        return true;
      }

      return this.textField.charTyped(typedChar, keyCode);
    }
  }

  @Override
  protected void insertText(String text, boolean setText) {
    if (setText) {
      this.textField.setText(text);
    } else {
      this.textField.writeText(text);
    }
  }

  @Override
  public void tick() {
    super.tick();

    this.textField.tick();
  }
  */

  @Override
  public void renderSlot(PoseStack matrixStack, Slot slotIn) {
    // don't draw dormant slots with no item
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.hasItem()) {
      return;
    }
    super.renderSlot(matrixStack, slotIn);
  }

  @Override
  public boolean isHovering(Slot slotIn, double mouseX, double mouseY) {
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.hasItem()) {
      return false;
    }
    return super.isHovering(slotIn, mouseX, mouseY);
  }

  protected void wood() {
    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18, 0);

    this.buttonsScreen.shiftStyle(TinkerStationButtonsScreen.WOOD_STYLE);

    this.buttonsBeam = BUTTONS_BEAM;
    this.panelBeam = PANEL_BEAM;
  }

  protected void metal() {
    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w * 2, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w * 2, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18 * 2, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18 * 2, 0);

    this.buttonsScreen.shiftStyle(TinkerStationButtonsScreen.METAL_STYLE);

    this.buttonsBeam = BUTTONS_BEAM.shift(0, BUTTONS_BEAM.h);
    this.panelBeam = PANEL_BEAM.shift(0, PANEL_BEAM.h);
  }

  @Override
  public void error(Component message) {
    this.tinkerInfo.setCaptions(COMPONENT_ERROR.copy().withStyle(ChatFormatting.UNDERLINE));
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaptions();
    this.modifierInfo.setText();
  }

  @Override
  public void warning(Component message) {
    this.tinkerInfo.setCaptions(COMPONENT_WARNING.copy().withStyle(ChatFormatting.UNDERLINE));
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaptions();
    this.modifierInfo.setText();
  }

  /**
   * Called when a tool button is pressed
   * @param layout      Data of the slot selected
   */
  public void onToolSelection(StationSlotLayout layout) {
    this.activeInputs = Math.min(layout.getInputCount(), maxInputs);
    this.currentLayout = layout;
    this.updateLayout();

    // update the active slots and filter in the container
    // this.container.setToolSelection(layout); TODO: needed?
    TinkerNetwork.getInstance().sendToServer(new TinkerStationSelectionPacket(layout.getName()));
  }

  private Rect2i getButtonsBeamArea() {
    return new Rect2i(this.cornerX - this.buttonsBeam.w, this.cornerY,
      this.buttonsBeam.w, this.buttonsBeam.h);
  }

  private Rect2i getPanelBeamArea() {
    return new Rect2i(this.cornerX + this.realWidth, this.cornerY,
      this.panelBeam.w, this.panelBeam.h);
  }

  @Override
  public List<Rect2i> getModuleAreas() {
    List<Rect2i> areas = super.getModuleAreas();
    areas.add(this.getButtonsBeamArea());
    areas.add(this.getPanelBeamArea());
    return areas;
  }

  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
    return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)
      && !this.getButtonsBeamArea().contains((int) mouseX, (int) mouseY) && !this.getPanelBeamArea().contains((int) mouseX, (int) mouseY);
  }
}
