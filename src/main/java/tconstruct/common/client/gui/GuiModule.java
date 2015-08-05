package tconstruct.common.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

// a sub-gui. Mostly the same as a separate GuiContainer, but doesn't do the calls that affect the game as if this were the only gui
@SideOnly(Side.CLIENT)
public abstract class GuiModule extends GuiContainer {
  protected final GuiMultiModule parent;

  // left or right of the parent
  protected final boolean right;
  // top or bottom of the parent
  protected final boolean bottom;

  public GuiModule(GuiMultiModule parent, Container container, boolean right, boolean bottom) {
    super(container);

    this.parent = parent;
    this.right = right;
    this.bottom = bottom;
  }

  public int guiRight() {
    return guiLeft + xSize;
  }

  public int guiBottom() {
    return guiTop + ySize;
  }

  public int getYSize() {
    return ySize;
  }

  public void setYSize(int ySize) {
    this.ySize = ySize;
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

  public boolean shouldDrawSlot(Slot slot) {
    return true;
  }
/*
  public void updateDragged(boolean dragSplitting, Set draggedSlots) {
    this.dragSplitting = dragSplitting;
    this.dragSplittingSlots.clear();
    for(Object o : draggedSlots) {
      if(o instanceof SlotWrapper)
        this.dragSplittingSlots.add(((SlotWrapper) o).parent);
      else
        this.dragSplittingSlots.add(o);
    }
  }
*/
  public void handleDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  public void handleDrawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }


  /**
   * Custom mouse click handling.
   * @return True to prevent the main container handling the mouseclick
   */
  public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    return false;
  }

  /**
   * Custom mouse click handling.
   * @return True to prevent the main container handling the mouseclick
   */
  public boolean handleMouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    return false;
  }

  /**
   * Custom mouse click handling.
   * @return True to prevent the main container handling the mouseclick
   */
  public boolean handleMouseReleased(int mouseX, int mouseY, int state) {
    return false;
  }

}
