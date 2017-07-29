package slimeknights.tconstruct.tools.common.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.tconstruct.tools.common.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

@SideOnly(Side.CLIENT)
public class GuiCraftingStation extends GuiTinkerStation implements IRecipeShownListener {

  private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/crafting_table.png");
  private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
  protected final TileCraftingStation tile;
  private final boolean hasSideInventory;
  private final GuiRecipeBook recipeBookGui;
  private boolean widthTooNarrow;
  private GuiButtonImage recipeButton;

  public GuiCraftingStation(InventoryPlayer playerInv, World world, BlockPos pos, TileCraftingStation tile) {
    super(world, pos, (ContainerTinkerStation) tile.createContainer(playerInv, world, pos));

    this.recipeBookGui = new GuiRecipeBook();
    this.tile = tile;

    boolean hasSideInventory = false;

    if(inventorySlots instanceof ContainerCraftingStation) {
      ContainerCraftingStation container = (ContainerCraftingStation) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerSideInventory.class);
      if(chestContainer != null) {
        if(chestContainer.getTile() instanceof TileEntityChest) {
          // Fix: chests don't update their single/double chest status clientside once accessed
          ((TileEntityChest) chestContainer.getTile()).doubleChestHandler = null;
        }
        this.addModule(new GuiSideInventory(this, chestContainer, chestContainer.getSlotCount(), chestContainer.columns));
        hasSideInventory = true;
      }
    }
    this.hasSideInventory = hasSideInventory;
  }

  @Override
  public void initGui() {
    super.initGui();
    if(inventorySlots instanceof ContainerCraftingStation) {
      widthTooNarrow = this.width < 379;
      recipeBookGui.init(this.width, this.height, this.mc, widthTooNarrow, this.container, ((ContainerCraftingStation) this.container).getCraftMatrix());
      recipeButton = new GuiButtonImage(10, this.cornerX + 5, this.height / 2 - 49, 20, 18, 0, 168, 19, CRAFTING_TABLE_GUI_TEXTURES);
    }

    if(hasSideInventory && recipeBookGui.isVisible()) {
      recipeBookGui.toggleVisibility();
    }
    if(recipeBookGui.isVisible()) {
      buttonList.add(this.recipeButton);
      updateRecipeBook();
    }
  }

  public boolean isSlotInChestInventory(Slot slot) {
    GuiModule module = getModuleForSlot(slot.slotNumber);
    return module instanceof GuiSideInventory;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  private void updateRecipeBook() {
    int newLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
    int diff = newLeft - cornerX;
    this.cornerX += diff;
    this.guiLeft += diff;
    this.recipeButton.setPosition(this.cornerX + 5, this.height / 2 - 49);
    modules.forEach(module -> module.updatePosition(cornerX, cornerY, realWidth, realHeight));
  }

  /* Everything below this line is copy&pasta from the workbench for the recipebook
   * ------------------------------------------------------------------------------
   */

  @Override
  public void updateScreen() {
    super.updateScreen();
    this.recipeBookGui.tick();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if(this.recipeBookGui.isVisible() && this.widthTooNarrow) {
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      this.recipeBookGui.render(mouseX, mouseY, partialTicks);
    }
    else {
      this.recipeBookGui.render(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
    }

    this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
  }


  @Override
  protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
    return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
      if(!this.widthTooNarrow || !this.recipeBookGui.isVisible()) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
      }
    }
  }

  @Override
  protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
    boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + this.xSize || p_193983_2_ >= p_193983_4_ + this.ySize;
    return this.recipeBookGui.hasClickedOutside(p_193983_1_, p_193983_2_, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (!hasSideInventory && button.id == recipeButton.id && (inventorySlots instanceof ContainerCraftingStation))
    {
      this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerCraftingStation)this.inventorySlots).getCraftMatrix());
      this.recipeBookGui.toggleVisibility();
      updateRecipeBook();
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (!this.recipeBookGui.keyPressed(typedChar, keyCode))
    {
      super.keyTyped(typedChar, keyCode);
    }
  }

  @Override
  protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
    super.handleMouseClick(slotIn, slotId, mouseButton, type);
    this.recipeBookGui.slotClicked(slotIn);
  }

  @Override
  public void recipesUpdated() {
    recipeBookGui.recipesUpdated();
  }

  @Override
  public void onGuiClosed() {
    this.recipeBookGui.removed();
    super.onGuiClosed();
  }
}
