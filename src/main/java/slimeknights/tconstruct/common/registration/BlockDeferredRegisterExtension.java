package slimeknights.tconstruct.common.registration;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegisterExtension extends BlockDeferredRegister {
  public BlockDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /**
   * Creates a new metal item object
   * @param name           Metal name
   * @param tagName        Name to use for tags for this block
   * @param blockSupplier  Supplier for the block
   * @param blockItem      Block item
   * @param itemProps      Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, String tagName, Supplier<Block> blockSupplier, Function<Block, BlockItem> blockItem, Item.Settings itemProps) {
    ItemObject<Block> block = register(name + "_block", blockSupplier, blockItem);
    Supplier<Item> itemSupplier = () -> new Item(itemProps);
    Item ingot = Registry.register(itemRegister, name + "_ingot", itemSupplier.get());
    Item nugget = Registry.register(itemRegister, name + "_nugget", itemSupplier.get());
    return new MetalItemObject(tagName, block, () -> ingot, () -> nugget);
  }

  /**
   * Creates a new metal item object
   * @param name           Metal name
   * @param blockSupplier  Supplier for the block
   * @param blockItem      Block item
   * @param itemProps      Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, Supplier<Block> blockSupplier, Function<Block, BlockItem> blockItem, Item.Settings itemProps) {
    return registerMetal(name, name, blockSupplier, blockItem, itemProps);
  }

  /**
   * Creates a new metal item object
   * @param name        Metal name
   * @param tagName     Name to use for tags for this block
   * @param blockProps  Properties for the block
   * @param blockItem   Block item
   * @param itemProps   Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, String tagName, AbstractBlock.Settings blockProps, Function<Block, BlockItem> blockItem, Item.Settings itemProps) {
    return registerMetal(name, tagName, () -> new Block(blockProps), blockItem, itemProps);
  }

  /**
   * Creates a new metal item object
   * @param name        Metal name
   * @param blockProps  Properties for the block
   * @param blockItem   Block item
   * @param itemProps   Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, AbstractBlock.Settings blockProps, Function<Block, BlockItem> blockItem, Item.Settings itemProps) {
    return registerMetal(name, name, blockProps, blockItem, itemProps);
  }
}
