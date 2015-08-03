package tconstruct.tools.client.module;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;

import java.io.IOException;

import tconstruct.common.client.gui.GuiMultiModule;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.TinkerRegistryClient;
import tconstruct.library.client.ToolBuildGuiInfo;
import tconstruct.tools.client.GuiButtonRepair;
import tconstruct.tools.client.GuiButtonItem;
import tconstruct.tools.client.GuiToolStation;

public class GuiButtonsToolStation extends GuiSideButtons {

  public GuiButtonsToolStation(GuiMultiModule parent, Container container) {
    super(parent, container, 4);
  }

  protected int selected = 0;

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;

    addButton(new GuiButtonRepair(index++, -1, -1));

    for(Item item : TinkerRegistry.getToolStationCrafting()) {
      ToolBuildGuiInfo info = TinkerRegistryClient.getToolBuildInfoForTool(item);
      if(info != null) {
        GuiButtonItem button = new GuiButtonItem(index++, -1, -1, info.tool, info);
        addButton(button);

        if(index - 1 == selected) {
          button.pressed = true;
        }
      }
    }

    // activate currently selected/default
    try {
      GuiButton button = (GuiButton) buttonList.get(selected);
      this.actionPerformed(button);
    } catch(IOException e) {
      TinkerRegistryClient.log.error(e);
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    for(Object o : buttonList) {
      ((GuiButtonItem) o).pressed = false;
    }
    ((GuiButtonItem) button).pressed = true;
    selected = button.id;

    ((GuiToolStation) parent).onToolSelection(((GuiButtonItem) button).info);
  }
}
