package slimeknights.tconstruct.tools.ranged;

import net.minecraftforge.fml.client.registry.RenderingRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.tools.common.client.renderer.RenderArrow;
import slimeknights.tconstruct.tools.common.client.renderer.RenderBolt;
import slimeknights.tconstruct.tools.common.client.renderer.RenderShuriken;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.common.entity.EntityBolt;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;

public class RangedClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    super.registerModels();

    // entities
    RenderingRegistry.registerEntityRenderingHandler(EntityShuriken.class, RenderShuriken::new);
    RenderingRegistry.registerEntityRenderingHandler(EntityArrow.class, RenderArrow::new);
    RenderingRegistry.registerEntityRenderingHandler(EntityBolt.class, RenderBolt::new);
  }

  @Override
  public void init() {
    super.init();

    registerToolBuildInfo();
  }

  private void registerToolBuildInfo() {
    ToolBuildGuiInfo info;

    // shuriken
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.shuriken);
    info.addSlotPosition(32 - 12, 41 - 12); // top left
    info.addSlotPosition(32 + 12, 41 - 12); // top right
    info.addSlotPosition(32 + 12, 41 + 12); // bot left
    info.addSlotPosition(32 - 12, 41 + 12); // bot right
    TinkerRegistryClient.addToolBuilding(info);

    // shortbow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.shortBow);
    info.addSlotPosition(32 + 4, 41 - 18); // top limb
    info.addSlotPosition(32 - 18, 41 + 4); // left limb
    info.addSlotPosition(32 + 6, 41 + 6); // center bowstring
    TinkerRegistryClient.addToolBuilding(info);

    // longbow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.longBow);
    info.addSlotPosition(32 + 12, 41 - 22); // top limb
    info.addSlotPosition(32 - 22, 41 + 12); // left limb
    info.addSlotPosition(32 - 15, 41 - 15); // grip
    info.addSlotPosition(32 + 6, 41 + 6); // center bowstring
    TinkerRegistryClient.addToolBuilding(info);

    // arrow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.arrow);
    info.addSlotPosition(32, 41); // center
    info.addSlotPosition(32 + 18, 41 - 18); // top right
    info.addSlotPosition(32 - 18, 41 + 18); // bot left
    TinkerRegistryClient.addToolBuilding(info);

    // crossbow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.crossBow);
    info.addSlotPosition(32 + 6, 41 + 6); // body
    info.addSlotPosition(32 + 12, 41 - 22); // limb
    info.addSlotPosition(32 - 18, 41 - 18); // grip
    info.addSlotPosition(32 - 14, 41 + 10); // bowstring
    TinkerRegistryClient.addToolBuilding(info);

    // bolt
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.bolt);
    info.addSlotPosition(32 + 8, 41 - 4); // boltcore
    info.addSlotPosition(32 - 12, 41 + 12); // fletching
    TinkerRegistryClient.addToolBuilding(info);
  }
}
