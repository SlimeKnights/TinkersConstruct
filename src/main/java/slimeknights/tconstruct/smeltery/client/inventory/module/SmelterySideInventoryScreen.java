package slimeknights.tconstruct.smeltery.client.inventory.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.smeltery.client.inventory.SmelteryScreen;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

public class SmelterySideInventoryScreen extends SideInventoryScreen<SmelteryScreen,SideInventoryContainer<SmelteryTileEntity>> {
  public static final Identifier SLOT_LOCATION = SmelteryScreen.BACKGROUND;

  // TODO: read from a proper place
  public SmelterySideInventoryScreen(SmelteryScreen parent, SideInventoryContainer<SmelteryTileEntity> container, PlayerInventory playerInventory, int slotCount, int columns) {
    super(parent, container, playerInventory, LiteralText.EMPTY, slotCount, columns, false, true);
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
    assert client != null;
    client.getTextureManager().bindTexture(SLOT_LOCATION);
    int ret = super.drawSlots(matrices, xPos, yPos);
    client.getTextureManager().bindTexture(GENERIC_INVENTORY);
    return ret;
  }

  @Override
  public void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawForeground(matrices, mouseX, mouseY);
  }

  @Override
  protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawMouseoverTooltip(matrices, mouseX, mouseY);
    if (parent.melting != null) {
      parent.melting.drawHeatTooltips(matrices, mouseX, mouseY);
    }
  }
}
