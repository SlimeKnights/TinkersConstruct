package slimeknights.tconstruct.tables.client.inventory.table;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tinkering.IToolStationDisplay;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tables.client.ToolSlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotPosition;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.client.inventory.module.ToolStationButtonsScreen;
import slimeknights.tconstruct.tables.inventory.table.toolstation.ToolStationContainer;
import slimeknights.tconstruct.tables.inventory.table.toolstation.ToolStationInSlot;
import slimeknights.tconstruct.tables.network.ToolStationSelectionPacket;
import slimeknights.tconstruct.tables.network.ToolStationTextPacket;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;
import slimeknights.tconstruct.tools.ToolRegistry;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ToolStationScreen extends TinkerStationScreen<ToolStationTileEntity, ToolStationContainer> {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/toolstation.png");

  private static final ElementScreen ACTIVE_TEXT_FIELD = new ElementScreen(0, 210, 102, 12, 256, 256);
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
  private static final int TABLE_SLOT_COUNT = 6;

  protected ElementScreen buttonDecorationTop = SLOT_SPACE_TOP;
  protected ElementScreen buttonDecorationBot = SLOT_SPACE_BOTTOM;
  protected ElementScreen panelDecorationL = PANEL_SPACE_LEFT;
  protected ElementScreen panelDecorationR = PANEL_SPACE_RIGHT;

  protected ElementScreen leftBeam = new ElementScreen(0, 0, 0, 0);
  protected ElementScreen rightBeam = new ElementScreen(0, 0, 0, 0);
  protected ScalableElementScreen centerBeam = new ScalableElementScreen(0, 0, 0, 0);

  protected ToolStationButtonsScreen toolStationButtonsScreen;

  protected int activeSlots; // how many of the available slots are active

  public TextFieldWidget textField;

  protected InfoPanelScreen toolInfo;
  protected InfoPanelScreen traitInfo;

  public SlotInformation currentData = ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME);

  public ToolStationScreen(ToolStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.toolStationButtonsScreen = new ToolStationButtonsScreen(this, container, playerInventory, title);
    this.addModule(this.toolStationButtonsScreen);

    this.toolInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.addModule(this.toolInfo);

    this.traitInfo = new InfoPanelScreen(this, container, playerInventory, title);
    this.addModule(this.traitInfo);

    this.toolInfo.yOffset = 5;
    this.traitInfo.yOffset = this.toolInfo.ySize + 9;

    this.ySize = 174;

    this.wood();
  }

  @Override
  public void init() {
    super.init();
    this.minecraft.keyboardListener.enableRepeatEvents(true);

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.guiTop += 4;
    this.cornerY += 4;

    this.textField = new TextFieldWidget(this.font, this.cornerX + 70, this.cornerY + 7, 92, 12, "");
    this.textField.setEnableBackgroundDrawing(false);
    this.textField.setMaxStringLength(40);

    this.toolStationButtonsScreen.xOffset = -2;
    this.toolStationButtonsScreen.yOffset = this.centerBeam.h + this.buttonDecorationTop.h;
    this.toolInfo.xOffset = 2;
    this.toolInfo.yOffset = this.centerBeam.h + this.panelDecorationL.h;
    this.traitInfo.xOffset = this.toolInfo.xOffset;
    this.traitInfo.yOffset = this.toolInfo.yOffset + this.toolInfo.ySize + 4;

    for (ModuleScreen module : this.modules) {
      module.guiTop += 4;
    }

    this.updateGUI();
  }

  @Override
  public void onClose() {
    super.onClose();
    this.minecraft.keyboardListener.enableRepeatEvents(false);
  }

  public void updateGUI() {
    int i;

    for (i = 0; i < this.activeSlots; i++) {
      SlotPosition point = this.currentData.getPoints().get(i);

      Slot slot = this.container.getSlot(i);
      slot.xPos = point.getX();
      slot.yPos = point.getY();

      if(slot instanceof ToolStationInSlot) {
        ((ToolStationInSlot) slot).activate();
      }
    }

    // remaining slots
    int stillFilled = 0;
    for (; i < TABLE_SLOT_COUNT; i++) {
      Slot slot = this.container.getSlot(i);

      if(slot instanceof ToolStationInSlot) {
        ((ToolStationInSlot) slot).deactivate();
      }

      if (slot.getHasStack()) {
        slot.xPos = 87 + 20 * stillFilled;
        slot.yPos = 62;
        stillFilled++;
      }
      else {
        slot.xPos = 0;
        slot.yPos = 0;
      }
    }

    this.updateDisplay();
  }

  @Override
  public void updateDisplay() {
    ItemStack toolStack = this.container.getResult();

    if (toolStack.isEmpty()) {
      toolStack = this.container.getSlot(0).getStack();
    }

    if (toolStack.getItem() instanceof IModifiable) {
      if (toolStack.getItem() instanceof IToolStationDisplay) {
        IToolStationDisplay tool = (IToolStationDisplay) toolStack.getItem();
        this.toolInfo.setCaption(tool.getLocalizedName().getFormattedText());
        this.toolInfo.setText(tool.getInformation(toolStack));
      } else {
        this.toolInfo.setCaption(toolStack.getDisplayName().getFormattedText());
        this.toolInfo.setText();
      }

      this.traitInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tool_station.traits").getFormattedText());
      this.traitInfo.setText("dyio where my trains!");
    }
    // Repair info
    else if (this.currentData.getItemStack().isEmpty()) {
      this.toolInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tool_station.repair").getFormattedText());
      this.toolInfo.setText();

      this.traitInfo.setCaption("");

      String c = TextFormatting.DARK_GRAY.toString();
      String[] art = new String[]{
        c + "",
        c + "",
        c + "       .",
        c + "     /( _________",
        c + "     |  >:=========`",
        c + "     )(  ",
        c + "     \"\""
      };
      this.traitInfo.setText(art);
    }
    // tool build info
    else {
      ToolCore tool = (ToolCore) this.currentData.getItemStack().getItem();
      this.toolInfo.setCaption(new TranslationTextComponent(tool.getTranslationKey()).getFormattedText());
      this.toolInfo.setText(new TranslationTextComponent(tool.getTranslationKey() + ".description").getFormattedText());

      List<String> text = Lists.newLinkedList();
      List<PartMaterialRequirement> materialRequirements = tool.getToolDefinition().getRequiredComponents();

      for (int i = 0; i < materialRequirements.size(); i++) {
        PartMaterialRequirement requirement = materialRequirements.get(i);
        StringBuilder sb = new StringBuilder();

        ItemStack slotStack = this.container.getSlot(i).getStack();
        if (!requirement.isValid(slotStack)) {
          sb.append(TextFormatting.RED);

          if (slotStack.getItem() instanceof IMaterial) {
            if (requirement.isValidItem(slotStack.getItem())) {
              // the item has an invalid material
              this.warning(new TranslationTextComponent("gui.tconstruct.error.wrong_material_part").getFormattedText());
            }
          }
        }

        sb.append(" * ");

        Item part = requirement.getPossiblePart();

        sb.append(part.getDisplayName(new ItemStack(part)).getFormattedText());
        sb.append("/");

        sb.deleteCharAt(sb.length() - 1); // removes last '/'
        text.add(sb.toString());
      }

      this.traitInfo.setCaption(new TranslationTextComponent("gui.tconstruct.tool_station.components").getFormattedText());
      this.traitInfo.setText(text.toArray(new String[0]));
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if(this.toolInfo.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    if(this.traitInfo.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    return this.textField.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unkowwn) {
    if (this.toolInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    if (this.traitInfo.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
      return false;
    }

    return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unkowwn);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (this.toolInfo.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    if (this.traitInfo.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.toolInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    if (this.traitInfo.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    return super.mouseReleased(mouseX, mouseY, state);
  }

  @Override
  public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    if (p_keyPressed_1_ == 256) {
      this.minecraft.player.closeScreen();
    }

    boolean keyPressed = this.textField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);

    if (keyPressed) {
      TinkerNetwork.getInstance().sendToServer(new ToolStationTextPacket(this.textField.getText()));
      this.container.setToolName(textField.getText());
    }

    return keyPressed || this.textField.canWrite() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
  }

  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    if (!this.textField.isFocused()) {
      return super.charTyped(typedChar, keyCode);
    } else {
      if (keyCode == 1) {
        this.minecraft.player.closeScreen();
        return true;
      }

      if (this.textField.charTyped(typedChar, keyCode)) {
        TinkerNetwork.getInstance().sendToServer(new ToolStationTextPacket(textField.getText()));
        this.container.setToolName(textField.getText());
        return true;
      }
    }

    return false;
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

  @Override
  public void drawSlot(Slot slotIn) {
    // don't draw dormant slots with no item
    if (slotIn instanceof ToolStationInSlot && ((ToolStationInSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return;
    }

    super.drawSlot(slotIn);
  }

  @Override
  public boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
    if (slotIn instanceof ToolStationInSlot && ((ToolStationInSlot) slotIn).isDormant() && !slotIn.getHasStack()) {
      return false;
    }

    return super.isSlotSelected(slotIn, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(BACKGROUND);

    // looks like there's a weird case where this is called before init? Not reproducible but meh.
    if (this.textField != null) {
      if (this.textField.isFocused()) {
        ACTIVE_TEXT_FIELD.draw(cornerX + 68, cornerY + 6);
      }

      // draw textField
      this.textField.render(mouseX, mouseX, partialTicks);
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
      } else if (this.currentData == ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME)) {
        this.minecraft.getTextureManager().bindTexture(Icons.ICONS);
        Icons.ANVIL.draw(logoX, logoY);
      }
    }

    RenderSystem.scalef(1f / scale, 1f / scale, 1.0f);
    RenderSystem.translatef(-xOff, -yOff, 0.0f);

    // rebind gui texture since itemstack drawing sets it to something else
    this.minecraft.getTextureManager().bindTexture(BACKGROUND);

    RenderSystem.enableBlend();
    RenderSystem.enableAlphaTest();
    RenderHelper.disableStandardItemLighting();
    RenderSystem.disableDepthTest();

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.82f);
    ITEM_COVER.draw(this.cornerX + 7, this.cornerY + 18);

    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.28f);
    for (int i = 0; i < this.activeSlots; i++) {
      Slot slot = this.container.getSlot(i);
      SLOT_BACKGROUND.draw(x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
    }

    // full opaque. Draw the borders of the slots
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    for (int i = 0; i < TABLE_SLOT_COUNT; i++) {
      Slot slot = this.container.getSlot(i);
      if (slot instanceof ToolStationInSlot && (!((ToolStationInSlot) slot).isDormant() || slot.getHasStack())) {
        SLOT_BORDER.draw(x + this.cornerX + slot.xPos - 1, y + this.cornerY + slot.yPos - 1);
      }
    }

    this.minecraft.getTextureManager().bindTexture(Icons.ICONS);

    if (this.currentData == ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME)) {
      this.drawRepairSlotIcons();
    } else if (this.currentData.getItemStack().getItem() instanceof ToolCore) {
      for (int i = 0; i < this.activeSlots; i++) {
        Slot slot = this.container.getSlot(i);

        if (!(slot instanceof ToolStationInSlot)) {
          continue;
        }

        ItemStack stack = ((ToolStationInSlot) slot).icon;

        if (slot.getHasStack()) {
          continue;
        }

        if (stack == null) {
          continue;
        }

        if (stack.getItem() == null) {
          continue;
        }

        this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        Function<ResourceLocation, TextureAtlasSprite> spriteGetter = this.minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

        ResourceLocation location = stack.getItem().getRegistryName();

        TextureAtlasSprite sprite = spriteGetter.apply(new ResourceLocation(location.getNamespace(), "gui/tinker_pattern/" + location.getPath()));
        blit(x + this.cornerX + slot.xPos, y + this.cornerY + slot.yPos, 100, 16, 16, sprite);
      }
    }

    this.minecraft.getTextureManager().bindTexture(BACKGROUND);
    x = this.toolStationButtonsScreen.guiLeft - this.leftBeam.w;
    y = this.cornerY;
    // draw the beams at the top
    x += this.leftBeam.draw(x, y);
    x += this.centerBeam.drawScaledX(x, y, this.toolStationButtonsScreen.xSize);
    this.rightBeam.draw(x, y);

    x = toolInfo.guiLeft - this.leftBeam.w;
    x += this.leftBeam.draw(x, y);
    x += this.centerBeam.drawScaledX(x, y, this.toolInfo.xSize);
    this.rightBeam.draw(x, y);

    // draw the decoration for the buttons
    for (Object o : this.toolStationButtonsScreen.getButtons()) {
      ButtonItem button = (ButtonItem) o;

      this.buttonDecorationTop.draw(button.x, button.y - this.buttonDecorationTop.h);
      // don't draw the bottom for the buttons in the last row
      if (button.buttonId < this.toolStationButtonsScreen.getButtons().size() - COLUMN_COUNT) {
        this.buttonDecorationBot.draw(button.x, button.y + button.getHeight());
      }
    }

    // draw the decorations for the panels
    this.panelDecorationL.draw(this.toolInfo.guiLeft + 5, this.toolInfo.guiTop - this.panelDecorationL.h);
    this.panelDecorationR.draw(this.toolInfo.guiRight() - 5 - this.panelDecorationR.w, this.toolInfo.guiTop - this.panelDecorationR.h);
    this.panelDecorationL.draw(this.traitInfo.guiLeft + 5, this.traitInfo.guiTop - this.panelDecorationL.h);
    this.panelDecorationR.draw(this.traitInfo.guiRight() - 5 - this.panelDecorationR.w, this.traitInfo.guiTop - this.panelDecorationR.h);

    RenderSystem.enableDepthTest();

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  protected void drawRepairSlotIcons() {
    for (int i = 0; i < this.activeSlots; i++) {
      this.drawRepairSlotIcon(i);
    }
  }

  protected void drawRepairSlotIcon(int i) {
    ElementScreen icon = null;
    Slot slot = this.container.getSlot(i);

    // only empty solts get the logo since something else than the displayed thing might be in there.
    // which would look weird.
    if (slot.getHasStack()) {
      return;
    }

    if (i == 0) {
      icon = Icons.PICKAXE;
    } else if (i == 1) {
      icon = Icons.DUST;
    } else if (i == 2) {
      icon = Icons.LAPIS;
    } else if (i == 3) {
      icon = Icons.INGOT;
    } else if (i == 4) {
      icon = Icons.GEM;
    } else if (i == 5) {
      icon = Icons.QUARTZ;
    }

    if (icon != null) {
      this.drawIconEmpty(slot, icon);
    }
  }

  protected void wood() {
    this.toolInfo.wood();
    this.traitInfo.wood();

    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18, 0);

    this.toolStationButtonsScreen.wood();

    this.leftBeam = LEFT_BEAM;
    this.rightBeam = RIGHT_BEAM;
    this.centerBeam = CENTER_BEAM;
  }

  protected void metal() {
    this.toolInfo.metal();
    this.traitInfo.metal();

    this.buttonDecorationTop = SLOT_SPACE_TOP.shift(SLOT_SPACE_TOP.w * 2, 0);
    this.buttonDecorationBot = SLOT_SPACE_BOTTOM.shift(SLOT_SPACE_BOTTOM.w * 2, 0);
    this.panelDecorationL = PANEL_SPACE_LEFT.shift(18 * 2, 0);
    this.panelDecorationR = PANEL_SPACE_RIGHT.shift(18 * 2, 0);

    this.toolStationButtonsScreen.metal();

    this.leftBeam = LEFT_BEAM.shift(0, LEFT_BEAM.h);
    this.rightBeam = RIGHT_BEAM.shift(0, RIGHT_BEAM.h);
    this.centerBeam = CENTER_BEAM.shift(0, CENTER_BEAM.h);
  }

  @Override
  public void error(String message) {
    this.toolInfo.setCaption(new TranslationTextComponent("gui.tconstruct.error").getFormattedText());
    this.toolInfo.setText(message);
    this.traitInfo.setCaption("");
    this.traitInfo.setText();
  }

  @Override
  public void warning(String message) {
    this.toolInfo.setCaption(new TranslationTextComponent("gui.tconstruct.warning").getFormattedText());
    this.toolInfo.setText(message);
    this.traitInfo.setCaption("");
    this.traitInfo.setText();
  }

  public void onToolSelection(SlotInformation data) {
    this.activeSlots = Math.min(data.getPoints().size(), TABLE_SLOT_COUNT);
    this.currentData = data;

    ItemStack tool = ItemStack.EMPTY;

    if (data.getItemStack().getItem() instanceof ToolCore) {
      tool = data.getItemStack();
    }

    container.setToolSelection(tool, activeSlots);
    TinkerNetwork.getInstance().sendToServer(new ToolStationSelectionPacket(tool, this.activeSlots));
    this.updateGUI();
  }

  public void onToolSelectionPacket(ToolStationSelectionPacket packet) {
    SlotInformation data = ToolSlotInformationLoader.get(packet.tool.getItem().getRegistryName());

    if (data == null || data.getItemStack().isEmpty() || data.getItemStack() == ItemStack.EMPTY) {
      data = ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME);
    }

    this.activeSlots = packet.activeSlots;
    this.currentData = data;

    this.toolStationButtonsScreen.setSelectedButtonByTool(this.currentData.getItemStack());

    this.updateGUI();
  }

  public Set<ToolCore> getBuildableItems() {
    return ToolRegistry.getTools();
  }
}
