package slimeknights.tconstruct.tables.client.inventory.library;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.BaseContainer;

public class ScalingChestScreen<T extends TileEntity & IInventory & IScalingInventory> extends DynInventoryScreen {

  protected final T inventory;
  public ScalingChestScreen(MultiModuleScreen<?> parent, BaseContainer<T> container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title);
    this.inventory = container.getTile();
    this.slotCount = this.inventory != null ? this.inventory.getVisualSize() : 0;
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
    this.slotCount = this.inventory != null ? this.inventory.getVisualSize() : 0;
    super.update(mouseX, mouseY);
    this.updateSlider();
    this.updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    if (this.inventory == null || slot.getSlotIndex() >= this.inventory.getVisualSize()) {
      return false;
    }
    return super.shouldDrawSlot(slot);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {}
}
