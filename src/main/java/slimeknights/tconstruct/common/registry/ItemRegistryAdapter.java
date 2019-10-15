package slimeknights.tconstruct.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

import slimeknights.mantle.item.BlockTooltipItem;

// MANTLE
// TODO: move to mantle
/**
 * Provides utility registration methods when registering itemblocks.
 */
public class ItemRegistryAdapter extends BaseRegistryAdapter<Item> {

  public ItemRegistryAdapter(IForgeRegistry<Item> registry) {
    super(registry);
  }

  /**
   * Registers a generic item block for a block.
   * If your block does not have its own item, just use this method to make it available as an item.
   * The item uses the same name as the block for registration.
   * The registered BlockItem has tooltip support by default, see {@link BlockTooltipItem}
   * @param block The block you want to have an item for
   * @param itemGroup The creative tab the item shall be available in.
   *                  Can be null for no creative tab.
   * @return The registered item for the block
   */
  public BlockItem registerBlockItem(Block block, @Nullable ItemGroup itemGroup) {
    Item.Properties itemProperties = new Item.Properties();
    if(itemGroup != null) {
      itemProperties.group(itemGroup);
    }
    BlockItem itemBlock = new BlockTooltipItem(block, itemProperties);
    return register(itemBlock, block.getRegistryName());
  }

  /**
   * Shortcut method to register your own BlockItem, registering with the same name as the block it represents.
   *
   * @param blockItem Item block instance to register
   * @return Registered item block, should be the same as teh one passed in.
   */
  public <T extends BlockItem> T registerBlockItem(T blockItem) {
    return register(blockItem, blockItem.getBlock().getRegistryName());
  }
}
