package tconstruct.smeltery;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Pulse;
import tconstruct.TinkerPulse;
import tconstruct.library.Util;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  static final Logger log = Util.getLogger(PulseId);

  // currently only a dummy-class
}
