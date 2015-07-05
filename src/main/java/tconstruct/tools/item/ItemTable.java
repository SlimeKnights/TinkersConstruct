package tconstruct.tools.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemTable extends ItemBlock {

  public ItemTable(Block block) {
    super(block);

    this.setMaxDamage(0);
    this.setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }
}
