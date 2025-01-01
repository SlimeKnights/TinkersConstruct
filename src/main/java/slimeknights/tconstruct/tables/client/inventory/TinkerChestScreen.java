package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.tconstruct.tables.block.entity.chest.AbstractChestBlockEntity;
import slimeknights.tconstruct.tables.client.inventory.module.ScalingChestScreen;
import slimeknights.tconstruct.tables.menu.TabbedContainerMenu;
import slimeknights.tconstruct.tables.menu.TinkerChestContainerMenu;

public class TinkerChestScreen extends BaseTabbedScreen<AbstractChestBlockEntity,TabbedContainerMenu<AbstractChestBlockEntity>> {
  // TODO: can this safely be removed? its unused
  //protected static final ScalableElementScreen BACKGROUND = new ScalableElementScreen(7 + 18, 7, 18, 18);
  public ScalingChestScreen<AbstractChestBlockEntity> scalingChestScreen;

  public TinkerChestScreen(TabbedContainerMenu<AbstractChestBlockEntity> container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    TinkerChestContainerMenu.DynamicChestInventory chestContainer = container.getSubContainer(TinkerChestContainerMenu.DynamicChestInventory.class);
    if (chestContainer != null) {
      this.scalingChestScreen = new ScalingChestScreen<>(this, chestContainer, playerInventory, title);
      this.addModule(scalingChestScreen);
    }
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(graphics, BLANK_BACK);

    if (this.scalingChestScreen != null) {
      this.scalingChestScreen.update(mouseX, mouseY);
    }

    super.renderBg(graphics, partialTicks, mouseX, mouseY);
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
