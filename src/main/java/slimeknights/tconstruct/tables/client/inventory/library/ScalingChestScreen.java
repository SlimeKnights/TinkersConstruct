package slimeknights.tconstruct.tables.client.inventory.library;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.BaseContainer;

public class ScalingChestScreen<T extends BlockEntity & Inventory> extends DynInventoryScreen {

  protected final Inventory inventory;

  public ScalingChestScreen(MultiModuleScreen<?> parent, BaseContainer<T> container, PlayerInventory playerInventory, Text title) {
    super(parent, container, playerInventory, title);

    this.inventory = container.getTile();
    if (this.inventory != null)
      this.slotCount = this.inventory.size();
    else
      this.slotCount = 0;
    this.sliderActive = true;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.x = parentX + this.xOffset;
    this.y = parentY + this.yOffset;

    // calculate rows and columns from space
    this.columns = (this.backgroundWidth - this.slider.width) / slot.w;
    this.rows = this.backgroundHeight / slot.h;

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
      this.slotCount = this.inventory.size();
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

    if (slot.index >= this.inventory.size()) {
      return false;
    }

    return super.shouldDrawSlot(slot);
  }

  @Override
  protected void drawForeground(MatrixStack matrixStack, int x, int y) {
  }

}
