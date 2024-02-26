package slimeknights.tconstruct.shared;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.json.BlockOrEntityCondition;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.json.TagDifferencePresentCondition;
import slimeknights.tconstruct.library.json.TagIntersectionPresentCondition;
import slimeknights.tconstruct.library.json.TagNotEmptyLootCondition;
import slimeknights.tconstruct.library.json.TagPreferenceLootEntry;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.predicate.block.SetBlockPredicate;
import slimeknights.tconstruct.library.json.predicate.block.TagBlockPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.MobTypePredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TagEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TinkerLivingEntityPredicate;
import slimeknights.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.BetterPaneBlock;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.ClearStainedGlassPaneBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.PlatformBlock;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.shared.block.SoulGlassBlock;
import slimeknights.tconstruct.shared.block.SoulGlassPaneBlock;
import slimeknights.tconstruct.shared.block.WaxedPlatformBlock;
import slimeknights.tconstruct.shared.block.WeatheringPlatformBlock;
import slimeknights.tconstruct.shared.command.TConstructCommand;
import slimeknights.tconstruct.shared.data.CommonRecipeProvider;
import slimeknights.tconstruct.shared.inventory.BlockContainerOpenedTrigger;
import slimeknights.tconstruct.shared.item.CheeseBlockItem;
import slimeknights.tconstruct.shared.item.CheeseItem;
import slimeknights.tconstruct.shared.item.TinkerBookItem;
import slimeknights.tconstruct.shared.item.TinkerBookItem.BookType;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_commons");

  /*
   * Blocks
   */
  public static final Material GLOW = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
  public static final RegistryObject<GlowBlock> glow = BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(GLOW, SoundType.WOOL).strength(0.0F).lightLevel(s -> 14).noOcclusion()));
  public static final BuildingBlockObject mudBricks = BLOCKS.registerBuilding("mud_bricks", builder(Material.DIRT, SoundType.GRAVEL).requiresCorrectToolForDrops().strength(2.0F), GENERAL_BLOCK_ITEM);
  // glass
  public static final ItemObject<GlassBlock> clearGlass = BLOCKS.register("clear_glass", () -> new GlassBlock(glassBuilder(MaterialColor.NONE)), GENERAL_BLOCK_ITEM);
  public static final ItemObject<TintedGlassBlock> clearTintedGlass = BLOCKS.register("clear_tinted_glass", () -> new TintedGlassBlock(glassBuilder(MaterialColor.COLOR_GRAY).color(MaterialColor.COLOR_GRAY).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)), GENERAL_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> clearGlassPane = BLOCKS.register("clear_glass_pane", () -> new ClearGlassPaneBlock(glassBuilder(MaterialColor.NONE)), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassBlock> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(glassBuilder(color.getDye().getMaterialColor()), color), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassPaneBlock> clearStainedGlassPane = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass_pane", (color) -> new ClearStainedGlassPaneBlock(glassBuilder(color.getDye().getMaterialColor()), color), GENERAL_BLOCK_ITEM);
  public static final ItemObject<GlassBlock> soulGlass = BLOCKS.register("soul_glass", () -> new SoulGlassBlock(glassBuilder(MaterialColor.COLOR_BROWN).speedFactor(0.2F).noCollission().isViewBlocking((state, getter, pos) -> true)), GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> soulGlassPane = BLOCKS.register("soul_glass_pane", () -> new SoulGlassPaneBlock(glassBuilder(MaterialColor.COLOR_BROWN).speedFactor(0.2F)), GENERAL_TOOLTIP_BLOCK_ITEM);
  // wood
  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", woodBuilder(MaterialColor.COLOR_ORANGE).lightLevel(s -> 7), GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject blazewood = BLOCKS.registerBuilding("blazewood", woodBuilder(MaterialColor.TERRACOTTA_RED).lightLevel(s -> 7), GENERAL_BLOCK_ITEM);
  // panes
  public static final ItemObject<IronBarsBlock> goldBars = BLOCKS.register("gold_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion()), GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> obsidianPane = BLOCKS.register("obsidian_pane", () -> new BetterPaneBlock(builder(Material.STONE, MaterialColor.PODZOL, SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().strength(25.0F, 400.0F)), GENERAL_BLOCK_ITEM);
  // platforms
  public static final ItemObject<PlatformBlock> goldPlatform = BLOCKS.register("gold_platform", () -> new PlatformBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<PlatformBlock> ironPlatform = BLOCKS.register("iron_platform", () -> new PlatformBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM);
  public static final ItemObject<PlatformBlock> cobaltPlatform = BLOCKS.register("cobalt_platform", () -> new PlatformBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).requiresCorrectToolForDrops().strength(5.0f).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM);
  public static final EnumObject<WeatherState,PlatformBlock> copperPlatform = new EnumObject.Builder<WeatherState,PlatformBlock>(WeatherState.class)
    .put(WeatherState.UNAFFECTED, BLOCKS.register("copper_platform",           () -> new WeatheringPlatformBlock(WeatherState.UNAFFECTED, BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.EXPOSED,    BLOCKS.register("exposed_copper_platform",   () -> new WeatheringPlatformBlock(WeatherState.EXPOSED,    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_LIGHT_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.WEATHERED,  BLOCKS.register("weathered_copper_platform", () -> new WeatheringPlatformBlock(WeatherState.WEATHERED,  BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.OXIDIZED,   BLOCKS.register("oxidized_copper_platform",  () -> new WeatheringPlatformBlock(WeatherState.OXIDIZED,   BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .build();
  public static final EnumObject<WeatherState,PlatformBlock> waxedCopperPlatform = new EnumObject.Builder<WeatherState,PlatformBlock>(WeatherState.class)
    .put(WeatherState.UNAFFECTED, BLOCKS.register("waxed_copper_platform",           () -> new WaxedPlatformBlock(WeatherState.UNAFFECTED, BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.EXPOSED,    BLOCKS.register("waxed_exposed_copper_platform",   () -> new WaxedPlatformBlock(WeatherState.EXPOSED,    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_LIGHT_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.WEATHERED,  BLOCKS.register("waxed_weathered_copper_platform", () -> new WaxedPlatformBlock(WeatherState.WEATHERED,  BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .put(WeatherState.OXIDIZED,   BLOCKS.register("waxed_oxidized_copper_platform",  () -> new WaxedPlatformBlock(WeatherState.OXIDIZED,   BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion()), GENERAL_BLOCK_ITEM))
    .build();


  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TAB_GENERAL));
  public static final ItemObject<EdibleItem> jeweledApple = ITEMS.register("jeweled_apple", () -> new EdibleItem(TinkerFood.JEWELED_APPLE, TAB_GENERAL));
  public static final ItemObject<Item> cheeseIngot = ITEMS.register("cheese_ingot", () -> new CheeseItem(new Properties().tab(TAB_GENERAL).food(TinkerFood.CHEESE)));
  public static final ItemObject<Block> cheeseBlock = BLOCKS.register("cheese_block", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.COLOR_YELLOW).strength(1.5F, 3.0F).speedFactor(0.4F).jumpFactor(0.5F).sound(SoundType.HONEY_BLOCK).noOcclusion()), block -> new CheeseBlockItem(block, new Properties().tab(TAB_GENERAL).food(TinkerFood.CHEESE)));

  private static final Item.Properties BOOK = new Item.Properties().tab(TAB_GENERAL).stacksTo(1);
  public static final ItemObject<TinkerBookItem> materialsAndYou = ITEMS.register("materials_and_you", () -> new TinkerBookItem(BOOK, BookType.MATERIALS_AND_YOU));
  public static final ItemObject<TinkerBookItem> punySmelting = ITEMS.register("puny_smelting", () -> new TinkerBookItem(BOOK, BookType.PUNY_SMELTING));
  public static final ItemObject<TinkerBookItem> mightySmelting = ITEMS.register("mighty_smelting", () -> new TinkerBookItem(BOOK, BookType.MIGHTY_SMELTING));
  public static final ItemObject<TinkerBookItem> tinkersGadgetry = ITEMS.register("tinkers_gadgetry", () -> new TinkerBookItem(BOOK, BookType.TINKERS_GADGETRY));
  public static final ItemObject<TinkerBookItem> fantasticFoundry = ITEMS.register("fantastic_foundry", () -> new TinkerBookItem(BOOK, BookType.FANTASTIC_FOUNDRY));
  public static final ItemObject<TinkerBookItem> encyclopedia = ITEMS.register("encyclopedia", () -> new TinkerBookItem(BOOK, BookType.ENCYCLOPEDIA));

  public static final RegistryObject<ParticleType<FluidParticleData>> fluidParticle = PARTICLE_TYPES.register("fluid", FluidParticleData.Type::new);

  /* Loot conditions */
  public static final RegistryObject<LootItemConditionType> lootConfig = LOOT_CONDITIONS.register(ConfigEnabledCondition.ID.getPath(), () -> new LootItemConditionType(ConfigEnabledCondition.SERIALIZER));
  public static final RegistryObject<LootItemConditionType> lootBlockOrEntity = LOOT_CONDITIONS.register("block_or_entity", () -> new LootItemConditionType(new BlockOrEntityCondition.ConditionSerializer()));
  public static final RegistryObject<LootItemConditionType> lootTagNotEmptyCondition = LOOT_CONDITIONS.register("tag_not_empty", () -> new LootItemConditionType(new TagNotEmptyLootCondition.ConditionSerializer()));
  public static final RegistryObject<LootPoolEntryType> lootTagPreference = LOOT_ENTRIES.register("tag_preference", () -> new LootPoolEntryType(new TagPreferenceLootEntry.Serializer()));

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeType, Item> slimeball = new EnumObject.Builder<SlimeType, Item>(SlimeType.class)
    .put(SlimeType.EARTH, Items.SLIME_BALL.delegate)
    .putAll(ITEMS.registerEnum(SlimeType.TINKER, "slime_ball", type -> new Item(GENERAL_PROPS)))
    .build();

  public static final BlockContainerOpenedTrigger CONTAINER_OPENED_TRIGGER = new BlockContainerOpenedTrigger();

  public TinkerCommons() {
    MinecraftForge.EVENT_BUS.addListener(RecipeCacheInvalidator::onReloadListenerReload);
  }

  @SubscribeEvent
  void commonSetupEvent(FMLCommonSetupEvent event) {
    TConstructCommand.init();
    SlimeBounceHandler.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    CraftingHelper.register(NoContainerIngredient.ID, NoContainerIngredient.Serializer.INSTANCE);
    CraftingHelper.register(ConfigEnabledCondition.SERIALIZER);
    CriteriaTriggers.register(CONTAINER_OPENED_TRIGGER);

    CraftingHelper.register(TagIntersectionPresentCondition.SERIALIZER);
    CraftingHelper.register(TagDifferencePresentCondition.SERIALIZER);
    CraftingHelper.register(new TagNotEmptyLootCondition.ConditionSerializer());
    // block predicates
    BlockPredicate.LOADER.register(getResource("and"), BlockPredicate.AND);
    BlockPredicate.LOADER.register(getResource("or"), BlockPredicate.OR);
    BlockPredicate.LOADER.register(getResource("inverted"), BlockPredicate.INVERTED);
    BlockPredicate.LOADER.register(getResource("requires_tool"), BlockPredicate.REQUIRES_TOOL.getLoader());
    BlockPredicate.LOADER.register(getResource("set"), SetBlockPredicate.LOADER);
    BlockPredicate.LOADER.register(getResource("tag"), TagBlockPredicate.LOADER);
    // entity predicates
    LivingEntityPredicate.LOADER.register(getResource("and"), LivingEntityPredicate.AND);
    LivingEntityPredicate.LOADER.register(getResource("or"), LivingEntityPredicate.OR);
    LivingEntityPredicate.LOADER.register(getResource("inverted"), LivingEntityPredicate.INVERTED);
    LivingEntityPredicate.LOADER.register(getResource("any"), LivingEntityPredicate.ANY.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("airborne"), LivingEntityPredicate.AIRBORNE.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("crouching"), LivingEntityPredicate.CROUCHING.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("fire_immune"), LivingEntityPredicate.FIRE_IMMUNE.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("water_sensitive"), LivingEntityPredicate.WATER_SENSITIVE.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("on_fire"), LivingEntityPredicate.ON_FIRE.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("tag"), TagEntityPredicate.LOADER);
    LivingEntityPredicate.LOADER.register(getResource("mob_type"), MobTypePredicate.LOADER);
    LivingEntityPredicate.LOADER.register(getResource("eyes_in_water"), LivingEntityPredicate.EYES_IN_WATER.getLoader());
    LivingEntityPredicate.LOADER.register(getResource("feet_in_water"), LivingEntityPredicate.FEET_IN_WATER.getLoader());
    // mantle
    slimeknights.mantle.data.predicate.entity.LivingEntityPredicate.LOADER.register(getResource("airborne"), TinkerLivingEntityPredicate.AIRBORNE.getLoader());
    // register mob types
    MobTypePredicate.MOB_TYPES.register(new ResourceLocation("undefined"), MobType.UNDEFINED);
    MobTypePredicate.MOB_TYPES.register(new ResourceLocation("undead"), MobType.UNDEAD);
    MobTypePredicate.MOB_TYPES.register(new ResourceLocation("arthropod"), MobType.ARTHROPOD);
    MobTypePredicate.MOB_TYPES.register(new ResourceLocation("illager"), MobType.ILLAGER);
    MobTypePredicate.MOB_TYPES.register(new ResourceLocation("water"), MobType.WATER);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new CommonRecipeProvider(datagenerator));
    }
  }
}
