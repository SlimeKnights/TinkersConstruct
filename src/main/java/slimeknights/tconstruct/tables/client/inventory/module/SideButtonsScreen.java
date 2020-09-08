package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class SideButtonsScreen extends ModuleScreen {

  private final int columns;
  private Button clickedButton;
  protected int buttonCount = 0;

  public int spacing = 4;

  public SideButtonsScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title, int columns) {
    this(parent, container, playerInventory, title, columns, false);
  }

  public SideButtonsScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title, int columns, boolean right) {
    super(parent, container, playerInventory, title, right, false);
    this.columns = columns;
  }

  public void addSideButton(Button button) {
    int rows = (this.buttonCount - 1) / this.columns + 1;

    this.xSize = button.getWidth() * this.columns + this.spacing * (this.columns - 1);
    this.ySize = button.getHeight() * rows + this.spacing * (rows - 1);

    int offset = this.buttonCount;
    int x = (offset % columns) * (button.getWidth() + this.spacing);
    int y = (offset / columns) * (button.getHeight() + this.spacing);

    button.x = guiLeft + x;
    button.y = guiTop + y;

    if (this.right) {
      button.x += parent.xSize;
    }

    this.buttons.add(button);
    this.buttonCount++;
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0) {
      for (Widget widget : this.buttons) {
        if (widget instanceof Button) {
          Button button = (Button) widget;

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
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    for (Widget widget : this.buttons) {
      widget.render(matrices, mouseX, mouseY, partialTicks);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
  }
}
