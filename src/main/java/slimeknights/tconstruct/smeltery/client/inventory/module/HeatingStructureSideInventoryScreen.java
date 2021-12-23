package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.smeltery.client.inventory.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.tileentity.controller.HeatingStructureTileEntity;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

public class HeatingStructureSideInventoryScreen extends SideInventoryScreen<HeatingStructureScreen,SideInventoryContainer<? extends HeatingStructureTileEntity>> {
  public static final ResourceLocation SLOT_LOCATION = HeatingStructureScreen.BACKGROUND;

  // TODO: read from a proper place
  public HeatingStructureSideInventoryScreen(HeatingStructureScreen parent, SideInventoryContainer<? extends HeatingStructureTileEntity> container, PlayerInventory playerInventory, int slotCount, int columns) {
    super(parent, container, playerInventory, StringTextComponent.EMPTY, slotCount, columns, false, true);
    slot = new ScalableElementScreen(0, 166, 22, 18, 256, 256);
    slotEmpty = new ScalableElementScreen(22, 166, 22, 18, 256, 256);
    yOffset = 0;
  }

  @Override
  protected boolean shouldDrawName() {
    return false;
  }

  @Override
  protected void updateSlots() {
    // adjust for the heat bar
    xOffset += 4;
    super.updateSlots();
    xOffset -= 4;
  }

  @Override
  protected int drawSlots(MatrixStack matrices, int xPos, int yPos) {
    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(SLOT_LOCATION);
    int ret = super.drawSlots(matrices, xPos, yPos);
    minecraft.getTextureManager().bindTexture(GENERIC_INVENTORY);
    return ret;
  }

  @Override
  public void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);
    if (parent.melting != null) {
      parent.melting.drawHeatTooltips(matrices, mouseX, mouseY);
    }
  }
}
