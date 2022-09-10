package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import org.apache.commons.compress.utils.Lists;
import slimeknights.mantle.client.screen.MultiModuleScreen;

import java.util.List;

public class SideButtonsWidget<T extends Button> implements Widget, GuiEventListener {

  protected final MultiModuleScreen<?> parent;

  // left or right of the parent
  protected final boolean right;

  public int leftPos;
  public int topPos;
  public int imageWidth;
  public int imageHeight;
  public int xOffset;
  public int yOffset;

  private final int columns;
  protected final List<T> buttons = Lists.newArrayList();
  private Button clickedButton;
  protected int buttonCount = 0;

  public int spacing = 4;

  public SideButtonsWidget(MultiModuleScreen<?> parent, int columns) {
    this(parent, columns, false);
  }

  public SideButtonsWidget(MultiModuleScreen<?> parent, int columns, boolean right) {
    this.parent = parent;
    this.right = right;

    this.columns = columns;
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

  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    if (this.right) {
      this.leftPos = parentX + parentSizeX;
    } else {
      this.leftPos = parentX - this.imageWidth;
    }

    this.topPos = parentY;

    this.leftPos += this.xOffset;
    this.topPos += this.yOffset;
  }

  public void addSideButton(T button) {
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

    this.buttons.add(button);
    this.buttonCount++;
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
}
