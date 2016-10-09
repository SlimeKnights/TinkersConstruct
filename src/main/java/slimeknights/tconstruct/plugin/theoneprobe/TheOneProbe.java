package slimeknights.tconstruct.plugin.theoneprobe;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = TheOneProbe.PulseId, modsRequired = TheOneProbe.modid, defaultEnable = true)
public class TheOneProbe {
  public static final String modid = "theoneprobe";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    FMLInterModComms.sendFunctionMessage(modid, "getTheOneProbe", "slimeknights.tconstruct.plugin.theoneprobe.GetTheOneProbe");
  }
}
