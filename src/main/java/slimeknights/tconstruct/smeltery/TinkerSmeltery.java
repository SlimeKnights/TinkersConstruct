package slimeknights.tconstruct.smeltery;

import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.Util;

@Pulse(id = TinkerPulseIds.TINKER_SMELTERY_PULSE_ID, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {
  public static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_SMELTERY_PULSE_ID);
}
