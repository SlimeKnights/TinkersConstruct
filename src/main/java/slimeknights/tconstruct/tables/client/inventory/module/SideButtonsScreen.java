package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class SideButtonsScreen extends ModuleScreen {

  private final int columns;
  private ButtonWidget clickedButton;
  protected int buttonCount = 0;

  public int spacing = 4;

  public SideButtonsScreen(MultiModuleScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title, int columns) {
    this(parent, container, playerInventory, title, columns, false);
  }

  public SideButtonsScreen(MultiModuleScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title, int columns, boolean right) {
    super(parent, container, playerInventory, title, right, false);
    this.columns = columns;
  }

  public void addSideButton(ButtonWidget button) {
    int rows = (this.buttonCount - 1) / this.columns + 1;

    this.backgroundWidth = button.getWidth() * this.columns + this.spacing * (this.columns - 1);
    // TODO: getHeightRealms->getHeight()
    this.backgroundHeight = button.getHeight() * rows + this.spacing * (rows - 1);

    int offset = this.buttonCount;
    int x = (offset % columns) * (button.getWidth() + this.spacing);
    // TODO: getHeightRealms->getHeight()
    int y = (offset / columns) * (button.getHeight() + this.spacing);

    button.x = x + x;
    button.y = y + y;

    if (this.right) {
      button.x += parent.backgroundWidth;
    }

    this.buttons.add(button);
    this.buttonCount++;
  }

  @Override
  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0) {
      for (AbstractButtonWidget widget : this.buttons) {
        if (widget instanceof ButtonWidget) {
          ButtonWidget button = (ButtonWidget) widget;

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
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    for (AbstractButtonWidget widget : this.buttons) {
      widget.render(matrices, mouseX, mouseY, partialTicks);
    }
  }

  @Override
  protected void drawForeground(MatrixStack matrixStack, int x, int y) {
  }
}
