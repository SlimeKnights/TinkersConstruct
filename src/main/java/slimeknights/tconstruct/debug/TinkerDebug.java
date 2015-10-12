package slimeknights.tconstruct.debug;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import mantle.pulsar.pulse.Pulse;

// todo: deactivate by default
@Pulse(id=TinkerDebug.PulseId, description = "Debug utilities")
public class TinkerDebug {
  public static final String PulseId = "TinkerDebug";

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      ClientCommandHandler.instance.registerCommand(new ReloadResources());
    }
  }

  @Subscribe
  public void serverStart(FMLServerStartingEvent event) {
    event.registerServerCommand(new LocalizationCheckCommand());
    event.registerServerCommand(new DumpMaterialTest());
    event.registerServerCommand(new FindBestTool());
    event.registerServerCommand(new DamageTool());
  }
}
