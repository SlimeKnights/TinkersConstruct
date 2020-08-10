package slimeknights.tconstruct.tables.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import slimeknights.mantle.item.RetexturedBlockItem;

public class RetexturedTableBlockItem extends RetexturedBlockItem {
  public RetexturedTableBlockItem(Block block, ITag<Item> textureTag, Properties builder) {
    super(block, textureTag, builder);
  }
}
