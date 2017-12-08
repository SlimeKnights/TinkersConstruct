package slimeknights.tconstruct.tools.harvest;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

import static slimeknights.tconstruct.tools.harvest.TinkerHarvestTools.*;

public class HarvestClientProxy extends ClientProxy {

  @Override
  public void init() {
    super.init();
    registerToolBuildInfo();
  }

  private void registerToolBuildInfo() {
    // pickaxe
    ToolBuildGuiInfo info;

    info = new ToolBuildGuiInfo(pickaxe);
    info.addSlotPosition(33 - 18, 42 + 18); // rod
    info.addSlotPosition(33 + 20, 42 - 20); // pick head
    info.addSlotPosition(33, 42); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // shovel
    info = new ToolBuildGuiInfo(shovel);
    info.addSlotPosition(33, 42); // rod
    info.addSlotPosition(33 + 18, 42 - 18); // shovel head
    info.addSlotPosition(33 - 20, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // hatchet
    info = new ToolBuildGuiInfo(hatchet);
    info.addSlotPosition(33 - 11, 42 + 11); // rod
    info.addSlotPosition(33 - 2, 42 - 20); // head
    info.addSlotPosition(33 + 18, 42 - 8); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // mattock
    info = new ToolBuildGuiInfo(mattock);
    info.addSlotPosition(33 - 11, 42 + 11); // rod
    info.addSlotPosition(33 - 2, 42 - 20); // axe head
    info.addSlotPosition(33 + 18, 42 - 8); // shovel head
    TinkerRegistryClient.addToolBuilding(info);

    // kama
    info = new ToolBuildGuiInfo(kama);
    info.addSlotPosition(33 - 11, 42 + 11); // rod
    info.addSlotPosition(33 - 2, 42 - 20); // head
    info.addSlotPosition(33 + 18, 42 - 8); // binding
    TinkerRegistryClient.addToolBuilding(info);


    // hammer
    info = new ToolBuildGuiInfo(hammer);
    info.addSlotPosition(33 - 10 - 2, 42 + 10); // handle
    info.addSlotPosition(33 + 13 - 2, 42 - 13); // head
    info.addSlotPosition(33 + 10 + 16 - 2, 42 - 10 + 16); // plate 1
    info.addSlotPosition(33 + 10 - 16 - 2, 42 - 10 - 16); // plate 2
    TinkerRegistryClient.addToolBuilding(info);

    // excavator
    info = new ToolBuildGuiInfo(excavator);
    info.addSlotPosition(33 - 10 + 2, 42 + 4); // handle
    info.addSlotPosition(33 + 12, 42 - 16); // head
    info.addSlotPosition(33 - 8, 42 - 16); // plate
    info.addSlotPosition(33 - 10 - 16, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // lumberaxe
    info = new ToolBuildGuiInfo(lumberAxe);
    info.addSlotPosition(33 + 6 - 10 + 3, 42 + 4); // handle
    info.addSlotPosition(33 + 6 - 6, 42 - 20); // head
    info.addSlotPosition(33 + 6 + 14, 42 - 4); // plate
    info.addSlotPosition(33 + 6 - 10 - 16, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // scythe
    info = new ToolBuildGuiInfo(scythe);
    info.addSlotPosition(33-16, 42+12); // handle
    info.addSlotPosition(33+3, 42-23); // head
    info.addSlotPosition(33+7+16, 42-23+10); // binding
    info.addSlotPosition(33-12+16, 42+5); // handle2
    TinkerRegistryClient.addToolBuilding(info);
  }
}
