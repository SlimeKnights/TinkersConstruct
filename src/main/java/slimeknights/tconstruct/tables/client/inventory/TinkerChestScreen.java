package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.tables.client.inventory.library.ScalingChestScreen;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

public class TinkerChestScreen extends BaseStationScreen<TinkerChestTileEntity, BaseStationContainer<TinkerChestTileEntity>> {
  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(7 + 18, 7, 18, 18);
  public ScalingChestScreen<TinkerChestTileEntity> scalingChestScreen;
  public TinkerChestScreen(BaseStationContainer<TinkerChestTileEntity> container, PlayerInventory playerInventory, Text title) {
    super(container, playerInventory, title);
    TinkerChestContainer.DynamicChestInventory chestContainer = container.getSubContainer(TinkerChestContainer.DynamicChestInventory.class);
    if (chestContainer != null) {
      this.scalingChestScreen = new ScalingChestScreen<>(this, chestContainer, playerInventory, title);
      this.addModule(scalingChestScreen);
    }
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, BLANK_BACK);

    this.scalingChestScreen.update(mouseX, mouseY);

    super.drawBackground(matrices, partialTicks, mouseX, mouseY);
  }
}
