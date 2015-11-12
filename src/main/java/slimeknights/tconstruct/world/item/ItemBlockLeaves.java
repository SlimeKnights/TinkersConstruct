package slimeknights.tconstruct.world.item;

import net.minecraft.block.Block;

import slimeknights.mantle.item.ItemBlockMeta;

public class ItemBlockLeaves extends ItemBlockMeta {

  public ItemBlockLeaves(Block block) {
    super(block);
  }

  @Override
  public int getMetadata(int damage) {
    return damage | 4; // this sets the CHECK_DECAY bit which means the leaves placed by this item don't decay
  }
}
