package slimeknights.tconstruct.tables.client.inventory.chest;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.library.ScalingChestScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;

public class PatternChestScreen extends TinkerStationScreen<PatternChestTileEntity, TinkerStationContainer<PatternChestTileEntity>> {

  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(7 + 18, 7, 18, 18);

  public ScalingChestScreen<PatternChestTileEntity> scalingChestScreen;

  public PatternChestScreen(TinkerStationContainer<PatternChestTileEntity> container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    // FIXME: nullable
    this.scalingChestScreen = new ScalingChestScreen<>(this, container.getSubContainer(PatternChestContainer.DynamicChestInventory.class), playerInventory, title);
    this.addModule(scalingChestScreen);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, BLANK_BACK);

    this.scalingChestScreen.update(mouseX, mouseY);

    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);
  }
}
