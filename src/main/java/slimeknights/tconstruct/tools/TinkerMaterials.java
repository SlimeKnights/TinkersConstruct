package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;

/**
 * Contains blocks and items used in crafting materials, and the materials themselves
 */
@SuppressWarnings("unused")
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
  public static final ItemObject<Block> bronzeBlock = BLOCKS.register("bronze_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> ravagerSteelBlock = BLOCKS.register("ravager_steel_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> soulSteelBlock = BLOCKS.register("soul_steel_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> heptazionBlock = BLOCKS.register("heptazion_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> slimeBronzeBlock = BLOCKS.register("slime_bronze_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> knightMetalBlock = BLOCKS.register("knight_metal_block", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);

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
  public static final ItemObject<Item> bronzeNugget = ITEMS.register("bronze_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> bronzeIngot = ITEMS.register("bronze_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> ravagerSteelNugget = ITEMS.register("ravager_steel_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> ravagerSteelIngot = ITEMS.register("ravager_steel_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> soulSteelNugget = ITEMS.register("soul_steel_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> soulSteelIngot = ITEMS.register("soul_steel_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> heptazionNugget = ITEMS.register("heptazion_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> heptazionIngot = ITEMS.register("heptazion_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> slimeBronzeNugget = ITEMS.register("slime_bronze_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> slimeBronzeIngot = ITEMS.register("slime_bronze_ingot", GENERAL_PROPS);
  public static final ItemObject<Item> knightMetalNugget = ITEMS.register("knight_metal_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> knightMetalIngot = ITEMS.register("knight_metal_ingot", GENERAL_PROPS);

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegistryEvent<IRecipeSerializer<?>> event) {
    CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
  }
}
