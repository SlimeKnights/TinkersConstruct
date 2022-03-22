package slimeknights.tconstruct.shared;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.shared.block.OrientableBlock;
import slimeknights.tconstruct.shared.block.SlimesteelBlock;

/**
 * Contains bommon blocks and items used in crafting materials
 */
@SuppressWarnings("unused")
public final class TinkerMaterials extends TinkerModule {
  // ores
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", metalBuilder(MaterialColor.COLOR_BLUE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 3
  public static final MetalItemObject slimesteel     = BLOCKS.registerMetal("slimesteel", () -> new SlimesteelBlock(metalBuilder(MaterialColor.WARPED_WART_BLOCK).noOcclusion()), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject amethystBronze = BLOCKS.registerMetal("amethyst_bronze", metalBuilder(MaterialColor.COLOR_PURPLE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject roseGold       = BLOCKS.registerMetal("rose_gold", metalBuilder(MaterialColor.TERRACOTTA_WHITE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject pigIron        = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(metalBuilder(MaterialColor.COLOR_PINK)), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 4
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", metalBuilder(MaterialColor.COLOR_GREEN), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", metalBuilder(MaterialColor.COLOR_PURPLE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", metalBuilder(MaterialColor.TERRACOTTA_BLUE), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", metalBuilder(MaterialColor.COLOR_BROWN).noOcclusion(), HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);
  public static final ItemObject<Item> copperNugget = ITEMS.register("copper_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", GENERAL_PROPS);
  public static final ItemObject<Item> debrisNugget = ITEMS.register("debris_nugget", TOOLTIP_ITEM);
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", metalBuilder(MaterialColor.COLOR_MAGENTA), HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);

  // non-metal
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> bloodbone = ITEMS.register("bloodbone", TOOLTIP_ITEM);
  public static final ItemObject<Item> blazingBone = ITEMS.register("blazing_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> necroniumBone = ITEMS.register("necronium_bone", TOOLTIP_ITEM);
  public static final FenceBuildingBlockObject nahuatl = BLOCKS.registerFenceBuilding("nahuatl", builder(Material.NETHER_WOOD, MaterialColor.PODZOL, SoundType.WOOD).requiresCorrectToolForDrops().strength(25f, 300f), GENERAL_BLOCK_ITEM);

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegistryEvent<RecipeSerializer<?>> event) {
    CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
  }
}
