package slimeknights.tconstruct.common;

import slimeknights.mantle.common.IRegisterUtil;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;

/**
 * Just a small helper class that provides some function for cleaner Pulses.
 *
 * Items should be registered during PreInit
 */
public class TinkerPulse implements IRegisterUtil {

  @Override
  public String getModId() {
    return TConstruct.modID;
  }

  protected static boolean isToolsLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_TOOLS_PULSE_ID);
  }

  protected static boolean isSmelteryLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_SMELTERY_PULSE_ID);
  }

  protected static boolean isWorldLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID);
  }

  protected static boolean isGadgetsLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_GADGETS_PULSE_ID);
  }

  /*protected static boolean isChiselPluginLoaded() {
    return TConstruct.pulseManager.isPulseLoaded(Chisel.PulseId);
  }*/
}
