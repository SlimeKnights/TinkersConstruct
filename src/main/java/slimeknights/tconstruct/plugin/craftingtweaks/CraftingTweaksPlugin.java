package slimeknights.tconstruct.plugin.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;

public class CraftingTweaksPlugin {

  public static void onConstruct() {
    CraftingTweaksAPI.registerCraftingGridProvider(new TinkersCraftingGridProvider());
  }

}
