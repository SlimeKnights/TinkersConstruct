package slimeknights.tconstruct.tools;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.modID, value= Dist.CLIENT, bus= Bus.MOD)
public class ToolClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    // todo: move to ToolClientProxy
    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModelLoader.INSTANCE);
  }
}
