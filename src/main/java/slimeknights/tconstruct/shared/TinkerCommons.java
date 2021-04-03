package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.conditions.ConfigEnabledCondition;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.recipe.BlockOrEntityCondition;
import slimeknights.tconstruct.common.recipe.IngredientIntersection;
import slimeknights.tconstruct.common.recipe.IngredientWithout;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.ReplaceItemLootModifier;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.ClearStainedGlassPaneBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.shared.data.CommonRecipeProvider;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_commons");

  /*
   * Blocks
   */
  public static final RegistryObject<GlowBlock> glow = BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.CLOTH).hardnessAndResistance(0.0F).setLightLevel(s -> 14).notSolid()));
  public static final BuildingBlockObject mudBricks = BLOCKS.registerBuilding("mud_bricks", builder(Material.EARTH, ToolType.SHOVEL, SoundType.GROUND).setRequiresTool().hardnessAndResistance(2.0F), GENERAL_BLOCK_ITEM);
  // clay
  // TODO: moving to natura
  private static final Block.Properties DRIED_CLAY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).setRequiresTool().hardnessAndResistance(1.5F, 20.0F);
  public static final BuildingBlockObject driedClay = BLOCKS.registerBuilding("dried_clay", DRIED_CLAY, HIDDEN_BLOCK_ITEM);
  public static final BuildingBlockObject driedClayBricks = BLOCKS.registerBuilding("dried_clay_bricks", DRIED_CLAY, HIDDEN_BLOCK_ITEM);
  // glass
  public static final ItemObject<GlassBlock> clearGlass = BLOCKS.register("clear_glass", () -> new GlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> clearGlassPane = BLOCKS.register("clear_glass_pane", () -> new ClearGlassPaneBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassBlock> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassPaneBlock> clearStainedGlassPane = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass_pane", (color) -> new ClearStainedGlassPaneBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  public static final ItemObject<GlassBlock> soulGlass = BLOCKS.register("soul_glass", () -> new GlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> soulGlassPane = BLOCKS.register("soul_glass_pane", () -> new ClearGlassPaneBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  // wood
  private static final Block.Properties WOOD = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).setRequiresTool().hardnessAndResistance(2.0F, 7.0F).setLightLevel(s -> 7);
  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", WOOD, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject blazewood = BLOCKS.registerBuilding("blazewood", WOOD, GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TAB_GENERAL));
  public static final ItemObject<TinkerBookItem> book = ITEMS.register("book", () -> new TinkerBookItem(new Item.Properties().group(TAB_GENERAL).maxStackSize(1)));
  // TODO: move to natura
  public static final ItemObject<Item> driedBrick = ITEMS.register("dried_brick", HIDDEN_PROPS);

  /*
   * Misc
   */
  public static final RegistryObject<ReplaceItemLootModifier.Serializer> replaceItemLootModifier = GLOBAL_LOOT_MODIFIERS.register("replace_item", ReplaceItemLootModifier.Serializer::new);

  /* Loot conditions */
  public static LootConditionType lootConfig;
  public static LootConditionType lootBlockOrEntity;

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeType, Item> slimeball = new EnumObject.Builder<SlimeType, Item>(SlimeType.class)
    .put(SlimeType.EARTH, Items.SLIME_BALL.delegate)
    .putAll(ITEMS.registerEnum(SlimeType.TINKER, "slime_ball", (type) -> new EdibleItem(type.getSlimeFood(type), TAB_GENERAL)))
    .build();

  public TinkerCommons() {
    MinecraftForge.EVENT_BUS.addListener(RecipeCacheInvalidator::onReloadListenerReload);
    CraftingHelper.register(IngredientWithout.ID, IngredientWithout.SERIALIZER);
    CraftingHelper.register(IngredientIntersection.ID, IngredientIntersection.SERIALIZER);
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigEnabledCondition.SERIALIZER);
    lootConfig = Registry.register(Registry.LOOT_CONDITION_TYPE, ConfigEnabledCondition.ID, new LootConditionType(ConfigEnabledCondition.SERIALIZER));
    lootBlockOrEntity = Registry.register(Registry.LOOT_CONDITION_TYPE, BlockOrEntityCondition.ID, new LootConditionType(BlockOrEntityCondition.SERIALIZER));
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new CommonRecipeProvider(datagenerator));
    }
  }
}
