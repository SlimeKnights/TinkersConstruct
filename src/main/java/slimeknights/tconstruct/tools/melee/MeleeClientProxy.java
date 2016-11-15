package slimeknights.tconstruct.tools.melee;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

public class MeleeClientProxy extends ClientProxy {

  @Override
  public void init() {
    super.init();

    registerToolBuildInfo();
  }

  private void registerToolBuildInfo() {
    ToolBuildGuiInfo info;

    // broadsword
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.broadSword);
    info.addSlotPosition(33 - 20 - 1, 42 + 20); // handle
    info.addSlotPosition(33 + 20 - 5, 42 - 20 + 4); // blade
    info.addSlotPosition(33 - 2 - 1, 42 + 2); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // longsword
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.longSword);
    info.addSlotPosition(33 - 20 - 1, 42 + 20); // handle
    info.addSlotPosition(33 + 20 - 5, 42 - 20 + 4); // blade
    info.addSlotPosition(33 - 2 - 1, 42 + 2); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // rapier
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.rapier);
    info.addSlotPosition(33 + 20 - 1, 42 + 20); // handle
    info.addSlotPosition(33 - 20 + 5, 42 - 20 + 4); // blade
    info.addSlotPosition(33 - 2 + 1, 42 + 2); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // dagger

    // battlesign
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.battleSign);
    info.addSlotPosition(33 - 6, 42 + 18); // handle
    info.addSlotPosition(33 - 6, 42 - 8); // sign
    TinkerRegistryClient.addToolBuilding(info);

    // frypan
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.fryPan);
    info.addSlotPosition(33 - 20 - 1, 42 + 20); // handle
    info.addSlotPosition(33 + 2 - 1, 42 - 6); // pan
    TinkerRegistryClient.addToolBuilding(info);


    // cleaver
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.cleaver);
    info.addSlotPosition(33 - 10 - 14, 42 + 10 + 12); // handle
    info.addSlotPosition(33 - 8, 42 - 10 + 4); // head
    info.addSlotPosition(33 + 14, 42 - 10 - 2); // plate/shield
    info.addSlotPosition(33 + 10 - 10, 42 + 10 + 6); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // battleaxe
    /*info = new ToolBuildGuiInfo(TinkerMeleeWeapons.battleAxe);
    info.addSlotPosition(33-14, 42+10); // handle
    info.addSlotPosition(33+10-20, 42-10-10); // head 1
    info.addSlotPosition(33+10+6, 42-10+16); // head 2
    info.addSlotPosition(33+9, 42-13); // binding
    TinkerRegistryClient.addToolBuilding(info);*/
  }
}
