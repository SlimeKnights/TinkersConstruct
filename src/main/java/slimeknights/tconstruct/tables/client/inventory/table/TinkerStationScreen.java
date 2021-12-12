package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
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
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.client.inventory.SlotButtonItem;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerStationButtonsScreen;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationSlot;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity.INPUT_SLOT;
import static slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity.TINKER_SLOT;

public class TinkerStationScreen extends BaseStationScreen<TinkerStationTileEntity, TinkerStationContainer> {
  // titles to display
  private static final ITextComponent COMPONENTS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.components");
  private static final ITextComponent MODIFIERS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.modifiers");
  private static final ITextComponent UPGRADES_TEXT = TConstruct.makeTranslation("gui", "tinker_station.upgrades");
  private static final ITextComponent TRAITS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.traits");
  // fallback text for crafting with no named slots
  private static final ITextComponent ASCII_ANVIL = new StringTextComponent("\n\n")
    .appendString("       .\n")
    .appendString("     /( _________\n")
    .appendString("     |  >:=========`\n")
    .appendString("     )(  \n")
    .appendString("     \"\"")
    .mergeStyle(TextFormatting.DARK_GRAY);

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
  private static final ElementScreen LEFT_BEAM = new ElementScreen(0, 180, 2, 7);
  private static final ElementScreen RIGHT_BEAM = new ElementScreen(131, 180, 2, 7);
  private static final ScalableElementScreen CENTER_BEAM = new ScalableElementScreen(2, 180, 129, 7);

  /** Number of button columns in the UI */
  public static final int COLUMN_COUNT = 5;

  // configurable elements
  protected ElementScreen buttonDecorationTop = SLOT_SPACE_TOP;
  protected ElementScreen buttonDecorationBot = SLOT_SPACE_BOTTOM;
  protected ElementScreen panelDecorationL = PANEL_SPACE_LEFT;
  protected ElementScreen panelDecorationR = PANEL_SPACE_RIGHT;

  protected ElementScreen leftBeam = new ElementScreen(0, 0, 0, 0);
  protected ElementScreen rightBeam = new ElementScreen(0, 0, 0, 0);
  protected ScalableElementScreen centerBeam = new ScalableElementScreen(0, 0, 0, 0);

  /** Gets the default layout to apply, the "repair" button */
  @Nonnull @Getter
  private final StationSlotLayout defaultLayout;
  /** Currently selected tool */
  @Nonnull
  private StationSlotLayout currentLayout;

  // components
  //protected TextFieldWidget textField;
  protected InfoPanelScreen tinkerInfo;
  protected InfoPanelScreen modifierInfo;
  protected TinkerStationButtonsScreen buttonsScreen;

  /** Maximum available slots */
  @Getter
  private final int maxInputs;
  /** How many of the available input slots are active */
  protected int activeInputs;

  public TinkerStationScreen(TinkerStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.buttonsScreen = new TinkerStationButtonsScreen(this, container, playerInventory, title);
    this.addModule(this.buttonsScreen);

    this.tinkerInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.tinkerInfo.setTextScale(8/9f);
    this.addModule(this.tinkerInfo);

    this.modifierInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.modifierInfo.setTextScale(7/9f);
    this.addModule(this.modifierInfo);

    this.tinkerInfo.yOffset = 5;
    this.modifierInfo.yOffset = this.tinkerInfo.ySize + 9;

    this.ySize = 174;

    // determine number of inputs
    int max = 5;
    TinkerStationTileEntity te = container.getTile();
    if (te != null) {
      max = te.getInputCount(); // TODO: not station sensitive
    }
    this.maxInputs = max;

    // large if at least 4, todo can configure?
    if (max > 3) {
      this.metal();
    } else {
      this.wood();
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
    super.init();

    assert this.minecraft != null;
    this.minecraft.keyboardListener.enableRepeatEvents(true);

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.guiTop += 4;
    this.cornerY += 4;

    //this.textField = new TextFieldWidget(this.font, this.cornerX + 81, this.cornerY + 7, 91, 12, StringTextComponent.EMPTY);
    //this.textField.setEnableBackgroundDrawing(false);
    //this.textField.setMaxStringLength(40);

    this.buttonsScreen.xOffset = -2;
    this.buttonsScreen.yOffset = this.centerBeam.h + this.buttonDecorationTop.h;
    this.tinkerInfo.xOffset = 2;
    this.tinkerInfo.yOffset = this.centerBeam.h + this.panelDecorationL.h;
    this.modifierInfo.xOffset = this.tinkerInfo.xOffset;
    this.modifierInfo.yOffset = this.tinkerInfo.yOffset + this.tinkerInfo.ySize + 4;

    for (ModuleScreen<?,?> module : this.modules) {
      module.guiTop += 4;
    }

    this.updateLayout();
  }

  @Override
  public void onClose() {
    super.onClose();

    assert this.minecraft != null;
    this.minecraft.keyboardListener.enableRepeatEvents(false);
  }

  /** Updates all slots for the current slot layout */
  public void updateLayout() {
    int stillFilled = 0;
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.container.getSlot(i);
      LayoutSlot layoutSlot = currentLayout.getSlot(i);
      if (layoutSlot.isHidden()) {
        // put the position in the still filled line
        slot.xPos = STILL_FILLED_X - STILL_FILLED_SPACING * stillFilled;
        slot.yPos = STILL_FILLED_Y;
        stillFilled++;
        if (slot instanceof TinkerStationSlot) {
          ((TinkerStationSlot) slot).deactivate();
        }
      } else {
        slot.xPos = layoutSlot.getX();
        slot.yPos = layoutSlot.getY();
        if (slot instanceof TinkerStationSlot) {
          ((TinkerStationSlot) slot).activate(layoutSlot);
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

    ItemStack toolStack = this.container.getResult();

    // if we have a message, display instead of refreshing the tool
    ValidatedResult currentError = tile.getCurrentError();
    if (currentError.hasError()) {
      error(currentError.getMessage());
      return;
    }

    // normal refresh
    if (toolStack.isEmpty()) {
      toolStack = this.container.getSlot(TINKER_SLOT).getStack();
    }

    // if the contained stack is modifiable, display some information
    if (TinkerTags.Items.MODIFIABLE.contains(toolStack.getItem())) {
      ToolStack tool = ToolStack.from(toolStack);
      if (toolStack.getItem() instanceof ITinkerStationDisplay) {
        ITinkerStationDisplay display = (ITinkerStationDisplay) toolStack.getItem();
        this.tinkerInfo.setCaption(display.getLocalizedName());
        // TODO: tooltips on these?
        assert minecraft != null;
        this.tinkerInfo.setText(display.getStatInformation(tool, minecraft.player, new ArrayList<>(), TooltipKey.fromScreen(), TooltipFlag.DETAILED));
      }
      else {
        this.tinkerInfo.setCaption(toolStack.getDisplayName());
        this.tinkerInfo.setText();
      }

      List<ITextComponent> modifierNames = new ArrayList<>();
      List<ITextComponent> modifierInfo = new ArrayList<>();
      ITextComponent title;
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

      this.modifierInfo.setCaption(title);
      this.modifierInfo.setText(modifierNames, modifierInfo);
    }
    // tool build info
    else {
      this.tinkerInfo.setCaption(this.currentLayout.getDisplayName());
      this.tinkerInfo.setText(this.currentLayout.getDescription());

      // for each named slot, color the slot if the slot is filled
      // typically all input slots should be named, or none of them
      IFormattableTextComponent text = new StringTextComponent("");
      boolean hasComponents = false;
      for (int i = 0; i <= activeInputs; i++) {
        LayoutSlot layout = currentLayout.getSlot(i);
        String key = layout.getTranslationKey();
        if (!layout.isHidden() && !key.isEmpty()) {
          hasComponents = true;
          IFormattableTextComponent textComponent = new StringTextComponent(" * ");
          ItemStack slotStack = this.container.getSlot(i).getStack();
          if (!layout.isValid(slotStack)) {
            textComponent.mergeStyle(TextFormatting.RED);
          }
          textComponent.appendSibling(new TranslationTextComponent(key)).appendString("\n");
          text.appendSibling(textComponent);
        }
      }
      // if we found any components, set the text, use the anvil if no components
      if (hasComponents) {
        this.modifierInfo.setCaption(COMPONENTS_TEXT);
        this.modifierInfo.setText(text);
      } else {
        this.modifierInfo.setCaption(StringTextComponent.EMPTY);
        this.modifierInfo.setText(ASCII_ANVIL);
      }
    }
  }

  @Override
  protected void drawContainerName(MatrixStack matrixStack) {
    this.font.drawText(matrixStack, this.getTitle(), 8.0F, 8.0F, 4210752);
  }

  private static void renderPattern(MatrixStack matrices, Pattern pattern, int x, int y) {
    TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(pattern.getTexture());
    blit(matrices, x, y, 100, 16, 16, sprite);
  }

  public static void renderIcon(MatrixStack matrices, LayoutIcon icon, int x, int y) {
    Pattern pattern = icon.getValue(Pattern.class);
    Minecraft minecraft = Minecraft.getInstance();
    if (pattern != null) {
      // draw pattern sprite
      minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
      renderPattern(matrices, pattern, x, y);
      return;
    }

    ItemStack stack = icon.getValue(ItemStack.class);
    if (stack != null) {
      minecraft.getItemRenderer().renderItemIntoGUI(stack, x, y);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
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
    final float xOff = 10f;
    final float yOff = 22f;

    // render the background icon
    RenderSystem.translatef(xOff, yOff, 0.0F);
    RenderSystem.scalef(scale, scale, 1.0f);
    int logoX = (int) (this.cornerX / scale);
    int logoY = (int) (this.cornerY / scale);
    renderIcon(matrices, currentLayout.getIcon(), logoX, logoY);


    RenderSystem.scalef(1f / scale, 1f / scale, 1.0f);
    RenderSystem.translatef(-xOff, -yOff, 0.0f);

    // rebind gui texture since itemstack drawing sets it to something else
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(TINKER_STATION_TEXTURE);

    RenderSystem.enableBlend();
    RenderSystem.enableAlphaTest();
    RenderHelper.disableStandardItemLighting();
    RenderSystem.disableDepthTest();

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.82f);
    ITEM_COVER.draw(matrices, this.cornerX + 7, this.cornerY + 18);

    // slot backgrounds, are transparent
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.28f);
    if (!this.currentLayout.getToolSlot().isHidden()) {
      Slot slot = this.container.getSlot(TINKER_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
    }
    for (int i = 0; i < this.activeInputs; i++) {
      Slot slot = this.container.getSlot(i + INPUT_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
    }

    // slot borders, are opaque
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.container.getSlot(i);
      if ((slot instanceof TinkerStationSlot && (!((TinkerStationSlot) slot).isDormant() || slot.getHasStack()))) {
        SLOT_BORDER.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
      }
    }

    // render slot background icons
    this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.container.getSlot(i);
      if (!slot.getHasStack()) {
        Pattern icon = currentLayout.getSlot(i).getIcon();
        if (icon != null) {
          renderPattern(matrices, icon, x + this.cornerX + slot.xPos, y + this.cornerY + slot.yPos);
        }
      }
    }

    // sidebar beams
    this.minecraft.getTextureManager().bindTexture(TINKER_STATION_TEXTURE);
    x = this.buttonsScreen.guiLeft - this.leftBeam.w;
    y = this.cornerY;
    // draw the beams at the top
    x += this.leftBeam.draw(matrices, x, y);
    x += this.centerBeam.drawScaledX(matrices, x, y, this.buttonsScreen.xSize);
    this.rightBeam.draw(matrices, x, y);

    x = tinkerInfo.guiLeft - this.leftBeam.w;
    x += this.leftBeam.draw(matrices, x, y);
    x += this.centerBeam.drawScaledX(matrices, x, y, this.tinkerInfo.xSize);
    this.rightBeam.draw(matrices, x, y);

    // draw the decoration for the buttons
    for (Widget widget : this.buttonsScreen.getButtons()) {
      if(widget instanceof SlotButtonItem) {
        SlotButtonItem button = (SlotButtonItem) widget;

        this.buttonDecorationTop.draw(matrices, button.x, button.y - this.buttonDecorationTop.h);
        // don't draw the bottom for the buttons in the last row
        if (button.buttonId < this.buttonsScreen.getButtons().size() - COLUMN_COUNT) {
          this.buttonDecorationBot.draw(matrices, button.x, button.y + button.getHeight());
        }
      }
    }

    // draw the decorations for the panels
    this.panelDecorationL.draw(matrices, this.tinkerInfo.guiLeft + 5, this.tinkerInfo.guiTop - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.tinkerInfo.guiRight() - 5 - this.panelDecorationR.w, this.tinkerInfo.guiTop - this.panelDecorationR.h);
    this.panelDecorationL.draw(matrices, this.modifierInfo.guiLeft + 5, this.modifierInfo.guiTop - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.modifierInfo.guiRight() - 5 - this.panelDecorationR.w, this.modifierInfo.guiTop - this.panelDecorationR.h);

    RenderSystem.enableDepthTest();

    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.tinkerInfo.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    if (this.modifierInfo.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }
    
    if(this.buttonsScreen.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    // TODO: textField
    // this.textField.mouseClicked(mouseX, mouseY, mouseButton)
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unkowwn) {
    if (this.tinkerInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    if (this.modifierInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unkowwn);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (this.tinkerInfo.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    if (this.modifierInfo.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.tinkerInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    if (this.modifierInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
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
    if (Minecraft.IS_RUNNING_ON_MAC) {
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
  public void moveItems(MatrixStack matrixStack, Slot slotIn) {
    // don't draw dormant slots with no item
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return;
    }
    super.moveItems(matrixStack, slotIn);
  }

  @Override
  public boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return false;
    }
    return super.isSlotSelected(slotIn, mouseX, mouseY);
  }

  protected void wood() {
    this.tinkerInfo.wood();
    this.modifierInfo.wood();

    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18, 0);

    this.buttonsScreen.shiftStyle(TinkerStationButtonsScreen.WOOD_STYLE);

    this.leftBeam = LEFT_BEAM;
    this.rightBeam = RIGHT_BEAM;
    this.centerBeam = CENTER_BEAM;
  }

  protected void metal() {
    this.tinkerInfo.metal();
    this.modifierInfo.metal();

    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w * 2, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w * 2, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18 * 2, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18 * 2, 0);

    this.buttonsScreen.shiftStyle(TinkerStationButtonsScreen.METAL_STYLE);

    this.leftBeam = LEFT_BEAM.shift(0, LEFT_BEAM.h);
    this.rightBeam = RIGHT_BEAM.shift(0, RIGHT_BEAM.h);
    this.centerBeam = CENTER_BEAM.shift(0, CENTER_BEAM.h);
  }

  @Override
  public void error(ITextComponent message) {
    this.tinkerInfo.setCaption(COMPONENT_ERROR);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(StringTextComponent.EMPTY);
    this.modifierInfo.setText(StringTextComponent.EMPTY);
  }

  @Override
  public void warning(ITextComponent message) {
    this.tinkerInfo.setCaption(COMPONENT_WARNING);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(StringTextComponent.EMPTY);
    this.modifierInfo.setText(StringTextComponent.EMPTY);
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
}
