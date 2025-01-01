package slimeknights.tconstruct.tools;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.predicate.tool.HasMaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasStatTypePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInRangePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInSetPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
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
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MaxTierHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.OneClickBreakModule;
import slimeknights.tconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.ParticleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.data.ArmorModelProvider;
import slimeknights.tconstruct.tools.data.StationSlotLayoutProvider;
import slimeknights.tconstruct.tools.data.ToolDefinitionDataProvider;
import slimeknights.tconstruct.tools.data.ToolItemModelProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRenderInfoProvider;
import slimeknights.tconstruct.tools.data.material.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialTraitsDataProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import slimeknights.tconstruct.tools.item.CrystalshotItem;
import slimeknights.tconstruct.tools.item.CrystalshotItem.CrystalshotEntity;
import slimeknights.tconstruct.tools.item.ModifiableSwordItem;
import slimeknights.tconstruct.tools.item.SlimeskullItem;
import slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher;
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

  public static final ItemObject<ModifiableItem> dagger = ITEMS.register("dagger", () -> new ModifiableSwordItem(new Item.Properties().stacksTo(2), ToolDefinitions.DAGGER));
  public static final ItemObject<ModifiableItem> sword = ITEMS.register("sword", () -> new ModifiableSwordItem(UNSTACKABLE_PROPS, ToolDefinitions.SWORD));
  public static final ItemObject<ModifiableItem> cleaver = ITEMS.register("cleaver", () -> new ModifiableSwordItem(UNSTACKABLE_PROPS, ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableCrossbowItem> crossbow = ITEMS.register("crossbow", () -> new ModifiableCrossbowItem(UNSTACKABLE_PROPS, ToolDefinitions.CROSSBOW));
  public static final ItemObject<ModifiableBowItem> longbow = ITEMS.register("longbow", () -> new ModifiableBowItem(UNSTACKABLE_PROPS, ToolDefinitions.LONGBOW));

  public static final ItemObject<ModifiableItem> flintAndBrick = ITEMS.register("flint_and_brick", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.FLINT_AND_BRICK));
  public static final ItemObject<ModifiableItem> skyStaff = ITEMS.register("sky_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.SKY_STAFF));
  public static final ItemObject<ModifiableItem> earthStaff = ITEMS.register("earth_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.EARTH_STAFF));
  public static final ItemObject<ModifiableItem> ichorStaff = ITEMS.register("ichor_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.ICHOR_STAFF));
  public static final ItemObject<ModifiableItem> enderStaff = ITEMS.register("ender_staff", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.ENDER_STAFF));

  // ancient
  public static final ItemObject<ModifiableItem> meltingPan = ITEMS.register("melting_pan", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.MELTING_PAN));
  public static final ItemObject<ModifiableCrossbowItem> warPick = ITEMS.register("war_pick", () -> new ModifiableCrossbowItem(UNSTACKABLE_PROPS, ToolDefinitions.WAR_PICK));
  public static final ItemObject<ModifiableItem> battlesign = ITEMS.register("battlesign", () -> new ModifiableItem(UNSTACKABLE_PROPS, ToolDefinitions.BATTLESIGN));

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
    for (ConfigurableAction action : Config.COMMON.toolTweaks) {
      event.enqueueWork(action);
    }
    ModifierHooks.init();
    ToolHooks.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      ItemPredicate.register(ToolStackItemPredicate.ID, ToolStackItemPredicate::deserialize);
      CraftingHelper.register(ToolHookIngredient.Serializer.ID, ToolHookIngredient.Serializer.INSTANCE);

      // tool definition components
      ToolModule.LOADER.register(getResource("base_stats"), SetStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("multiply_stats"), MultiplyStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_actions"), ToolActionsModule.LOADER);
      ToolModule.LOADER.register(getResource("traits"), ToolTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("modifier_slots"), ToolSlotsModule.LOADER);
      ToolModule.LOADER.register(getResource("volatile_flag"), VolatileFlagModule.LOADER);
      // harvest
      ToolModule.LOADER.register(getResource("is_effective"), IsEffectiveModule.LOADER);
      ToolModule.LOADER.register(getResource("mining_speed_modifier"), MiningSpeedModifierModule.LOADER);
      ToolModule.LOADER.register(getResource("max_tier"), MaxTierHarvestLogic.LOADER);
      ToolModule.LOADER.register(getResource("one_click_break"), OneClickBreakModule.LOADER);
      // material
      ToolModule.LOADER.register(getResource("material_stats"), MaterialStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("part_stats"), PartStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_traits"), MaterialTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_parts"), PartsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_repair"), MaterialRepairModule.LOADER);
      ToolModule.LOADER.register(getResource("default_materials"), DefaultMaterialsModule.LOADER);
      // aoe
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
      ToolModule.LOADER.register(getResource("dual_option_interaction"), DualOptionInteraction.LOADER);
      ToolModule.LOADER.register(getResource("preference_set_interaction"), PreferenceSetInteraction.LOADER);
      // special tool modules
      ToolModule.LOADER.register(getResource("melting_fluid_effective"), MeltingFluidEffectiveModule.LOADER);
      // tool predicates
      ToolContextPredicate.LOADER.register(getResource("has_upgrades"), ToolContextPredicate.HAS_UPGRADES.getLoader());
      ToolContextPredicate.LOADER.register(getResource("has_modifier"), HasModifierPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_material"), HasMaterialPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_stat_type"), HasStatTypePredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("not_broken"), ToolStackPredicate.NOT_BROKEN.getLoader());
      ToolStackPredicate.LOADER.register(getResource("stat_in_range"), StatInRangePredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("stat_in_set"), StatInSetPredicate.LOADER);
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

    // ancient tools
    acceptTool(output, meltingPan);
    acceptTool(output, warPick);
    acceptTool(output, battlesign);

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
}
