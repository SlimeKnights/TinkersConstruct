package slimeknights.tconstruct.tables.client.inventory.chest;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.library.ScalingChestScreen;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;

public class PartChestScreen extends TinkerStationScreen<PartChestTileEntity, TinkerStationContainer<PartChestTileEntity>> {

  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(7 + 18, 7, 18, 18);

  public ScalingChestScreen scalingChestScreen;

  public PartChestScreen(TinkerStationContainer<PartChestTileEntity> container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    this.scalingChestScreen = new ScalingChestScreen(this, container.getSubContainer(PartChestContainer.DynamicChestInventory.class), playerInventory, title);
    this.addModule(scalingChestScreen);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(BLANK_BACK);

    this.scalingChestScreen.update(mouseX, mouseY);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
