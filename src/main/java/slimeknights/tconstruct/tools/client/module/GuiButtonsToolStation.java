package slimeknights.tconstruct.tools.client.module;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;

import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.tools.client.GuiButtonItem;
import slimeknights.tconstruct.tools.client.GuiButtonRepair;
import slimeknights.tconstruct.tools.client.GuiTinkerStation;
import slimeknights.tconstruct.tools.client.GuiToolStation;

public class GuiButtonsToolStation extends GuiSideButtons {

  protected final GuiToolStation parent;

  public GuiButtonsToolStation(GuiToolStation parent, Container container) {
    super(parent, container, GuiToolStation.Column_Count);

    this.parent = parent;
  }

  protected int selected = 0;

  private int style = 0;


  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;

    {
      GuiButtonItem<ToolBuildGuiInfo> button = new GuiButtonRepair(index++, -1, -1);
      shiftButton(button, 0, -18 * style);
      addButton(button);
    }

    for(Item item : parent.getBuildableItems()) {
      ToolBuildGuiInfo info = TinkerRegistryClient.getToolBuildInfoForTool(item);
      if(info != null) {
        GuiButtonItem<ToolBuildGuiInfo> button = new GuiButtonItem<ToolBuildGuiInfo>(index++, -1, -1, info.tool, info);
        shiftButton(button, 0, -18 * style);
        addButton(button);

        if(index - 1 == selected) {
          button.pressed = true;
        }
      }
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // activate currently selected/default
    parent.updateGUI();
  }

  public void setSelectedButtonByTool(ItemStack stack) {
    for(Object o : buttonList) {
      @SuppressWarnings("unchecked")
      GuiButtonItem<ToolBuildGuiInfo> btn = (GuiButtonItem<ToolBuildGuiInfo>) o;
      btn.pressed = ItemStack.areItemStacksEqual(btn.data.tool, stack);
    }
  }


  @Override
  @SuppressWarnings("unchecked")
  protected void actionPerformed(GuiButton button) throws IOException {
    for(Object o : buttonList) {
      ((GuiButtonItem<ToolBuildGuiInfo>) o).pressed = false;
    }
    ((GuiButtonItem<ToolBuildGuiInfo>) button).pressed = true;
    selected = button.id;

    parent.onToolSelection(((GuiButtonItem<ToolBuildGuiInfo>) button).data);
  }

  @SuppressWarnings("unchecked")
  public void wood() {
    for(Object o : buttonList) {
      shiftButton((GuiButtonItem<ToolBuildGuiInfo>) o, 0, -36);
    }

    style = 2;
  }

  @SuppressWarnings("unchecked")
  public void metal() {
    for(Object o : buttonList) {
      shiftButton((GuiButtonItem<ToolBuildGuiInfo>) o, 0, -18);
    }

    style = 1;
  }

  protected void shiftButton(GuiButtonItem<ToolBuildGuiInfo> button, int xd, int yd) {
    button.setGraphics(GuiTinkerStation.ICON_Button.shift(xd, yd),
                       GuiTinkerStation.ICON_ButtonHover.shift(xd, yd),
                       GuiTinkerStation.ICON_ButtonPressed.shift(xd, yd),
                       GuiTinkerStation.ICONS);
  }
}
