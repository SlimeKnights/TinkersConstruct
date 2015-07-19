package tconstruct.tools.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import java.io.IOException;

// a sub-gui. Mostly the same as a separate GuiContainer, but doesn't do the calls that affect the game as if this were the only gui
public abstract class GuiModule extends GuiContainer {

  // left or right of the parent
  private final boolean right;
  // top or bottom of the parent
  private final boolean bottom;

  public GuiModule(Container container, boolean right, boolean bottom) {
    super(container);

    this.right = right;
    this.bottom = bottom;
  }

  public int guiRight() {
    return guiLeft + xSize;
  }

  public int guiBottom() {
    return guiTop + ySize;
  }

  @Override
  public void initGui() {
    this.guiLeft = (this.width - this.xSize) / 2;
    this.guiTop = (this.height - this.ySize) / 2;
  }

  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    if(right)
      this.guiLeft = parentX + parentSizeX;
    else
      this.guiLeft = parentX - this.xSize;

    if(bottom)
      this.guiTop = parentY + parentSizeY - this.ySize;
    else
      this.guiTop = parentY;
  }

  public void handleDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  public int getYSize() {
    return ySize;
  }

  public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    this.mouseClicked(mouseX, mouseY, mouseButton);
  }
}
