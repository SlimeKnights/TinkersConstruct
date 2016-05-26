package slimeknights.tconstruct.shared;

import static slimeknights.tconstruct.shared.TinkerCommons.blockDecoGround;
import static slimeknights.tconstruct.shared.TinkerCommons.blockFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.blockMetal;
import static slimeknights.tconstruct.shared.TinkerCommons.blockOre;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSoil;
import static slimeknights.tconstruct.shared.TinkerCommons.book;
import static slimeknights.tconstruct.shared.TinkerCommons.edibles;
import static slimeknights.tconstruct.shared.TinkerCommons.ingots;
import static slimeknights.tconstruct.shared.TinkerCommons.materials;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggets;
import static slimeknights.tconstruct.shared.TinkerCommons.slabFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.slabDecoGround;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsLavawood;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsMudBrick;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.book.TinkerBook;

public class CommonsClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    nuggets.registerItemModels();
    ingots.registerItemModels();
    materials.registerItemModels();
    edibles.registerItemModels();

    registerItemModel(book, 0, "inventory");
    TinkerBook.INSTANCE.equals(null); // instantiate book so it's loaded ._.

    registerItemBlockMeta(blockMetal);
    registerItemBlockMeta(blockSoil);
    registerItemBlockMeta(blockOre);
    registerItemBlockMeta(blockFirewood);
    registerItemBlockMeta(slabFirewood);
    registerItemModel(stairsFirewood);
    registerItemModel(stairsLavawood);

    registerItemBlockMeta(blockDecoGround);
    registerItemBlockMeta(slabDecoGround);
    registerItemModel(stairsMudBrick);
  }

  @Override
  protected void registerItemModel(ItemStack item, String name) {
    // safety! We call it for everything even if it wasn't registered
    if(item == null) return;

    super.registerItemModel(item, name);
  }
}
