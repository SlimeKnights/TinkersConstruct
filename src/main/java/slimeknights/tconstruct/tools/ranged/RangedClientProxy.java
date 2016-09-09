package slimeknights.tconstruct.tools.ranged;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.tools.common.client.renderer.RenderArrow;
import slimeknights.tconstruct.tools.common.client.renderer.RenderShuriken;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;

public class RangedClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    // entities
    RenderingRegistry.registerEntityRenderingHandler(EntityShuriken.class, new IRenderFactory<EntityShuriken>() {
      @Override
      public Render<? super EntityShuriken> createRenderFor(RenderManager manager) {
        return new RenderShuriken(manager);
      }
    });
    RenderingRegistry.registerEntityRenderingHandler(EntityArrow.class, new IRenderFactory<EntityArrow>() {
      @Override
      public Render<? super EntityArrow> createRenderFor(RenderManager manager) {
        return new RenderArrow(manager);
      }
    });
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
/*
    // shortbow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.shortBow);
    info.addSlotPosition(32 + 6, 41 + 6); // center bowstring
    info.addSlotPosition(32 + 4, 41 - 18); // top limb
    info.addSlotPosition(32 - 18, 41 + 4); // left limb
    TinkerRegistryClient.addToolBuilding(info);

    // arrow
    info = new ToolBuildGuiInfo(TinkerRangedWeapons.arrow);
    info.addSlotPosition(32, 41); // center
    info.addSlotPosition(32 + 18, 41 - 18); // top right
    info.addSlotPosition(32 - 18, 41 + 18); // bot left
    TinkerRegistryClient.addToolBuilding(info);*/
  }
}
