package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.book.TinkerBook;

import static slimeknights.tconstruct.shared.TinkerCommons.blockFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.blockMetal;
import static slimeknights.tconstruct.shared.TinkerCommons.blockOre;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSoil;
import static slimeknights.tconstruct.shared.TinkerCommons.book;
import static slimeknights.tconstruct.shared.TinkerCommons.edibles;
import static slimeknights.tconstruct.shared.TinkerCommons.ingots;
import static slimeknights.tconstruct.shared.TinkerCommons.materials;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggets;

public class CommonsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    nuggets.registerItemModels();
    ingots.registerItemModels();
    materials.registerItemModels();
    edibles.registerItemModels();

    registerItemModel(book, 0, "inventory");
    TinkerBook.INSTANCE.equals(null);

    registerItemBlockMeta(blockMetal);
    registerItemBlockMeta(blockSoil);
    registerItemBlockMeta(blockOre);
    registerItemBlockMeta(blockFirewood);
  }

  @Override
  protected void registerItemModel(ItemStack item, String name) {
    // safety! We call it for everything even if it wasn't registered
    if(item == null) return;

    super.registerItemModel(item, name);
  }
}
