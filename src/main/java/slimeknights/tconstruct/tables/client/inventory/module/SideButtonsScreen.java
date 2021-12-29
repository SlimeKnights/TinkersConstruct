package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;

// TODO: this is raw types
public class SideButtonsScreen extends ModuleScreen {

  private final int columns;
  private Button clickedButton;
  protected int buttonCount = 0;

  public int spacing = 4;

  public SideButtonsScreen(MultiModuleScreen parent, AbstractContainerMenu container, Inventory playerInventory, Component title, int columns) {
    this(parent, container, playerInventory, title, columns, false);
  }

  public SideButtonsScreen(MultiModuleScreen parent, AbstractContainerMenu container, Inventory playerInventory, Component title, int columns, boolean right) {
    super(parent, container, playerInventory, title, right, false);
    this.columns = columns;
  }

  public void addSideButton(Button button) {
    int rows = (this.buttonCount - 1) / this.columns + 1;

    this.imageWidth = button.getWidth() * this.columns + this.spacing * (this.columns - 1);
    this.imageHeight = button.getHeight() * rows + this.spacing * (rows - 1);

    int offset = this.buttonCount;
    int x = (offset % columns) * (button.getWidth() + this.spacing);
    int y = (offset / columns) * (button.getHeight() + this.spacing);

    button.x = leftPos + x;
    button.y = topPos + y;

    if (this.right) {
      button.x += parent.imageWidth;
    }

    this.addRenderableWidget(button);
    this.buttonCount++;
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0) {
      for (Widget widget : this.renderables) {
        if (widget instanceof Button button) {

          if (button.mouseClicked(mouseX, mouseY, mouseButton)) {
            this.clickedButton = button;
            return true;
          }
        }
      }
    }

    return false;
  }

  @Override
  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (clickedButton != null) {
      clickedButton.mouseReleased(mouseX, mouseY, state);
      clickedButton = null;
      return true;
    }

    return false;
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    for (Widget widget : this.renderables) {
      widget.render(matrices, mouseX, mouseY, partialTicks);
    }
  }

  @Override
  protected void renderLabels(PoseStack matrixStack, int x, int y) {
  }
}
