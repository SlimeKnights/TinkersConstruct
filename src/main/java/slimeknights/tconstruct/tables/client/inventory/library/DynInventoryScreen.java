package slimeknights.tconstruct.tables.client.inventory.library;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.client.screen.SliderWidget;
import slimeknights.tconstruct.tables.client.inventory.module.GenericScreen;

public class DynInventoryScreen extends ModuleScreen {

  // Graphic Resources
  protected static final ScalableElementScreen slot = GenericScreen.slot;
  private static final ScalableElementScreen slotEmpty = GenericScreen.slotEmpty;

  protected static final ElementScreen sliderNormal = GenericScreen.sliderNormal;
  protected static final ElementScreen sliderLow = GenericScreen.sliderLow;
  protected static final ElementScreen sliderHigh = GenericScreen.sliderHigh;
  protected static final ElementScreen sliderTop = GenericScreen.sliderTop;
  protected static final ElementScreen sliderBottom = GenericScreen.sliderBottom;
  protected static final ScalableElementScreen sliderBackground = GenericScreen.sliderBackground;

  protected SliderWidget slider = new SliderWidget(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);

  // Logic
  protected int columns; // columns displayed
  protected int rows; // rows displayed
  protected int slotCount;
  protected boolean sliderActive;

  protected int firstSlotId;
  protected int lastSlotId;

  // Container containing the slots to display
  protected final Container container;

  public DynInventoryScreen(MultiModuleScreen<?> parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, false, false);
    this.container = container;

    // default parameters.
    // These correspond to a regular inventory
    this.xOffset = 7;
    this.yOffset = 17;
    this.xSize = 162;
    this.ySize = 54;

    this.slotCount = container.inventorySlots.size();
    this.firstSlotId = 0;
    this.lastSlotId = this.slotCount;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.guiLeft = parentX + xOffset;
    this.guiTop = parentY + yOffset;

    // calculate rows and columns from space
    this.columns = this.xSize / slot.w;
    this.rows = this.ySize / slot.h;

    this.sliderActive = slotCount > this.columns * this.rows;

    this.updateSlider();

    // recalculate columns with slider
    if (sliderActive) {
      this.columns = (xSize - slider.width) / slot.w;
      this.updateSlider();
    }

    this.updateSlots();
  }

  protected void updateSlider() {
    int max = 0;

    if (this.sliderActive) {
      this.slider.show();
      max = (this.slotCount - 1) / this.columns - this.rows + 1; // the assumption here is that for an active slider this always is >0
    } else {
      slider.hide();
    }

    this.slider.setPosition(this.guiLeft + this.xSize - slider.width, this.guiTop);
    this.slider.setSize(this.ySize);
    this.slider.setSliderParameters(0, max, 1);
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (!this.sliderActive) {
      return false;
    }

    if (mouseButton == 0) {
      if (mouseX >= this.slider.xPos && mouseY >= this.slider.yPos && mouseX <= this.slider.xPos + this.slider.width && mouseY <= this.slider.yPos + this.slider.height) {
        this.slider.handleMouseClicked((int) mouseX, (int) mouseY, mouseButton);
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (!this.sliderActive) {
      return false;
    }

    this.slider.handleMouseReleased();
    return mouseX >= this.slider.xPos && mouseY >= this.slider.yPos && mouseX <= this.slider.xPos + this.slider.width && mouseY <= this.slider.yPos + this.slider.height;
  }

  @Override
  public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollData) {
    if (!this.sliderActive) {
      return false;
    }

    return this.slider.mouseScrolled(scrollData, !this.isMouseOverFullSlot(mouseX, mouseY) && this.isMouseInModule((int) mouseX, (int) mouseY));
  }

  public void update(int mouseX, int mouseY) {
    if (!this.sliderActive) {
      return;
    }

    this.slider.update(mouseX, mouseY);
    this.updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    // all visible
    if (!this.slider.isEnabled()) {
      return true;
    }

    int index = slot.getSlotIndex();

    return (this.firstSlotId <= index && this.lastSlotId > index); // inside visible area
    //|| indexStart > index || indexEnd <= index; // or not our concern
  }

  // updates slot visibility
  public void updateSlots() {
    // calculate displayed slots
    int oldFirstSlot = this.firstSlotId;
    int oldLastSlot = this.lastSlotId;
    this.firstSlotId = this.slider.getValue() * this.columns;
    this.lastSlotId = Math.min(this.slotCount, this.firstSlotId + this.rows * this.columns);
    if (oldFirstSlot != this.firstSlotId || oldLastSlot != this.lastSlotId) {
      for (Slot slot : this.container.inventorySlots) {
        if (this.shouldDrawSlot(slot)) {
          // calc position of the slot
          int offset = slot.getSlotIndex() - this.firstSlotId;
          int x = (offset % this.columns) * DynInventoryScreen.slot.w;
          int y = (offset / this.columns) * DynInventoryScreen.slot.h;

          slot.xPos = xOffset + x + 1;
          slot.yPos = yOffset + y + 1;
        } else {
          slot.xPos = 0;
          slot.yPos = 0;
        }
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(GenericScreen.LOCATION);
    if (!this.slider.isHidden()) {
      this.slider.draw(matrices);

      this.updateSlots();
    }

    // fully filled rows
    int fullRows = (this.lastSlotId - this.firstSlotId) / this.columns;
    int w = this.columns * slot.w;
    int y;

    for (y = 0; y < fullRows * slot.h && y < this.ySize; y += slot.h) {
      slot.drawScaledX(matrices, this.guiLeft, this.guiTop + y, w);
    }

    // draw partial row and unused slots
    int slotsLeft = (this.lastSlotId - this.firstSlotId) % this.columns;
    if (slotsLeft > 0) {
      slot.drawScaledX(matrices, this.guiLeft, this.guiTop + y, slotsLeft * slot.w);
      // empty slots that don't exist
      slotEmpty.drawScaledX(matrices, this.guiLeft + slotsLeft * slot.w, this.guiTop + y, w - slotsLeft * slot.w);
    }
  }
}
