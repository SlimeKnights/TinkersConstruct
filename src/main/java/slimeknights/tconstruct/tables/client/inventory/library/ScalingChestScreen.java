package slimeknights.tconstruct.tables.client.inventory.library;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.EmptyItemHandler;

import java.util.Optional;

public class ScalingChestScreen<T extends TileEntity> extends DynInventoryScreen {
  private final IScalingInventory scaling;
  public ScalingChestScreen(MultiModuleScreen<?> parent, BaseContainer<T> container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title);
    TileEntity tile = container.getTile();
    IItemHandler handler = Optional.ofNullable(tile)
                                   .flatMap(t -> t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve())
                                   .orElse(EmptyItemHandler.INSTANCE);
    this.scaling = handler instanceof IScalingInventory ? (IScalingInventory) handler : handler::getSlots;
    this.slotCount = scaling.getVisualSize();
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
    this.slotCount = this.scaling.getVisualSize();
    super.update(mouseX, mouseY);
    this.updateSlider();
    this.updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    if (slot.getSlotIndex() >= this.scaling.getVisualSize()) {
      return false;
    }
    return super.shouldDrawSlot(slot);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {}
}
