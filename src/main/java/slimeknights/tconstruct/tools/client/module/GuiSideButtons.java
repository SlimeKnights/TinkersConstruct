package slimeknights.tconstruct.tools.client.module;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

import java.io.IOException;

import slimeknights.tconstruct.common.client.gui.GuiModule;
import slimeknights.tconstruct.common.client.gui.GuiMultiModule;

/**
 * A grid of buttons that allows you to select one of them
 */
public class GuiSideButtons extends GuiModule {

  private final int columns;
  private GuiButton clickedButton;

  public int spacing = 4;

  public GuiSideButtons(GuiMultiModule parent, Container container, int columns) {
    this(parent, container, columns, false);
  }

  public GuiSideButtons(GuiMultiModule parent, Container container, int columns, boolean right) {
    super(parent, container, right, false);
    this.columns = columns;
  }

  public void addButton(GuiButton button) {
    int count = buttonList.size();

    int rows = (count-1)/columns + 1;

    this.xSize = button.width*columns + spacing * (columns-1);
    this.ySize = button.height * rows + spacing * (rows - 1);

    int offset = buttonList.size();
    int x = (offset % columns) * (button.width + spacing);
    int y = (offset / columns) * (button.height + spacing);

    button.xPosition = guiLeft + x;
    button.yPosition = guiTop + y;

    if(this.right) {
      button.xPosition += parent.xSize;
    }

    this.buttonList.add(button);
  }

  @Override
  public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
    if (mouseButton == 0)
    {
      for(Object o : this.buttonList) {
        GuiButton guibutton = (GuiButton) o;

        if(guibutton.mousePressed(this.mc, mouseX, mouseY)) {
          this.clickedButton = guibutton;
          guibutton.playPressSound(this.mc.getSoundHandler());
          this.actionPerformed(guibutton);
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean handleMouseReleased(int mouseX, int mouseY, int state) {
    if(clickedButton != null) {
      clickedButton.mouseReleased(mouseX, mouseY);
      clickedButton = null;
      return true;
    }
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    for(Object o : buttonList) {
      ((GuiButton)o).drawButton(this.mc, mouseX, mouseY);
    }
  }
}
