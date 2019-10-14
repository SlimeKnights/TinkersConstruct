package slimeknights.tconstruct.common;

import javax.annotation.Nonnull;

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

  /**
   * This is to initialize fields that are injected via {@link net.minecraftforge.registries.ObjectHolder} annotation.
   * It just returns null, but it removes all the static code analysis varnings regarding this.
   * @return null.
   */
  @Nonnull
  @SuppressWarnings("ConstantConditions")
  protected static <T> T injected() {
    return null;
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
