package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import org.apache.commons.compress.utils.Lists;
import slimeknights.mantle.client.screen.MultiModuleScreen;

import java.util.List;

public class SideButtonsWidget<T extends Button> implements Widget, GuiEventListener {

  public static final int SPACING = 4;

  protected final MultiModuleScreen<?> parent;

  public final int leftPos;
  public final int topPos;
  public final int imageWidth;
  public final int imageHeight;

  private final int columns;
  protected final List<T> buttons = Lists.newArrayList();
  private Button clickedButton;

  public SideButtonsWidget(MultiModuleScreen<?> parent, int leftPos, int topPos, int columns, int rows, int buttonWidth, int buttonHeight) {
    this.parent = parent;

    this.leftPos = leftPos;
    this.topPos = topPos;
    this.columns = columns;

    this.imageWidth = size(columns, buttonWidth);
    this.imageHeight = size(rows, buttonHeight);
  }

  public int guiRight() {
    return this.leftPos + this.imageWidth;
  }

  public int guiBottom() {
    return this.topPos + this.imageHeight;
  }

  public Rect2i getArea() {
    return new Rect2i(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
  }

  public void setButtonPositions() {
    for (int i = 0; i < this.buttons.size(); i++) {
      T button = this.buttons.get(i);
      int x = (i % columns) * (button.getWidth() + SPACING);
      int y = (i / columns) * (button.getHeight() + SPACING);
      button.x = leftPos + x;
      button.y = topPos + y;
    }
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return this.leftPos <= mouseX && mouseX < this.guiRight() && this.topPos <= mouseY && mouseY < this.guiBottom();
  }

  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0) {
      for (T button : this.buttons) {
        if (button.mouseClicked(mouseX, mouseY, mouseButton)) {
          this.clickedButton = button;
          return true;
        }
      }
    }

    return false;
  }

  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    if (clickedButton != null) {
      clickedButton.mouseReleased(mouseX, mouseY, state);
      clickedButton = null;
      return true;
    }

    return false;
  }

  @Override
  public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
    for (T button : this.buttons) {
      button.render(matrices, mouseX, mouseY, partialTicks);
    }
  }

  public static int rowsForCount(int columns, int count) {
    return (count - 1) / columns + 1;
  }

  public static int size(int buttonCount, int buttonSize) {
    return buttonSize * buttonCount + SPACING * (buttonCount - 1);
  }
}
