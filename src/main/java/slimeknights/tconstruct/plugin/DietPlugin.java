package slimeknights.tconstruct.plugin;

import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import top.theillusivec4.diet.api.DietCapability;

/** Plugin to enable compat with the Diet mod */
public class DietPlugin {
  /** Call on mod construct to enable the compat */
  public static void onConstruct() {
    ModifierUtil.foodConsumer = (player, stack, hunger, saturation) -> {
      DietCapability.get(player).ifPresent(cap -> cap.consume(stack, hunger, saturation));
    };
  }
}
