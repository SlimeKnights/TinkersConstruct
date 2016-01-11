package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.common.ClientProxy;

import static slimeknights.tconstruct.shared.TinkerCommons.edibles;
import static slimeknights.tconstruct.shared.TinkerCommons.ingots;
import static slimeknights.tconstruct.shared.TinkerCommons.materials;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggets;

public class CommonsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    nuggets.registerItemModels("nugget_");
    ingots.registerItemModels("ingot_");
    materials.registerItemModels("");
    edibles.registerItemModels("");
  }

  @Override
  protected void registerItemModel(ItemStack item, String name) {
    // safety! We call it for everything even if it wasn't registered
    if(item == null) return;

    super.registerItemModel(item, name);
  }
}
