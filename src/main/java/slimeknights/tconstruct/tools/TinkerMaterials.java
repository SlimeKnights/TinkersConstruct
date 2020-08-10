package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;

/**
 * Contains blocks and items used in crafting materials, and the materials themselves
 */
public final class TinkerMaterials extends TinkerModule {
  /*
   * Blocks
   */

  // metal
  // TODO: which tinker materials do we want?
  public static final ItemObject<Block> cobaltBlock = BLOCKS.register("cobalt_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> arditeBlock = BLOCKS.register("ardite_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> manyullynBlock = BLOCKS.register("manyullyn_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> knightSlimeBlock = BLOCKS.register("knightslime_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> pigironBlock = BLOCKS.register("pigiron_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> copperBlock = BLOCKS.register("copper_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> roseGoldBlock = BLOCKS.register("rose_gold_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  // metals
  public static final ItemObject<Item> cobaltNugget = ITEMS.register("cobalt_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> cobaltIngot = ITEMS.register("cobalt_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> arditeNugget = ITEMS.register("ardite_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> arditeIngot = ITEMS.register("ardite_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> manyullynNugget = ITEMS.register("manyullyn_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> manyullynIngot = ITEMS.register("manyullyn_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> pigironNugget = ITEMS.register("pigiron_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> pigironIngot = ITEMS.register("pigiron_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> knightslimeNugget = ITEMS.register("knightslime_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> knightslimeIngot = ITEMS.register("knightslime_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> copperNugget = ITEMS.register("copper_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> copperIngot = ITEMS.register("copper_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> roseGoldNugget = ITEMS.register("rose_gold_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> roseGoldIngot = ITEMS.register("rose_gold_ingot", GENERAL_PROPS);
}
