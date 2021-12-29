package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.tables.client.inventory.library.ScalingChestScreen;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;
import slimeknights.tconstruct.tables.tileentity.chest.ChestTileEntity;

public class TinkerChestScreen extends BaseStationScreen<ChestTileEntity, BaseStationContainer<ChestTileEntity>> {

  protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(7 + 18, 7, 18, 18);
  public ScalingChestScreen<ChestTileEntity> scalingChestScreen;

  public TinkerChestScreen(BaseStationContainer<ChestTileEntity> container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    TinkerChestContainer.DynamicChestInventory chestContainer = container.getSubContainer(TinkerChestContainer.DynamicChestInventory.class);
    if (chestContainer != null) {
      this.scalingChestScreen = new ScalingChestScreen<>(this, chestContainer, playerInventory, title);
      this.addModule(scalingChestScreen);
    }
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, BLANK_BACK);

    if (this.scalingChestScreen != null) {
      this.scalingChestScreen.update(mouseX, mouseY);
    }

    super.renderBg(matrices, partialTicks, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.scalingChestScreen == null) {
      return false;
    }

    if (this.scalingChestScreen.handleMouseClicked(mouseX, mouseY, mouseButton)) {
      return false;
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    if (this.scalingChestScreen == null) {
      return false;
    }

    if (this.scalingChestScreen.handleMouseClickMove(mouseX, mouseY, button, dragX)) {
      return false;
    }

    return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (this.scalingChestScreen == null) {
      return false;
    }

    if (this.scalingChestScreen.handleMouseScrolled(mouseX, mouseY, delta)) {
      return false;
    }

    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    if (this.scalingChestScreen == null) {
      return false;
    }

    if (this.scalingChestScreen.handleMouseReleased(mouseX, mouseY, state)) {
      return false;
    }

    return super.mouseReleased(mouseX, mouseY, state);
  }
}
