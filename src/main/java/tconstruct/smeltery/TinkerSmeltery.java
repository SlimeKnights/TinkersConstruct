package tconstruct.smeltery;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.CommonProxy;
import tconstruct.TinkerPulse;
import tconstruct.library.Util;
import tconstruct.tools.item.Pattern;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "tconstruct.smeltery.SmelteryClientProxy", serverSide = "tconstruct.CommonProxy")
  public static CommonProxy proxy;

  public static Pattern cast;

  // currently only a dummy-class

  // PRE-INITIALIZATION
  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    cast = registerItem(new Pattern(), "Cast");

    proxy.preInit();
  }

  // INITIALIZATION
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }

  // POST-INITIALIZATION
  @Handler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();
  }
}
