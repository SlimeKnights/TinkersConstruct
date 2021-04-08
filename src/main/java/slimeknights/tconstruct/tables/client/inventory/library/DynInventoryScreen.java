package slimeknights.tconstruct.tables.client.inventory.library;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
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
  protected final ScreenHandler handler;

  public DynInventoryScreen(MultiModuleScreen<?> parent, ScreenHandler container, PlayerInventory playerInventory, Text title) {
    super(parent, container, playerInventory, title, false, false);
    this.handler = container;

    // default parameters.
    // These correspond to a regular inventory
    this.xOffset = 7;
    this.yOffset = 17;
    this.backgroundWidth = 162;
    this.backgroundHeight = 54;

    this.slotCount = container.slots.size();
    this.firstSlotId = 0;
    this.lastSlotId = this.slotCount;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.x = parentX + xOffset;
    this.y = parentY + yOffset;

    // calculate rows and columns from space
    this.columns = this.backgroundWidth / slot.w;
    this.rows = this.backgroundHeight / slot.h;

    this.sliderActive = slotCount > this.columns * this.rows;

    this.updateSlider();

    // recalculate columns with slider
    if (sliderActive) {
      this.columns = (backgroundWidth - slider.width) / slot.w;
      this.updateSlider();
    }

    this.updateSlots();
  }

  protected void updateSlider() {
    int max = 0;

    if (this.sliderActive) {
      this.slider.show();
      max = this.slotCount / this.columns - this.rows + 1; // the assumption here is that for an active slider this always is >0
    } else {
      slider.hide();
    }

    this.slider.setPosition(this.x + this.backgroundWidth - slider.width, this.y);
    this.slider.setSize(this.backgroundHeight);
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
    this.firstSlotId = this.slider.getValue() * this.columns;
    this.lastSlotId = Math.min(this.slotCount, this.firstSlotId + this.rows * this.columns);

    for (Slot slot : this.handler.slots) {
      if (this.shouldDrawSlot(slot)) {
        // calc position of the slot
        int offset = slot.getSlotIndex() - this.firstSlotId;
        int x = (offset % this.columns) * DynInventoryScreen.slot.w;
        int y = (offset / this.columns) * DynInventoryScreen.slot.h;

        slot.x = xOffset + x + 1;
        slot.y = yOffset + y + 1;
      } else {
        slot.x = 0;
        slot.y = 0;
      }
    }
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.client.getTextureManager().bindTexture(GenericScreen.LOCATION);
    if (!this.slider.isHidden()) {
      this.slider.draw(matrices);

      this.updateSlots();
    }

    // fully filled rows
    int fullRows = (this.lastSlotId - this.firstSlotId) / this.columns;
    int w = this.columns * slot.w;
    int y;

    for (y = 0; y < fullRows * slot.h && y < this.backgroundHeight; y += slot.h) {
      slot.drawScaledX(matrices, this.x, this.y + y, w);
    }

    // draw partial row and unused slots
    int slotsLeft = (this.lastSlotId - this.firstSlotId) % this.columns;
    if (slotsLeft > 0) {
      slot.drawScaledX(matrices, this.x, this.y + y, slotsLeft * slot.w);
      // empty slots that don't exist
      slotEmpty.drawScaledX(matrices, this.x + slotsLeft * slot.w, this.y + y, w - slotsLeft * slot.w);
    }
  }
}
