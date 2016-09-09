package slimeknights.tconstruct.tools.common.client;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.BaseContainer;

public class GuiScalingChest extends GuiDynInventory {

  protected final IInventory inventory;

  public GuiScalingChest(GuiMultiModule parent, BaseContainer container) {
    super(parent, container);

    inventory = (IInventory) container.getTile();
    slotCount = inventory.getSizeInventory();
    sliderActive = true;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.guiLeft = parentX + xOffset;
    this.guiTop = parentY + yOffset;

    // calculate rows and columns from space
    columns = (xSize - slider.width) / slot.w;
    rows = ySize / slot.h;

    updateSlider();
    updateSlots();
  }

  @Override
  protected void updateSlider() {
    sliderActive = slotCount > columns * rows;
    super.updateSlider();
    slider.setEnabled(sliderActive);
    slider.show();
  }

  @Override
  public void update(int mouseX, int mouseY) {
    slotCount = inventory.getSizeInventory();
    super.update(mouseX, mouseY);

    updateSlider();
    slider.show();
    updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    if(slot.getSlotIndex() >= inventory.getSizeInventory()) {
      return false;
    }
    return super.shouldDrawSlot(slot);
  }
}
