package slimeknights.tconstruct.tables.client.inventory.library;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.BaseContainer;

public class ScalingChestScreen extends DynInventoryScreen {

  protected final IInventory inventory;

  public ScalingChestScreen(MultiModuleScreen<?> parent, BaseContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title);

    this.inventory = (IInventory) container.getTileEntity();
    if (this.inventory != null)
      this.slotCount = this.inventory.getSizeInventory();
    else
      this.slotCount = 0;
    this.sliderActive = true;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.guiLeft = parentX + this.xOffset;
    this.guiTop = parentY + this.yOffset;

    // calculate rows and columns from space
    this.columns = (this.xSize - this.slider.width) / slot.w;
    this.rows = this.ySize / slot.h;

    this.updateSlider();
    this.updateSlots();
  }

  @Override
  protected void updateSlider() {
    this.sliderActive = this.slotCount > this.columns * this.rows;
    super.updateSlider();
    this.slider.setEnabled(this.sliderActive);
    this.slider.show();
  }

  @Override
  public void update(int mouseX, int mouseY) {
    if (this.inventory == null) {
      this.slotCount = 0;
    } else {
      this.slotCount = this.inventory.getSizeInventory();
    }
    super.update(mouseX, mouseY);

    this.updateSlider();
    this.slider.show();
    this.updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    if (this.inventory == null) {
      return false;
    }

    if (slot.getSlotIndex() >= this.inventory.getSizeInventory()) {
      return false;
    }

    return super.shouldDrawSlot(slot);
  }
}
