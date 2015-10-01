package slimeknights.tconstruct.debug;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import mantle.pulsar.pulse.Pulse;

@Pulse(id=TinkerDebug.PulseId, description = "Debug utilities")
public class TinkerDebug {
  public static final String PulseId = "TinkerDebug";

  @Subscribe
  public void serverStart(FMLServerStartingEvent event) {
    event.registerServerCommand(new LocalizationCheckCommand());
    event.registerServerCommand(new DumpMaterialTest());
  }
}
