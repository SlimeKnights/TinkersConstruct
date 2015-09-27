package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.common.ClientProxy;

import static slimeknights.tconstruct.shared.TinkerCommons.ingotArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotManyullyn;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeBallBlue;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeCrystal;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeCrystalBlue;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetManyullyn;

public class CommonsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    // Nuggets
    registerItemModel(nuggetCobalt,    "NuggetCobalt");
    registerItemModel(nuggetArdite,    "NuggetArdite");
    registerItemModel(nuggetManyullyn, "NuggetManyullyn");

    // Ingots
    registerItemModel(ingotCobalt,    "IngotCobalt");
    registerItemModel(ingotArdite,    "IngotArdite");
    registerItemModel(ingotManyullyn, "IngotManyullyn");

    // Materials
    registerItemModel(matSlimeBallBlue,    "SlimeBallBlue");
    registerItemModel(matSlimeCrystal,     "SlimeCrystal");
    registerItemModel(matSlimeCrystalBlue, "SlimeCrystalBlue");
  }

  @Override
  protected void registerItemModel(ItemStack item, String name) {
    // safety! We call it for everything even if it wasn't registered
    if(item == null) return;

    super.registerItemModel(item, name);
  }
}
