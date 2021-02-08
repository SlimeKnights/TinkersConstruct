package slimeknights.tconstruct.common.registration;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.function.Function;

public class BlockDeferredRegisterExtension extends BlockDeferredRegister {
  public BlockDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /**
   * Creates a new metal item object
   * @param name        Metal name
   * @param blockProps  Properties for the block
   * @param blockItem   Block item
   * @param itemProps   Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, AbstractBlock.Properties blockProps, Function<Block,? extends BlockItem> blockItem, Item.Properties itemProps) {
    ItemObject<Block> block = register(name + "_block", blockProps, blockItem);
    RegistryObject<Item> ingot = itemRegister.register(name + "_ingot", () -> new Item(itemProps));
    RegistryObject<Item> nugget = itemRegister.register(name + "_nugget", () -> new Item(itemProps));
    return new MetalItemObject(name, block, ingot, nugget);
  }
}
