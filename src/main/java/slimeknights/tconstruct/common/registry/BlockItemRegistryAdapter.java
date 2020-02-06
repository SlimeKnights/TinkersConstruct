package slimeknights.tconstruct.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.BlockTooltipItem;

import javax.annotation.Nullable;

// MANTLE
// TODO: move to mantle
/**
 * Provides utility registration methods when registering itemblocks.
 */
public class BlockItemRegistryAdapter extends BaseRegistryAdapter<Item> {

  private final ItemGroup defaultItemGroup;

  public BlockItemRegistryAdapter(IForgeRegistry<Item> registry, @Nullable ItemGroup defaultItemGroup) {
    super(registry);
    this.defaultItemGroup = defaultItemGroup;
  }

  /**
   * Registers a generic item block for a block.
   * If your block does not have its own item, just use this method to make it available as an item.
   * The item uses the same name as the block for registration.
   * The registered BlockItem has tooltip support by default, see {@link BlockTooltipItem}
   * It will be added to the creative itemgroup passed in in the constructor. If you want a different one, use the method with a ItemGroup parameter.
   * @param block The block you want to have an item for
   * @return The registered item for the block
   */
  public BlockItem registerBlockItem(Block block) {
    return registerBlockItem(block, defaultItemGroup);
  }

  /**
   * Same as the variant without ItemGroup, but registers it for the given itemgroup.
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
