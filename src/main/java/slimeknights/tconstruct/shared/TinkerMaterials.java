package slimeknights.tconstruct.shared;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.shared.block.OrientableBlock;
import slimeknights.tconstruct.shared.block.SlimesteelBlock;

/**
 * Contains bommon blocks and items used in crafting materials
 */
@SuppressWarnings("unused")
public final class TinkerMaterials extends TinkerModule {
  // ores
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", metalBuilder(MapColor.COLOR_BLUE), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  // tier 3
  public static final MetalItemObject slimesteel     = BLOCKS.registerMetal("slimesteel", () -> new SlimesteelBlock(metalBuilder(MapColor.WARPED_WART_BLOCK).noOcclusion()), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject amethystBronze = BLOCKS.registerMetal("amethyst_bronze", metalBuilder(MapColor.COLOR_PURPLE), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject roseGold       = BLOCKS.registerMetal("rose_gold", metalBuilder(MapColor.TERRACOTTA_WHITE), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject pigIron        = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(metalBuilder(MapColor.COLOR_PINK)), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  // tier 4
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", metalBuilder(MapColor.COLOR_GREEN), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", metalBuilder(MapColor.COLOR_PURPLE), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", metalBuilder(MapColor.TERRACOTTA_BLUE), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", metalBuilder(MapColor.COLOR_BROWN).noOcclusion(), BLOCK_ITEM, ITEM_PROPS);
  public static final ItemObject<Item> copperNugget = ITEMS.register("copper_nugget", ITEM_PROPS);
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", ITEM_PROPS);
  public static final ItemObject<Item> debrisNugget = ITEMS.register("debris_nugget", TOOLTIP_ITEM);
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", metalBuilder(MapColor.COLOR_MAGENTA), BLOCK_ITEM, ITEM_PROPS);

  // non-metal
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> venombone = ITEMS.register("venombone", TOOLTIP_ITEM);
  public static final ItemObject<Item> blazingBone = ITEMS.register("blazing_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> necroniumBone = ITEMS.register("necronium_bone", TOOLTIP_ITEM);
  public static final FenceBuildingBlockObject nahuatl = BLOCKS.registerFenceBuilding("nahuatl", builder(MapColor.COLOR_PURPLE, SoundType.WOOD).instrument(NoteBlockInstrument.BASS).requiresCorrectToolForDrops().strength(25f, 300f), BLOCK_ITEM);
  public static final FenceBuildingBlockObject blazewood = BLOCKS.registerFenceBuilding("blazewood", woodBuilder(MapColor.TERRACOTTA_RED).requiresCorrectToolForDrops().strength(25f, 300f).lightLevel(s -> 7), BLOCK_ITEM);

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
    }
  }

  /** Adds all relevant items to the creative tab, called by general tab */
  public static void addTabItems(ItemDisplayParameters itemDisplayParameters, Output output) {
    // non-metals
    // necrotic bone is in world
    output.accept(venombone);
    output.accept(blazewood);
    acceptIfTag(output, necroniumBone, TinkerTags.Items.URANIUM_INGOTS);
    accept(output, nahuatl);
    accept(output, blazewood);

    // natural ores
    output.accept(copperNugget);
    accept(output, cobalt);
    output.accept(debrisNugget);
    // mod alloys
    accept(output, slimesteel);
    accept(output, amethystBronze);
    accept(output, roseGold);
    accept(output, pigIron);
    accept(output, queensSlime);
    accept(output, manyullyn);
    accept(output, hepatizon);
    output.accept(netheriteNugget);
    // future: soulsteel
    // future: knightslime
  }

  /** Adds a metal to the tab */
  private static void accept(Output output, MetalItemObject metal) {
    output.accept(metal.getIngot());
    output.accept(metal.getNugget());
    output.accept(metal.get());
  }
}
