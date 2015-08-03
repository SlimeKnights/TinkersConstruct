package tconstruct.tools.client.module;

import net.minecraft.inventory.Container;
import net.minecraft.item.Item;

import tconstruct.common.client.gui.GuiMultiModule;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.TinkerRegistryClient;
import tconstruct.library.client.ToolBuildGuiInfo;
import tconstruct.tools.client.GuiButtonTool;

public class GuiButtonsToolStation extends GuiSideButtons {

  public GuiButtonsToolStation(GuiMultiModule parent, Container container) {
    super(parent, container, 4);
  }

  @Override
  public void initGui() {
    super.initGui();

    for(Item item : TinkerRegistry.getToolStationCrafting()) {
      ToolBuildGuiInfo info = TinkerRegistryClient.getToolBuildInfoForTool(item);
      if(info != null) {
        addButton(new GuiButtonTool(0, -1, -1, info.tool, info));
      }
    }
  }
}
