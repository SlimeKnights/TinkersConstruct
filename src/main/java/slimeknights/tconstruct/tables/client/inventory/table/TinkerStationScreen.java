package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tinkering.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tables.client.SlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.client.inventory.SlotButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotPosition;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.client.inventory.module.TinkerStationButtonsScreen;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerSlot;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationInSlot;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import java.util.List;
import java.util.function.Function;

public class TinkerStationScreen extends BaseStationScreen<TinkerStationTileEntity, TinkerStationContainer> {

  private static final ResourceLocation TINKER_STATION_TEXTURE = Util.getResource("textures/gui/tinker_station.png");

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

  public static final ResourceLocation REPAIR_NAME = Util.getResource("repair");

  public static final int COLUMN_COUNT = 5;
  private static final int TABLE_SLOT_COUNT = 6;

  protected ElementScreen buttonDecorationTop = SLOT_SPACE_TOP;
  protected ElementScreen buttonDecorationBot = SLOT_SPACE_BOTTOM;
  protected ElementScreen panelDecorationL = PANEL_SPACE_LEFT;
  protected ElementScreen panelDecorationR = PANEL_SPACE_RIGHT;

  protected ElementScreen leftBeam = new ElementScreen(0, 0, 0, 0);
  protected ElementScreen rightBeam = new ElementScreen(0, 0, 0, 0);
  protected ScalableElementScreen centerBeam = new ScalableElementScreen(0, 0, 0, 0);

  public TextFieldWidget textField;
  protected InfoPanelScreen tinkerInfo;
  protected InfoPanelScreen modifierInfo;

  protected TinkerStationButtonsScreen buttonsScreen;
  protected int activeSlots; // how many of the available slots are active
  public SlotInformation currentData;

  public TinkerStationScreen(TinkerStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.buttonsScreen = new TinkerStationButtonsScreen(this, container, playerInventory, title);
    this.addModule(this.buttonsScreen);

    this.tinkerInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.addModule(this.tinkerInfo);

    this.modifierInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.addModule(this.modifierInfo);

    this.tinkerInfo.yOffset = 5;
    this.modifierInfo.yOffset = this.tinkerInfo.ySize + 9;

    this.ySize = 174;

    this.wood();

    SlotInformation slotInformation = SlotInformationLoader.get(TinkerStationScreen.REPAIR_NAME);

    this.activeSlots = Math.min(slotInformation.getPoints().size(), TABLE_SLOT_COUNT);
    this.currentData = slotInformation;
  }

  @Override
  public void init() {
    super.init();

    assert this.minecraft != null;
    this.minecraft.keyboardListener.enableRepeatEvents(true);

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.guiTop += 4;
    this.cornerY += 4;

    this.textField = new TextFieldWidget(this.font, this.cornerX + 81, this.cornerY + 7, 91, 12, StringTextComponent.EMPTY);
    this.textField.setEnableBackgroundDrawing(false);
    this.textField.setMaxStringLength(40);

    this.buttonsScreen.xOffset = -2;
    this.buttonsScreen.yOffset = this.centerBeam.h + this.buttonDecorationTop.h;
    this.tinkerInfo.xOffset = 2;
    this.tinkerInfo.yOffset = this.centerBeam.h + this.panelDecorationL.h;
    this.modifierInfo.xOffset = this.tinkerInfo.xOffset;
    this.modifierInfo.yOffset = this.tinkerInfo.yOffset + this.tinkerInfo.ySize + 4;

    for (ModuleScreen module : this.modules) {
      module.guiTop += 4;
    }

    this.updateScreen();
  }

  @Override
  public void onClose() {
    super.onClose();

    assert this.minecraft != null;
    this.minecraft.keyboardListener.enableRepeatEvents(false);
  }

  public void updateScreen() {
    int i;

    for (i = 0; i < this.activeSlots; i++) {
      Slot slot = this.container.getSlot(i);
      SlotPosition point = this.currentData.getPoints().get(i);

      slot.xPos = point.getX();
      slot.yPos = point.getY();

      if (slot instanceof TinkerStationInSlot) {
        ((TinkerStationInSlot) slot).activate();
      }
    }

    if (!this.currentData.getToolSlot().isHidden()) {
      Slot slot = this.container.getSlot(TinkerStationTileEntity.TINKER_SLOT);
      SlotPosition position = this.currentData.getToolSlot();

      slot.xPos = position.getX();
      slot.yPos = position.getY();

      if (slot instanceof TinkerSlot) {
        ((TinkerSlot) slot).activate();
      }
    }

    // remaining slots
    int stillFilled = 0;

    for (; i < TABLE_SLOT_COUNT; i++) {
      Slot currentSlot = this.container.getSlot(i);

      if (currentSlot instanceof TinkerStationInSlot) {
        ((TinkerStationInSlot) currentSlot).deactivate();

        currentSlot.xPos = 87 + 20 * stillFilled;
        currentSlot.yPos = 62;
        stillFilled++;
      }

      if (currentSlot instanceof TinkerSlot && this.currentData.getToolSlot().isHidden()) {
        ((TinkerSlot) currentSlot).deactivate();

        currentSlot.xPos = 87 + 20 * stillFilled;
        currentSlot.yPos = 62;
        stillFilled++;
      }
    }

    this.updateDisplay();
  }

  @Override
  public void updateDisplay() {
    ItemStack toolStack = this.container.getResult();

    if (toolStack.isEmpty()) {
      toolStack = this.container.getSlot(TinkerStationTileEntity.TINKER_SLOT).getStack();
    }

    if (toolStack.getItem() instanceof IModifiable) {
      if (toolStack.getItem() instanceof ITinkerStationDisplay) {
        ITinkerStationDisplay tool = (ITinkerStationDisplay) toolStack.getItem();
        this.tinkerInfo.setCaption(tool.getLocalizedName());
        this.tinkerInfo.setText(tool.getInformation(toolStack));
      }
      else {
        this.tinkerInfo.setCaption(toolStack.getDisplayName());
        this.tinkerInfo.setText();
      }

      this.modifierInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tinker_station.modifiers"));
      this.modifierInfo.setText(((IModifiable)toolStack.getItem()).getTraits(toolStack));

    }
    // Repair info
    else if (this.currentData.getItemStack().isEmpty()) {
      this.tinkerInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tinker_station.repair"));
      this.tinkerInfo.setText();

      this.modifierInfo.setCaption(StringTextComponent.EMPTY);

      IFormattableTextComponent textComponent = new StringTextComponent("\n\n")
        .appendString("       .\n")
        .appendString("     /( _________\n")
        .appendString("     |  >:=========`\n")
        .appendString("     )(  \n")
        .appendString("     \"\"")
        .mergeStyle(TextFormatting.DARK_GRAY);

      this.modifierInfo.setText(textComponent);
    }
    // tool build info
    // TODO: not all tinkerable is tool core, switch to IModifyable?
    else {
      ToolCore tool = (ToolCore) this.currentData.getItemStack().getItem();
      this.tinkerInfo.setCaption(new TranslationTextComponent(tool.getTranslationKey()));
      this.tinkerInfo.setText(new TranslationTextComponent(tool.getTranslationKey() + ".description"));

      IFormattableTextComponent text = new StringTextComponent("");
      List<IToolPart> materialRequirements = tool.getToolDefinition().getRequiredComponents();
      for (int i = 0; i < materialRequirements.size(); i++) {
        IToolPart requirement = materialRequirements.get(i);
        IFormattableTextComponent textComponent = new StringTextComponent(" * ");

        ItemStack slotStack = this.container.getSlot(i).getStack();
        if (requirement.asItem() != slotStack.getItem()) {
          textComponent.mergeStyle(TextFormatting.RED);
        }
        textComponent.append(new TranslationTextComponent(requirement.asItem().getTranslationKey())).appendString("\n");

        text.append(textComponent);
      }

      this.modifierInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tinker_station.components"));

      this.modifierInfo.setText(text);
    }
  }

  @Override
  protected void drawContainerName(MatrixStack matrixStack) {
    this.font.func_243248_b(matrixStack, this.getTitle(), 8.0F, 8.0F, 4210752);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, TINKER_STATION_TEXTURE);

    // looks like there's a weird case where this is called before init? Not reproducible but meh.
    if (this.textField != null) {
      if (this.textField.isFocused()) {
        ACTIVE_TEXT_FIELD.draw(matrices, cornerX + 79, cornerY + 6);
      }

      // draw textField
      this.textField.render(matrices, mouseX, mouseY, partialTicks);
    }

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
      if (!this.currentData.getItemStack().isEmpty()) {
        this.itemRenderer.renderItemIntoGUI(this.currentData.getToolForRendering(), logoX, logoY);
      }
      else if (this.currentData == SlotInformationLoader.get(TinkerStationScreen.REPAIR_NAME)) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(Icons.ICONS);
        Icons.ANVIL.draw(matrices, logoX, logoY);
      }
    }

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

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.28f);

    for (int i = 0; i < this.activeSlots; i++) {
      Slot slot = this.container.getSlot(i);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
    }

    if (!this.currentData.getToolSlot().isHidden()) {
      Slot slot = this.container.getSlot(TinkerStationTileEntity.TINKER_SLOT);
      SLOT_BACKGROUND.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
    }

    // full opaque. Draw the borders of the slots
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

    for (int i = 0; i < TABLE_SLOT_COUNT; i++) {
      Slot slot = this.container.getSlot(i);

      if (slot instanceof TinkerStationInSlot && (!((TinkerStationInSlot) slot).isDormant() || slot.getHasStack())) {
        SLOT_BORDER.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
      }
      else if (slot instanceof TinkerSlot && (!((TinkerSlot) slot).isDormant() || slot.getHasStack())) {
        SLOT_BORDER.draw(matrices, x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
      }
    }

    this.minecraft.getTextureManager().bindTexture(Icons.ICONS);

    if (this.currentData == SlotInformationLoader.get(TinkerStationScreen.REPAIR_NAME)) {
      this.drawRepairSlotIcons(matrices);
    }
    else if (this.currentData.getItemStack().getItem() instanceof ToolCore) {
      for (int i = 0; i < this.activeSlots; i++) {
        Slot slot = this.container.getSlot(i);
        if (slot.getHasStack() || !(slot instanceof TinkerStationInSlot)) {
          continue;
        }

        ResourceLocation icon = ((TinkerStationInSlot) slot).getIcon();
        if (icon != null) {
          this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
          Function<ResourceLocation, TextureAtlasSprite> spriteGetter = this.minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
          TextureAtlasSprite sprite = spriteGetter.apply(new ResourceLocation(icon.getNamespace(), "gui/tinker_pattern/" + icon.getPath()));
          blit(matrices, x + this.cornerX + slot.xPos, y + this.cornerY + slot.yPos, 100, 16, 16, sprite);
        }
      }
    }

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
          // TODO: getHeightRealms()->getHeight()
          this.buttonDecorationBot.draw(matrices, button.x, button.y + button.getHeightRealms());
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

  protected void drawRepairSlotIcons(MatrixStack matrixStack) {
    for (int i = 0; i < this.activeSlots; i++) {
      this.drawRepairSlotIcon(matrixStack, i);
    }

    if (!this.currentData.getToolSlot().isHidden()) {
      this.drawRepairSlotIcon(matrixStack, TinkerStationTileEntity.TINKER_SLOT);
    }
  }

  protected void drawRepairSlotIcon(MatrixStack matrixStack, int i) {
    ElementScreen icon = null;
    Slot slot = this.container.getSlot(i);

    // only empty slots get the logo since something else than the displayed thing might be in there.
    // which would look weird.
    if (slot.getHasStack()) {
      return;
    }

    if (i == 0) {
      icon = Icons.QUARTZ;
    }
    else if (i == 1) {
      icon = Icons.DUST;
    }
    else if (i == 2) {
      icon = Icons.LAPIS;
    }
    else if (i == 3) {
      icon = Icons.INGOT;
    }
    else if (i == 4) {
      icon = Icons.GEM;
    }
    else if (i == 5) {
      icon = Icons.PICKAXE;
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

    return this.textField.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
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

    return super.mouseReleased(mouseX, mouseY, state);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == 256) {
      assert this.minecraft != null;
      assert this.minecraft.player != null;
      this.minecraft.player.closeScreen();
    }

    boolean keyPressed = this.textField.keyPressed(keyCode, scanCode, modifiers);

    if (keyPressed) {
      //TinkerNetwork.getInstance().sendToServer(new ToolStationTextPacket(this.textField.getText()));
      //this.container.setToolName(textField.getText());
    }

    return keyPressed || this.textField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
  }

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
    }
    else {
      this.textField.writeText(text);
    }
  }

  @Override
  public void tick() {
    super.tick();

    this.textField.tick();
  }

  @Override
  public void moveItems(MatrixStack matrixStack, Slot slotIn) {
    // don't draw dormant slots with no item
    if (slotIn instanceof TinkerStationInSlot && ((TinkerStationInSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return;
    }

    if (slotIn instanceof TinkerSlot && ((TinkerSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return;
    }

    super.moveItems(matrixStack, slotIn);
  }

  @Override
  public boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
    if (slotIn instanceof TinkerStationInSlot && ((TinkerStationInSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return false;
    }

    if (slotIn instanceof TinkerSlot && ((TinkerSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
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
    this.tinkerInfo.setCaption(new TranslationTextComponent("gui.tconstruct.error"));
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(StringTextComponent.EMPTY);
    this.modifierInfo.setText(StringTextComponent.EMPTY);
  }

  @Override
  public void warning(ITextComponent message) {
    this.tinkerInfo.setCaption(new TranslationTextComponent("gui.tconstruct.warning"));
    this.tinkerInfo.setText(message);
    this.modifierInfo.setCaption(StringTextComponent.EMPTY);
    this.modifierInfo.setText(StringTextComponent.EMPTY);
  }

  /**
   * Called when a tool button is pressed
   * @param data  Info from the pressed button
   */
  public void onToolSelection(SlotInformation data) {
    this.activeSlots = Math.min(data.getPoints().size(), TABLE_SLOT_COUNT);
    this.currentData = data;

    ItemStack tool = ItemStack.EMPTY;

    if (data.getItemStack().getItem() instanceof ToolCore) {
      tool = data.getItemStack();
    }

    List<IToolPart> requiredComponents = null;
    if (tool != ItemStack.EMPTY && tool.getItem() instanceof ToolCore) {
      requiredComponents = ((ToolCore)tool.getItem()).getToolDefinition().getRequiredComponents();
    }

    for (int i = 0; i < this.tile.getSizeInventory(); i++) {
      Slot slot = this.container.getSlot(i);
      // set part icons for the slots
      if (slot instanceof TinkerStationInSlot) {
        TinkerStationInSlot toolPartSlot = (TinkerStationInSlot) slot;

        toolPartSlot.setIcon(null);
        if (i >= activeSlots) {
          toolPartSlot.deactivate();
        }
        else {
          toolPartSlot.activate();
          if (requiredComponents != null && i < requiredComponents.size()) {
            toolPartSlot.setIcon(requiredComponents.get(i).asItem().getRegistryName());
          }
        }
      }

      if (slot instanceof TinkerSlot) {
        TinkerSlot tinkerSlot = (TinkerSlot) slot;

        if (data.getToolSlot().isHidden()) {
          tinkerSlot.deactivate();
        }
        else {
          tinkerSlot.activate();
        }
      }
    }

    this.container.setToolSelection(activeSlots, data.getToolSlot().isHidden());
    TinkerNetwork.getInstance().sendToServer(new TinkerStationSelectionPacket(activeSlots, data.getToolSlot().isHidden()));

    this.updateScreen();
  }
}
