package slimeknights.tconstruct.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.entity.ToolEntities;
import slimeknights.tconstruct.entity.WorldEntities;
import slimeknights.tconstruct.library.client.renderer.BlueSlimeRenderer;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.slime.BlueColorReloadListener;
import slimeknights.tconstruct.world.client.slime.OrangeColorReloadListener;
import slimeknights.tconstruct.world.client.slime.PurpleColorReloadListener;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents {

  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  static {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft != null) {
      IResourceManager iManager = Minecraft.getInstance().getResourceManager();
      if (iManager instanceof IReloadableResourceManager) {
        IReloadableResourceManager reloadable = (IReloadableResourceManager)iManager;
        reloadable.addReloadListener(new BlueColorReloadListener());
        reloadable.addReloadListener(new PurpleColorReloadListener());
        reloadable.addReloadListener(new OrangeColorReloadListener());
      }
    }
  }

  @SubscribeEvent
  public static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(WorldEntities.blue_slime_entity, BlueSlimeRenderer.BLUE_SLIME_FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(ToolEntities.indestructible_item, manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
  }
}
