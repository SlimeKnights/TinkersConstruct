package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.LogManager;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tinkering.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.client.SlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.client.inventory.SlotButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotPosition;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerStationButtonsScreen;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationInputSlot;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationSlot;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerableSlot;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity.INPUT_SLOT;
import static slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity.TINKER_SLOT;

public class TinkerStationScreen extends BaseStationScreen<TinkerStationTileEntity, TinkerStationContainer> {
  private static final Text COMPONENTS_TEXT = Util.makeTranslation("gui", "tinker_station.components");

  private static final Text MODIFIERS_TEXT = Util.makeTranslation("gui", "tinker_station.modifiers");
  private static final Text UPGRADES_TEXT = Util.makeTranslation("gui", "tinker_station.upgrades");
  private static final Text TRAITS_TEXT = Util.makeTranslation("gui", "tinker_station.traits");
  private static final Text REPAIR_TEXT = Util.makeTranslation("gui", "tinker_station.repair");
  private static final Text ASCII_ANVIL = new LiteralText("\n\n")
    .append("       .\n")
    .append("     /( _________\n")
    .append("     |  >:=========`\n")
    .append("     )(  \n")
    .append("     \"\"")
    .formatted(Formatting.DARK_GRAY);

  private static final Identifier TINKER_STATION_TEXTURE = Util.getResource("textures/gui/tinker_station.png");

  private static final ElementScreen ACTIVE_TEXT_FIELD = new ElementScreen(0, 210, 91, 12, 256, 256);
  private static final ElementScreen ITEM_COVER = new ElementScreen(176, 18, 80, 64);
  private static final ElementScreen SLOT_BACKGROUND = new ElementScreen(176, 0, 18, 18);
  private static final ElementScreen SLOT_BORDER = new ElementScreen(194, 0, 18, 18);
  private static final ElementScreen SLOT_SPACE_TOP = new ElementScreen(0, 174 + 2, 18, 2);
  private static final ElementScreen SLOT_SPACE_BOTTOM = new ElementScreen(0, 174, 18, 2);
  private static final ElementScreen PANEL_SPACE_LEFT = new ElementScreen(0, 174, 5, 4);
  private static final ElementScreen PANEL_SPACE_RIGHT = new ElementScreen(9, 174, 9, 4);
  private static final ElementScreen LEFT_BEAM = new ElementScreen(0, 180, 2, 7);
  private static final ElementScreen RIGHT_BEAM = new ElementScreen(131, 180, 2, 7);
  private static final ScalableElementScreen CENTER_BEAM = new ScalableElementScreen(2, 180, 129, 7);

  public static final int COLUMN_COUNT = 5;

  protected ElementScreen buttonDecorationTop = SLOT_SPACE_TOP;
  protected ElementScreen buttonDecorationBot = SLOT_SPACE_BOTTOM;
  protected ElementScreen panelDecorationL = PANEL_SPACE_LEFT;
  protected ElementScreen panelDecorationR = PANEL_SPACE_RIGHT;

  protected ElementScreen leftBeam = new ElementScreen(0, 0, 0, 0);
  protected ElementScreen rightBeam = new ElementScreen(0, 0, 0, 0);
  protected ScalableElementScreen centerBeam = new ScalableElementScreen(0, 0, 0, 0);

  //public TextFieldWidget textField;
  protected InfoPanelScreen tinkerInfo;
  protected InfoPanelScreen modifierInfo;

  protected TinkerStationButtonsScreen buttonsScreen;
  /** Maximum available slots */
  @Getter
  private final int maxInputs;
  /** How many of the available slots are active */
  protected int activeSlots;
  /** Currently selected tool */
  private SlotInformation currentData;

  public TinkerStationScreen(TinkerStationContainer container, PlayerInventory playerInventory, Text title) {
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
    this.modifierInfo.yOffset = this.tinkerInfo.backgroundHeight + 9;

    this.backgroundHeight = 174;

    // determine number of inputs
    int max = 5;
    if (container.getTile() != null) {
      max = container.getTile().getInputCount();
    }
    this.maxInputs = max;

    // large if at least 4
    if (max > 3) {
      this.metal();
    } else {
      this.wood();
    }
    // apply base slot information
    SlotInformation slotInformation = SlotInformationLoader.get(Util.getResource("repair_" + max));
    this.currentData = slotInformation;
    this.activeSlots = Math.min(slotInformation.getPoints().size(), max);

    this.passEvents = false;
  }

  @Override
  public void init() {
    super.init();

    assert this.client != null;
    this.client.keyboard.setRepeatEvents(true);

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.y += 4;
    this.cornerY += 4;

    //this.textField = new TextFieldWidget(this.textRenderer, this.cornerX + 81, this.cornerY + 7, 91, 12, LiteralText.EMPTY);
    //this.textField.setDrawsBackground(false);
    //this.textField.setMaxLength(40);

    this.buttonsScreen.xOffset = -2;
    this.buttonsScreen.yOffset = this.centerBeam.h + this.buttonDecorationTop.h;
    this.tinkerInfo.xOffset = 2;
    this.tinkerInfo.yOffset = this.centerBeam.h + this.panelDecorationL.h;
    this.modifierInfo.xOffset = this.tinkerInfo.xOffset;
    this.modifierInfo.yOffset = this.tinkerInfo.yOffset + this.tinkerInfo.backgroundHeight + 4;

    for (ModuleScreen<?,?> module : this.modules) {
      module.y += 4;
    }

    this.updateScreen();
  }

  @Override
  public void removed() {
    super.removed();

    assert this.client != null;
    this.client.keyboard.setRepeatEvents(false);
  }

  public void updateScreen() {
    int i;

    // remaining slots
    int stillFilled = 0;
    Slot slot = this.handler.getSlot(TINKER_SLOT);
    SlotPosition toolPos = currentData.getToolSlot();
    if (toolPos.isHidden()) {
      // update position for other slots
      slot.x = 87;
      slot.y = 62;
      stillFilled++;
      if (slot instanceof TinkerStationSlot) {
        ((TinkerableSlot) slot).deactivate();
      }
    } else {
      slot.x = toolPos.getX();
      slot.y = toolPos.getY();
      if (slot instanceof TinkerableSlot) {
        ((TinkerableSlot) slot).activate();
      }
    }

    // visible slots
    for (i = 0; i < this.activeSlots; i++) {
      slot = this.handler.getSlot(i + INPUT_SLOT);
      SlotPosition point = this.currentData.getPoints().get(i);

      slot.x = point.getX();
      slot.y = point.getY();

      if (slot instanceof TinkerStationInputSlot) {
        ((TinkerStationInputSlot) slot).activate();
      }
    }

    // hidden slots
    for (; i < maxInputs; i++) {
      Slot currentSlot = this.handler.getSlot(i + INPUT_SLOT);
      if (currentSlot instanceof TinkerStationInputSlot) {
        ((TinkerStationInputSlot) currentSlot).deactivate();

        currentSlot.x = 87 + 20 * stillFilled;
        currentSlot.y = 62;
        stillFilled++;
      }
    }

    this.updateDisplay();
  }

  @Override
  public void updateDisplay() {
    ItemStack toolStack = this.handler.getResult();

    if (toolStack.isEmpty()) {
      toolStack = this.handler.getSlot(TINKER_SLOT).getStack();
    }

    if (TinkerTags.Items.MODIFIABLE.contains(toolStack.getItem())) {
      if (toolStack.getItem() instanceof ITinkerStationDisplay) {
        ITinkerStationDisplay tool = (ITinkerStationDisplay) toolStack.getItem();
        this.tinkerInfo.setCaption(tool.getLocalizedName());
        // TODO: tooltips
        this.tinkerInfo.setText(tool.getInformation(toolStack));
      }
      else {
        this.tinkerInfo.setCaption(toolStack.getName());
        this.tinkerInfo.setText();
      }

      // TODO: generalize to all modifiable tools
      ToolStack tool = ToolStack.from(toolStack);
      List<Text> modifiers = new ArrayList<>();
      List<Text> modifierInfo = new ArrayList<>();
      for (ModifierEntry entry : tool.getModifierList()) {
        Modifier mod = entry.getModifier();
        if (mod.shouldDisplay(true)) {
          modifiers.add(mod.getDisplayName(tool, entry.getLevel()));
          modifierInfo.add(mod.getDescription());
        }
      }

      this.modifierInfo.setCaption(MODIFIERS_TEXT);
      this.modifierInfo.setText(modifiers, modifierInfo);
    }
    // Repair info
    else if (this.currentData.isRepair()) {
      this.tinkerInfo.setCaption(REPAIR_TEXT);
      this.tinkerInfo.setText();

      this.modifierInfo.setCaption(LiteralText.EMPTY);
      this.modifierInfo.setText(ASCII_ANVIL);
    }
    // tool build info
    // TODO: not all tinkerable is tool core, switch to IModifyable?
    else {
      ToolCore tool = (ToolCore) this.currentData.getItem();
      this.tinkerInfo.setCaption(new TranslatableText(tool.getTranslationKey()));
      this.tinkerInfo.setText(new TranslatableText(tool.getTranslationKey() + ".description"));

      MutableText text = new LiteralText("");
      List<IToolPart> materialRequirements = tool.getToolDefinition().getRequiredComponents();
      for (int i = 0; i < materialRequirements.size(); i++) {
        IToolPart requirement = materialRequirements.get(i);
        MutableText textComponent = new LiteralText(" * ");

        ItemStack slotStack = this.handler.getSlot(i + INPUT_SLOT).getStack();
        if (requirement.asItem() != slotStack.getItem()) {
          textComponent.formatted(Formatting.RED);
        }
        textComponent.append(new TranslatableText(requirement.asItem().getTranslationKey())).append("\n");

        text.append(textComponent);
      }

      this.modifierInfo.setCaption(COMPONENTS_TEXT);
      this.modifierInfo.setText(text);
    }
  }

  @Override
  protected void drawContainerName(MatrixStack matrixStack) {
    this.textRenderer.draw(matrixStack, this.getTitle(), 8.0F, 8.0F, 4210752);
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
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

    RenderSystem.translatef(xOff, yOff, 0.0F);
    RenderSystem.scalef(scale, scale, 1.0f);

    int logoX = (int) (this.cornerX / scale);
    int logoY = (int) (this.cornerY / scale);

    if (this.currentData != null) {
      if (this.currentData.isRepair()) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(Icons.ICONS);
        Icons.ANVIL.draw(matrices, logoX, logoY);
      }
      else {
        this.itemRenderer.renderGuiItemIcon(this.currentData.getToolForRendering(), logoX, logoY);
      }
    }

    RenderSystem.scalef(1f / scale, 1f / scale, 1.0f);
    RenderSystem.translatef(-xOff, -yOff, 0.0f);

    // rebind gui texture since itemstack drawing sets it to something else
    assert this.client != null;
    this.client.getTextureManager().bindTexture(TINKER_STATION_TEXTURE);

    RenderSystem.enableBlend();
    RenderSystem.enableAlphaTest();
    DiffuseLighting.disable();
    RenderSystem.disableDepthTest();

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.82f);
    ITEM_COVER.draw(matrices, this.cornerX + 7, this.cornerY + 18);

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.28f);

    for (int i = 0; i < this.activeSlots; i++) {
      Slot slot = this.handler.getSlot(i + INPUT_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
    }

    if (!this.currentData.getToolSlot().isHidden()) {
      Slot slot = this.handler.getSlot(TINKER_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
    }

    // full opaque. Draw the borders of the slots
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

    for (int i = 0; i <= maxInputs; i++) {
      Slot slot = this.handler.getSlot(i);
      if ((slot instanceof TinkerStationSlot && (!((TinkerStationSlot) slot).isDormant() || slot.hasStack()))) {
        SLOT_BORDER.draw(matrices, x + this.cornerX + slot.x - 1, y + this.cornerY + slot.y - 1);
      }
    }

    this.client.getTextureManager().bindTexture(Icons.ICONS);

    if (this.currentData.isRepair()) {
      this.drawRepairSlotIcons(matrices);
    }
    else if (this.currentData.getItem() instanceof ToolCore) {
      for (int i = 0; i < this.activeSlots; i++) {
        Slot slot = this.handler.getSlot(i + INPUT_SLOT);
        if (slot.hasStack() || !(slot instanceof TinkerStationInputSlot)) {
          continue;
        }

        Identifier icon = ((TinkerStationInputSlot) slot).getIcon();
        if (icon != null) {
          this.client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
          Function<Identifier, Sprite> spriteGetter = this.client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
          Sprite sprite = spriteGetter.apply(new Identifier(icon.getNamespace(), "gui/tinker_pattern/" + icon.getPath()));
          drawSprite(matrices, x + this.cornerX + slot.x, y + this.cornerY + slot.y, 100, 16, 16, sprite);
        }
      }
    }

    this.client.getTextureManager().bindTexture(TINKER_STATION_TEXTURE);
    x = this.buttonsScreen.x - this.leftBeam.w;
    y = this.cornerY;
    // draw the beams at the top
    x += this.leftBeam.draw(matrices, x, y);
    x += this.centerBeam.drawScaledX(matrices, x, y, this.buttonsScreen.backgroundWidth);
    this.rightBeam.draw(matrices, x, y);

    x = tinkerInfo.x - this.leftBeam.w;
    x += this.leftBeam.draw(matrices, x, y);
    x += this.centerBeam.drawScaledX(matrices, x, y, this.tinkerInfo.backgroundWidth);
    this.rightBeam.draw(matrices, x, y);
    // draw the decoration for the buttons
    for (AbstractButtonWidget widget : this.buttonsScreen.getButtons()) {
      if(widget instanceof SlotButtonItem) {
        SlotButtonItem button = (SlotButtonItem) widget;

        this.buttonDecorationTop.draw(matrices, button.x, button.y - this.buttonDecorationTop.h);
        // don't draw the bottom for the buttons in the last row
        if (button.buttonId < this.buttonsScreen.getButtons().size() - COLUMN_COUNT) {
          // TODO: getHeightRealms()->getHeight()
          this.buttonDecorationBot.draw(matrices, button.x, button.y + button.getHeight());
        }
      }
    }

    // draw the decorations for the panels
    this.panelDecorationL.draw(matrices, this.tinkerInfo.x + 5, this.tinkerInfo.y - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.tinkerInfo.guiRight() - 5 - this.panelDecorationR.w, this.tinkerInfo.y - this.panelDecorationR.h);
    this.panelDecorationL.draw(matrices, this.modifierInfo.x + 5, this.modifierInfo.y - this.panelDecorationL.h);
    this.panelDecorationR.draw(matrices, this.modifierInfo.guiRight() - 5 - this.panelDecorationR.w, this.modifierInfo.y - this.panelDecorationR.h);

    RenderSystem.enableDepthTest();

    super.drawBackground(matrices, partialTicks, mouseX, mouseY);
  }

  /** Draws the repair icons for all slots */
  protected void drawRepairSlotIcons(MatrixStack matrixStack) {
    for (int i = 0; i < this.activeSlots; i++) {
      this.drawRepairSlotIcon(matrixStack, i + INPUT_SLOT);
    }

    if (!this.currentData.getToolSlot().isHidden()) {
      this.drawRepairSlotIcon(matrixStack, TINKER_SLOT);
    }
  }

  /** Draws the repair icon for the given slot */
  protected void drawRepairSlotIcon(MatrixStack matrixStack, int i) {
    ElementScreen icon = null;
    Slot slot = this.handler.getSlot(i);

    // only empty slots get the logo since something else than the displayed thing might be in there.
    // which would look weird.
    if (slot.hasStack()) {
      return;
    }

    switch (i) {
      case 0:
        icon = Icons.PICKAXE;
        break;
      case 1:
        icon = Icons.QUARTZ;
        break;
      case 2:
        icon = Icons.DUST;
        break;
      case 3:
        icon = Icons.LAPIS;
        break;
      case 4:
        icon = Icons.INGOT;
        break;
      case 5:
        icon = Icons.GEM;
        break;
    }

    if (icon != null) {
      this.drawIconEmpty(matrixStack, slot, icon);
    }
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

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == 256) {
      assert this.client != null;
      assert this.client.player != null;
      this.client.player.updateSubmergedInWaterState();
    }

    // TODO: textField
    //boolean keyPressed = this.textField.keyPressed(keyCode, scanCode, modifiers);
    //if (keyPressed) {
      //TinkerNetwork.getInstance().sendToServer(new ToolStationTextPacket(this.textField.getText()));
      //this.container.setToolName(textField.getText());
    //}
    // keyPressed || this.textField.isActive() ||
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  /* TODO: textField
  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    if (!this.textField.isFocused()) {
      return super.charTyped(typedChar, keyCode);
    }
    else {
      if (keyCode == 1) {
        assert this.client != null;
        assert this.client.player != null;
        this.client.player.updateSubmergedInWaterState();
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
      this.textField.write(text);
    }
  }

  @Override
  public void tick() {
    super.tick();

    this.textField.tick();
  }
  */

/*  @Override
  public void drawSlot(MatrixStack matrixStack, Slot slotIn) {
    // don't draw dormant slots with no item
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.hasStack()) {
      return;
    }
    super.drawSlot(matrixStack, slotIn);
  }

  @Override
  public boolean isPointOverSlot(Slot slotIn, double mouseX, double mouseY) {
    if (slotIn instanceof TinkerStationSlot && ((TinkerStationSlot) slotIn).isDormant() && !slotIn.hasStack()) {
      return false;
    }
    return super.isPointOverSlot(slotIn, mouseX, mouseY);
  }*/

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
  public void error(Text message) {
    this.tinkerInfo.setCaption(COMPONENT_ERROR);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(LiteralText.EMPTY);
    this.modifierInfo.setText(LiteralText.EMPTY);
  }

  @Override
  public void warning(Text message) {
    this.tinkerInfo.setCaption(COMPONENT_WARNING);
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(LiteralText.EMPTY);
    this.modifierInfo.setText(LiteralText.EMPTY);
  }

  /**
   * Called when a tool button is pressed
   * @param data  Info from the pressed button
   */
  public void onToolSelection(SlotInformation data) {
    this.activeSlots = Math.min(data.getPoints().size(), maxInputs);
    this.currentData = data;

    List<IToolPart> requiredComponents = null;
    if (!currentData.isRepair() && currentData.getItem() instanceof ToolCore) {
      requiredComponents = ((ToolCore)currentData.getItem()).getToolDefinition().getRequiredComponents();
    }

    Slot slot = this.handler.getSlot(TINKER_SLOT);
    if (slot instanceof TinkerableSlot) {
      TinkerableSlot tinkerableSlot = (TinkerableSlot) slot;
      if (data.getToolSlot().isHidden()) {
        tinkerableSlot.deactivate();
      }
      else {
        tinkerableSlot.activate();
      }
    }

    for (int i = 0; i < maxInputs; i++) {
      // set part icons for the slots
      slot = this.handler.getSlot(i + INPUT_SLOT);
      if (slot instanceof TinkerStationInputSlot) {
        TinkerStationInputSlot toolPartSlot = (TinkerStationInputSlot) slot;
        toolPartSlot.setIcon(null);
        if (i >= activeSlots) {
          toolPartSlot.deactivate();
        }
        else {
          toolPartSlot.activate();
          if (requiredComponents != null && i < requiredComponents.size()) {
            toolPartSlot.setIcon(Registry.ITEM.getId(requiredComponents.get(i).asItem()));
          }
        }
      }
    }

    this.handler.setToolSelection(activeSlots, data.getToolSlot().isHidden());
    TinkerNetwork.getInstance().sendToServer(new TinkerStationSelectionPacket(activeSlots, data.getToolSlot().isHidden()));

    this.updateScreen();
  }
}
