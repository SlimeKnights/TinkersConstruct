package slimeknights.tconstruct.plugin;

import com.illusivesoulworks.diet.common.capability.DietCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

/** Plugin to enable compat with the Diet mod */
public class DietPlugin {
  /** Call on mod construct to enable the compat */
  public static void onConstruct() {
    ModifierUtil.foodConsumer = (player, stack, hunger, saturation) -> {
      DietCapability.get(player).ifPresent(cap -> cap.consume(stack, hunger, saturation));
    };
  }
}
