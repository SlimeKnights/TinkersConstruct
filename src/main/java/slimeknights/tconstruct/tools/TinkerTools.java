package slimeknights.tconstruct.tools;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.config.ConfigurableAction;
import slimeknights.tconstruct.common.data.tags.MaterialTagProvider;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPaletteDebugGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.client.data.material.TrimMaterialPaletteGenerator;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.predicate.tool.HasMaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasStatTypePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.PersistentDataPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInRangePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInSetPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.VolatileDataPredicate;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.BoxAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.ConditionalAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.TreeAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.VeiningAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.VolatileFlagModule;
import slimeknights.tconstruct.library.tools.definition.module.build.VolatileIntModule;
import slimeknights.tconstruct.library.tools.definition.module.display.FixedMaterialToolName;
import slimeknights.tconstruct.library.tools.definition.module.display.MaterialToolNameModule;
import slimeknights.tconstruct.library.tools.definition.module.display.StatTypesToolNameModule;
import slimeknights.tconstruct.library.tools.definition.module.display.UniqueMaterialToolName;
import slimeknights.tconstruct.library.tools.definition.module.interaction.AttackInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.ToggleableSetInteraction;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.StatlessPartRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MaxTierModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.OneClickBreakModule;
import slimeknights.tconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.ParticleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArrowItem;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.item.ModifiableShurikenItem;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.data.ArmorModelProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.data.StationSlotLayoutProvider;
import slimeknights.tconstruct.tools.data.ToolDefinitionDataProvider;
import slimeknights.tconstruct.tools.data.ToolItemModelProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.data.material.MaterialRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRenderInfoProvider;
import slimeknights.tconstruct.tools.data.material.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialTraitsDataProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import slimeknights.tconstruct.tools.entity.CombatFishingHook;
import slimeknights.tconstruct.tools.entity.ModifiableArrow;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;
import slimeknights.tconstruct.tools.entity.ThrownTool;
import slimeknights.tconstruct.tools.item.CrystalshotItem;
import slimeknights.tconstruct.tools.item.CrystalshotItem.CrystalshotEntity;
import slimeknights.tconstruct.tools.item.ModifiableSwordItem;
import slimeknights.tconstruct.tools.item.SlimeskullItem;
import slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher;
import slimeknights.tconstruct.tools.logic.ModifiableArrowDispenserBehavior;
import slimeknights.tconstruct.tools.logic.ModifiableShurikenDispenserBehavior;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;
import slimeknights.tconstruct.tools.modules.MeltingFluidEffectiveModule;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {
  public TinkerTools() {
    SlotType.init();
    BlockSideHitListener.init();
    ModifierLootingHandler.init();
    RandomMaterial.init();
  }

  /** Creative tab for complete tools */
  public static final RegistryObject<CreativeModeTab> tabTools = CREATIVE_TABS.register(
    "tools", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "tools"))
                                  .icon(() -> TinkerTools.pickaxe.get().getRenderTool())
                                  .displayItems(TinkerTools::addTabItems)
                                  .withTabsBefore(TinkerTables.tabTables.getId())
                                  .withSearchBar()
                                  .build());

  /** Loot function type for tool add data */
  public static final RegistryObject<LootItemFunctionType> lootAddToolData = LOOT_FUNCTIONS.register("add_tool_data", () -> new LootItemFunctionType(AddToolDataFunction.SERIALIZER));

  /*
   * Items
   */
  public static final ItemObject<ModifiableItem> pickaxe = ITEMS.register("pickaxe", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.PICKAXE));
  public static final ItemObject<ModifiableItem> sledgeHammer = ITEMS.register("sledge_hammer", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.SLEDGE_HAMMER));
  public static final ItemObject<ModifiableItem> veinHammer = ITEMS.register("vein_hammer", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.VEIN_HAMMER));

  public static final ItemObject<ModifiableItem> mattock = ITEMS.register("mattock", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.MATTOCK));
  public static final ItemObject<ModifiableItem> pickadze = ITEMS.register("pickadze", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.PICKADZE));
  public static final ItemObject<ModifiableItem> excavator = ITEMS.register("excavator", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.EXCAVATOR));

  public static final ItemObject<ModifiableItem> handAxe = ITEMS.register("hand_axe", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.HAND_AXE));
  public static final ItemObject<ModifiableItem> broadAxe = ITEMS.register("broad_axe", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.BROAD_AXE));

  public static final ItemObject<ModifiableItem> kama = ITEMS.register("kama", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.KAMA));
  public static final ItemObject<ModifiableItem> scythe = ITEMS.register("scythe", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.SCYTHE));

  // setting durability to -1 makes sure its not 0 for the defaultDurability call in the TieredItem constructor, but is still less than 0 for the stacksTo call to work
  // problem is setting the durability sets the max stack size, and we don't want that. And we need TieredItem to work with piglins
  public static final ItemObject<ModifiableItem> dagger = ITEMS.register("dagger", () -> new ModifiableSwordItem(new Item.Properties().durability(-1).stacksTo(2), ToolDefinitions.DAGGER, 2));
  public static final ItemObject<ModifiableItem> sword = ITEMS.register("sword", () -> new ModifiableSwordItem(UNSTACKABLE_PROPS, ToolDefinitions.SWORD));
  public static final ItemObject<ModifiableItem> cleaver = ITEMS.register("cleaver", () -> new ModifiableSwordItem(UNSTACKABLE_PROPS, ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableCrossbowItem> crossbow = ITEMS.register("crossbow", () -> new ModifiableCrossbowItem(UNSTACKABLE_PROPS, ToolDefinitions.CROSSBOW));
  public static final ItemObject<ModifiableBowItem> longbow = ITEMS.register("longbow", () -> new ModifiableBowItem(UNSTACKABLE_PROPS, ToolDefinitions.LONGBOW, true));
  public static final ItemObject<ModifiableItem> fishingRod = ITEMS.register("fishing_rod", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.FISHING_ROD));
  public static final ItemObject<ModifiableItem> javelin = ITEMS.register("javelin", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.JAVELIN));
  public static final ItemObject<ModifiableArrowItem> arrow = ITEMS.register("arrow", () -> new ModifiableArrowItem(ITEM_PROPS, ToolDefinitions.ARROW));
  public static final ItemObject<ModifiableShurikenItem> shuriken = ITEMS.register("shuriken", () -> new ModifiableShurikenItem(new Item.Properties().stacksTo(16), ToolDefinitions.SHURIKEN));
  public static final ItemObject<ModifiableShurikenItem> throwingAxe = ITEMS.register("throwing_axe", () -> new ModifiableShurikenItem(new Item.Properties().stacksTo(16), ToolDefinitions.THROWING_AXE));

  public static final ItemObject<ModifiableItem> flintAndBrick = ITEMS.register("flint_and_brick", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.FLINT_AND_BRICK));
  public static final ItemObject<ModifiableItem> skyStaff = ITEMS.register("sky_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.SKY_STAFF));
  public static final ItemObject<ModifiableItem> earthStaff = ITEMS.register("earth_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.EARTH_STAFF));
  public static final ItemObject<ModifiableItem> ichorStaff = ITEMS.register("ichor_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.ICHOR_STAFF));
  public static final ItemObject<ModifiableItem> enderStaff = ITEMS.register("ender_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.ENDER_STAFF));

  // ancient
  public static final ItemObject<ModifiableItem> meltingPan = ITEMS.register("melting_pan", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.MELTING_PAN));
  public static final ItemObject<ModifiableCrossbowItem> warPick = ITEMS.register("war_pick", () -> new ModifiableCrossbowItem(UNSTACKABLE_PROPS, ToolDefinitions.WAR_PICK));
  public static final ItemObject<ModifiableItem> battlesign = ITEMS.register("battlesign", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.BATTLESIGN));
  public static final ItemObject<ModifiableItem> swasher = ITEMS.register("swasher", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.SWASHER));
  public static final ItemObject<ModifiableItem> minotaurAxe;
  static {
    // conditionally register minotaur axe as it's the easiest way to keep it out of JEI display
    if (ModList.get().isLoaded("twilightforest")) {
      minotaurAxe = ITEMS.register("minotaur_axe", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.MINOTAUR_AXE));
    } else {
      minotaurAxe = new ItemObject<>(RegistryObject.create(getResource("minotaur_axe"), ForgeRegistries.ITEMS));
    }
  }

  // armor
  public static final EnumObject<ArmorItem.Type,ModifiableArmorItem> travelersGear = ITEMS.registerEnum("travelers", ArmorItem.Type.values(), type -> new MultilayerArmorItem(ArmorDefinitions.TRAVELERS, type, UNSTACKABLE_PROPS));
  public static final EnumObject<ArmorItem.Type,ModifiableArmorItem> plateArmor = ITEMS.registerEnum("plate", ArmorItem.Type.values(), type -> new MultilayerArmorItem(ArmorDefinitions.PLATE, type, UNSTACKABLE_PROPS));
  public static final EnumObject<ArmorItem.Type,ModifiableArmorItem> slimesuit = new EnumObject.Builder<ArmorItem.Type,ModifiableArmorItem>(ArmorItem.Type.class)
    .putAll(ITEMS.registerEnum("slime", new ArmorItem.Type[] {ArmorItem.Type.BOOTS, ArmorItem.Type.LEGGINGS, ArmorItem.Type.CHESTPLATE}, type -> new MultilayerArmorItem(ArmorDefinitions.SLIMESUIT, type, UNSTACKABLE_PROPS)))
    .put(ArmorItem.Type.HELMET, ITEMS.register("slime_helmet", () -> new SlimeskullItem(ArmorDefinitions.SLIMESUIT, UNSTACKABLE_PROPS)))
    .build();


  // shields
  public static final ItemObject<ModifiableItem> travelersShield = ITEMS.register("travelers_shield", () -> new ModifiableItem(UNSTACKABLE_PROPS, ArmorDefinitions.TRAVELERS_SHIELD));
  public static final ItemObject<ModifiableItem> plateShield = ITEMS.register("plate_shield", () -> new ModifiableItem(UNSTACKABLE_PROPS, ArmorDefinitions.PLATE_SHIELD));

  // arrows
  public static final ItemObject<ArrowItem> crystalshotItem = ITEMS.register("crystalshot", () -> new CrystalshotItem(ITEM_PROPS));

  /* Particles */
  public static final RegistryObject<SimpleParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack", () -> new SimpleParticleType(true));
  public static final RegistryObject<SimpleParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack", () -> new SimpleParticleType(true));
  public static final RegistryObject<SimpleParticleType> bonkAttackParticle = PARTICLE_TYPES.register("bonk", () -> new SimpleParticleType(true));

  /* Entities */
  public static final RegistryObject<EntityType<IndestructibleItemEntity>> indestructibleItem = ENTITIES.register("indestructible_item", () ->
    EntityType.Builder.<IndestructibleItemEntity>of(IndestructibleItemEntity::new, MobCategory.MISC)
                      .sized(0.25F, 0.25F)
                      .fireImmune());
  public static final RegistryObject<EntityType<CrystalshotEntity>> crystalshotEntity = ENTITIES.register("crystalshot", () ->
    EntityType.Builder.<CrystalshotEntity>of(CrystalshotEntity::new, MobCategory.MISC)
                      .sized(0.5F, 0.5F)
                      .clientTrackingRange(4)
                      .updateInterval(20));
  public static final RegistryObject<EntityType<CombatFishingHook>> fishingHook = ENTITIES.register("fishing_bobber", () -> EntityType.Builder.<CombatFishingHook>of(CombatFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
  public static final RegistryObject<EntityType<ModifiableArrow>> materialArrow = ENTITIES.register("arrow", () -> EntityType.Builder.<ModifiableArrow>of(ModifiableArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
  public static final RegistryObject<EntityType<ThrownShuriken>> thrownShuriken = ENTITIES.register("thrown_shuriken", () -> EntityType.Builder.<ThrownShuriken>of(ThrownShuriken::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
  public static final RegistryObject<EntityType<ThrownTool>> thrownTool = ENTITIES.register("thrown_tool", () -> EntityType.Builder.<ThrownTool>of(ThrownTool::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
  static {
    // used for the fishing bobber
    DATA_SERIALIZERS.register("material_variant", () -> MaterialVariantId.DATA_ACCESSOR);
  }


  /* Containers */
  public static final RegistryObject<MenuType<ToolContainerMenu>> toolContainer = MENUS.register("tool_container", ToolContainerMenu::forClient);


  /*
   * Events
   */

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    EquipmentChangeWatcher.register();
    ToolCapabilityProvider.register(ToolFluidCapability.Provider::new);
    ToolCapabilityProvider.register(ToolInventoryCapability.Provider::new);
    ToolCapabilityProvider.register((stack, tool) -> new ToolEnergyCapability.Provider(tool));
    for (ConfigurableAction action : Config.COMMON.toolTweaks) {
      event.enqueueWork(action);
    }
    event.enqueueWork(() -> {
      DispenserBlock.registerBehavior(TinkerTools.arrow.get(), ModifiableArrowDispenserBehavior.INSTANCE);
      DispenserBlock.registerBehavior(TinkerTools.shuriken.get(), ModifiableShurikenDispenserBehavior.INSTANCE);
      DispenserBlock.registerBehavior(TinkerTools.throwingAxe.get(), ModifiableShurikenDispenserBehavior.INSTANCE);
      ModifierUtil.registerShieldDisabler(entity -> {
        if (entity instanceof Player player && player.isBlocking()) {
          player.disableShield(true);
        }
      }, EntityType.PLAYER);
    });
    ModifierHooks.init();
    ToolHooks.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      ItemPredicate.register(ToolStackItemPredicate.ID, ToolStackItemPredicate::deserialize);
      CraftingHelper.register(ToolHookIngredient.Serializer.ID, ToolHookIngredient.Serializer.INSTANCE);

      // register tool stats that are not defined directly in the class; safer than static init registration
      ToolStats.register(OverslimeModule.OVERSLIME_STAT);
      ToolStats.register(ToolTankHelper.CAPACITY_STAT);
      ToolStats.register(ToolEnergyCapability.MAX_STAT);

      ToolModule.LOADER.register(getResource("empty"), ToolModule.EMPTY.getLoader());
      // tool definition components
      ToolModule.LOADER.register(getResource("base_stats"), SetStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("multiply_stats"), MultiplyStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_actions"), ToolActionsModule.LOADER);
      ToolModule.LOADER.register(getResource("traits"), ToolTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("modifier_slots"), ToolSlotsModule.LOADER);
      ToolModule.LOADER.register(getResource("volatile_flag"), VolatileFlagModule.LOADER);
      ToolModule.LOADER.register(getResource("volatile_int"), VolatileIntModule.LOADER);
      // harvest
      ToolModule.LOADER.register(getResource("is_effective"), IsEffectiveModule.LOADER);
      ToolModule.LOADER.register(getResource("mining_speed_modifier"), MiningSpeedModifierModule.LOADER);
      ToolModule.LOADER.register(getResource("max_tier"), MaxTierModule.LOADER);
      ToolModule.LOADER.register(getResource("one_click_break"), OneClickBreakModule.LOADER);
      // material
      ToolModule.LOADER.register(getResource("material_stats"), MaterialStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("part_stats"), PartStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_traits"), MaterialTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_parts"), PartsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_repair"), MaterialRepairModule.LOADER);
      ToolModule.LOADER.register(getResource("default_materials"), DefaultMaterialsModule.LOADER);
      ToolModule.LOADER.register(getResource("statless_part_repair"), StatlessPartRepairModule.LOADER);
      // aoe
      AreaOfEffectIterator.LOADER.register(getResource("empty"), AreaOfEffectIterator.EMPTY.getLoader());
      AreaOfEffectIterator.register(getResource("box_aoe"), BoxAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("circle_aoe"), CircleAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("tree_aoe"), TreeAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("vein_aoe"), VeiningAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("conditional_aoe"), ConditionalAOEIterator.LOADER);
      // attack
      ToolModule.LOADER.register(getResource("sweep_melee"), SweepWeaponAttack.LOADER);
      ToolModule.LOADER.register(getResource("circle_melee"), CircleWeaponAttack.LOADER);
      ToolModule.LOADER.register(getResource("melee_particle"), ParticleWeaponAttack.LOADER);
      // generic tool modules
      ToolModule.LOADER.register(getResource("attack_interaction"), AttackInteraction.LOADER);
      ToolModule.LOADER.register(getResource("dual_option_interaction"), DualOptionInteraction.LOADER);
      ToolModule.LOADER.register(getResource("preference_set_interaction"), PreferenceSetInteraction.LOADER);
      ToolModule.LOADER.register(getResource("toggleable_set_interaction"), ToggleableSetInteraction.LOADER);
      // special tool modules
      ToolModule.LOADER.register(getResource("melting_fluid_effective"), MeltingFluidEffectiveModule.LOADER);
      // display name
      ToolModule.LOADER.register(getResource("material_name"), MaterialToolNameModule.LOADER);
      ToolModule.LOADER.register(getResource("stat_types_name"), StatTypesToolNameModule.LOADER);
      ToolModule.LOADER.register(getResource("fixed_material_name"), FixedMaterialToolName.LOADER);
      ToolModule.LOADER.register(getResource("unique_material_name"), UniqueMaterialToolName.LOADER);
      // tool predicates
      ToolContextPredicate.LOADER.register(getResource("has_upgrades"), ToolContextPredicate.HAS_UPGRADES.getLoader());
      ToolContextPredicate.LOADER.register(getResource("has_modifier"), HasModifierPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_material"), HasMaterialPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_stat_type"), HasStatTypePredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_persistent_key"), PersistentDataPredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("not_broken"), ToolStackPredicate.NOT_BROKEN.getLoader());
      ToolStackPredicate.LOADER.register(getResource("stat_in_range"), StatInRangePredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("stat_in_set"), StatInSetPredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("has_volatile_key"), VolatileDataPredicate.LOADER);
    }
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    boolean client = event.includeClient();
    generator.addProvider(server, new ToolsRecipeProvider(packOutput));
    generator.addProvider(server, new MaterialRecipeProvider(packOutput));
    MaterialDataProvider materials = new MaterialDataProvider(packOutput);
    generator.addProvider(server, materials);
    generator.addProvider(server, new MaterialStatsDataProvider(packOutput, materials));
    generator.addProvider(server, new MaterialTraitsDataProvider(packOutput, materials));
    generator.addProvider(server, new ToolDefinitionDataProvider(packOutput));
    generator.addProvider(server, new StationSlotLayoutProvider(packOutput));
    generator.addProvider(server, new MaterialTagProvider(packOutput, existingFileHelper));
    generator.addProvider(client, new ToolItemModelProvider(packOutput, existingFileHelper));
    TinkerMaterialSpriteProvider materialSprites = new TinkerMaterialSpriteProvider();
    TinkerPartSpriteProvider partSprites = new TinkerPartSpriteProvider();
    generator.addProvider(client, new MaterialRenderInfoProvider(packOutput, materialSprites, existingFileHelper));
    generator.addProvider(client, new GeneratorPartTextureJsonGenerator(packOutput, TConstruct.MOD_ID, partSprites));
    generator.addProvider(client, new MaterialPartTextureGenerator(packOutput, existingFileHelper, partSprites, materialSprites));
    generator.addProvider(client, new MaterialPaletteDebugGenerator(packOutput, TConstruct.MOD_ID, materialSprites));
    generator.addProvider(client, new ArmorModelProvider(packOutput));
    generator.addProvider(client, new TrimMaterialPaletteGenerator(packOutput, TConstruct.MOD_ID, existingFileHelper, materialSprites, MaterialIds.TRIM_MATERIALS) {
      @Override
      protected ISpriteTransformer getTransformer(MaterialId material) {
        // queens slime is normally a spacially aware generator, use flat colors
        if (MaterialIds.queensSlime.equals(material)) {
          // return new RecolorSpriteTransformer(GreyToColorMapping.builderFromBlack().addARGB(63, 0xFF274723).addARGB(102, 0xFF325B2D).addARGB(140, 0xFF34742D).addARGB(178, 0xFF348D3C).addARGB(216, 0xFF52BB53).addARGB(255, 0xFF5DD45F).build());
          return new RecolorSpriteTransformer(GreyToColorMapping.builderFromBlack().addARGB(63, 0xFF5F1100).addARGB(102, 0xFF893200).addARGB(140, 0xFF966A03).addARGB(178, 0xFF8C9226).addARGB(216, 0xFF52BB53).addARGB(255, 0xFF5DD45F).build());
        }
        return super.getTransformer(material);
      }
    });
  }

  /** Adds all relevant items to the creative tab */
  private static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
    // start with tools that lack materials
    Consumer<ItemStack> output = tab::accept;
    acceptTool(output, flintAndBrick);
    acceptTool(output, skyStaff);
    acceptTool(output, earthStaff);
    acceptTool(output, ichorStaff);
    acceptTool(output, enderStaff);

    // small tools
    acceptTool(output, pickaxe);
    acceptTool(output, pickadze);
    acceptTool(output, mattock);
    acceptTool(output, handAxe);
    acceptTool(output, kama);
    acceptTool(output, dagger);
    acceptTool(output, sword);

    // broad tools
    acceptTool(output, sledgeHammer);
    acceptTool(output, veinHammer);
    acceptTool(output, excavator);
    acceptTool(output, broadAxe);
    acceptTool(output, scythe);
    acceptTool(output, cleaver);

    // ranged tools
    acceptTool(output, crossbow);
    acceptTool(output, longbow);
    acceptTool(output, fishingRod);
    acceptTool(output, javelin);
    acceptTool(output, arrow);
    acceptTool(output, shuriken);
    acceptEFLN(shuriken.get(), tab);
    acceptTool(output, throwingAxe);

    // ancient tools
    acceptTool(output, meltingPan);
    acceptTool(output, warPick);
    acceptTool(output, battlesign);
    acceptTool(output, swasher);
    if (ModList.get().isLoaded("twilightforest")) {
      acceptTool(output, minotaurAxe);
    }

    // armor
    acceptTools(output, travelersGear);
    acceptTool(output, travelersShield);
    acceptTools(output, plateArmor);
    acceptTool(output, plateShield);
    acceptTools(output, slimesuit);
  }

  /** Adds a tool to the tab */
  private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
    ToolBuildHandler.addVariants(output, tool.get(), "");
  }

  /** Adds a tool to the tab */
  private static void acceptTools(Consumer<ItemStack> output, EnumObject<?,? extends IModifiable> tools) {
    tools.forEach(tool -> ToolBuildHandler.addVariants(output, tool, ""));
  }

  /**
   * Creates a EFLN using the given shuriken style item.
   * @param item  Item to add
   * @param tab   Creative tab to fill
   */
  private static void acceptEFLN(IModifiable item, CreativeModeTab.Output tab) {
    ToolDefinition definition = item.getToolDefinition();
    if (ToolMaterialHook.stats(definition).size() == 2) {
      IMaterial gunpowder = MaterialRegistry.getMaterial(MaterialIds.gunpowder);
      IMaterial prismarine = MaterialRegistry.getMaterial(MaterialIds.prismarine);
      if (gunpowder != IMaterial.UNKNOWN && prismarine != IMaterial.UNKNOWN) {
        ToolStack efln = ToolStack.createTool(item.asItem(), definition, MaterialNBT.of(gunpowder, prismarine));
        if (ModifierManager.INSTANCE.contains(ModifierIds.redirected)) {
          efln.addModifier(ModifierIds.redirected, 1);
        }
        ItemStack stack = efln.createStack();
        stack.setHoverName(TConstruct.makeTranslation("item", "efln_ball"));
        tab.accept(stack);
      }
    }
  }
}
