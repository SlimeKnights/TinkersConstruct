package slimeknights.tconstruct.tools;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.config.ConfigurableAction;
import slimeknights.tconstruct.common.data.tags.MaterialTagProvider;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.json.AddToolDataFunction;
import slimeknights.tconstruct.library.json.RandomMaterial;
import slimeknights.tconstruct.library.json.predicate.tool.HasMaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasStatTypePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ItemToolPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInSetPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolPredicate;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.aoe.BoxAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.FallbackAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.TreeAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.VeiningAOEIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.FixedTierHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.ModifiedHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.TagHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.definition.module.ToolModuleHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import slimeknights.tconstruct.library.tools.definition.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.IWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.ParticleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.item.ModifiableStaffItem;
import slimeknights.tconstruct.library.tools.stat.StatPredicate;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tools.data.StationSlotLayoutProvider;
import slimeknights.tconstruct.tools.data.ToolDefinitionDataProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRenderInfoProvider;
import slimeknights.tconstruct.tools.data.material.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialTraitsDataProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.item.CrystalshotItem;
import slimeknights.tconstruct.tools.item.CrystalshotItem.CrystalshotEntity;
import slimeknights.tconstruct.tools.item.ModifiableBowItem;
import slimeknights.tconstruct.tools.item.ModifiableCrossbowItem;
import slimeknights.tconstruct.tools.item.ModifiableDaggerItem;
import slimeknights.tconstruct.tools.item.ModifiableSwordItem;
import slimeknights.tconstruct.tools.item.PlateArmorItem;
import slimeknights.tconstruct.tools.item.SlimelytraItem;
import slimeknights.tconstruct.tools.item.SlimeskullItem;
import slimeknights.tconstruct.tools.item.SlimesuitItem;
import slimeknights.tconstruct.tools.item.TravelersGearItem;
import slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

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

  /** Creative tab for all tool items */
  public static final CreativeModeTab TAB_TOOLS = new SupplierCreativeTab(TConstruct.MOD_ID, "tools", () -> TinkerTools.pickaxe.get().getRenderTool());

  /** Loot function type for tool add data */
  public static final RegistryObject<LootItemFunctionType> lootAddToolData = LOOT_FUNCTIONS.register("add_tool_data", () -> new LootItemFunctionType(AddToolDataFunction.SERIALIZER));

  /*
   * Items
   */
  private static final Item.Properties TOOL = new Item.Properties().stacksTo(1).tab(TAB_TOOLS);

  public static final ItemObject<ModifiableItem> pickaxe = ITEMS.register("pickaxe", () -> new ModifiableItem(TOOL, ToolDefinitions.PICKAXE));
  public static final ItemObject<ModifiableItem> sledgeHammer = ITEMS.register("sledge_hammer", () -> new ModifiableItem(TOOL, ToolDefinitions.SLEDGE_HAMMER));
  public static final ItemObject<ModifiableItem> veinHammer = ITEMS.register("vein_hammer", () -> new ModifiableItem(TOOL, ToolDefinitions.VEIN_HAMMER));

  public static final ItemObject<ModifiableItem> mattock = ITEMS.register("mattock", () -> new ModifiableItem(TOOL, ToolDefinitions.MATTOCK));
  public static final ItemObject<ModifiableItem> pickadze = ITEMS.register("pickadze", () -> new ModifiableItem(TOOL, ToolDefinitions.PICKADZE));
  public static final ItemObject<ModifiableItem> excavator = ITEMS.register("excavator", () -> new ModifiableItem(TOOL, ToolDefinitions.EXCAVATOR));

  public static final ItemObject<ModifiableItem> handAxe = ITEMS.register("hand_axe", () -> new ModifiableItem(TOOL, ToolDefinitions.HAND_AXE));
  public static final ItemObject<ModifiableItem> broadAxe = ITEMS.register("broad_axe", () -> new ModifiableItem(TOOL, ToolDefinitions.BROAD_AXE));

  public static final ItemObject<ModifiableItem> kama = ITEMS.register("kama", () -> new ModifiableItem(TOOL, ToolDefinitions.KAMA));
  public static final ItemObject<ModifiableItem> scythe = ITEMS.register("scythe", () -> new ModifiableItem(TOOL, ToolDefinitions.SCYTHE));

  public static final ItemObject<ModifiableItem> dagger = ITEMS.register("dagger", () -> new ModifiableDaggerItem(TOOL, ToolDefinitions.DAGGER));
  public static final ItemObject<ModifiableItem> sword = ITEMS.register("sword", () -> new ModifiableSwordItem(TOOL, ToolDefinitions.SWORD));
  public static final ItemObject<ModifiableItem> cleaver = ITEMS.register("cleaver", () -> new ModifiableSwordItem(TOOL, ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableLauncherItem> crossbow = ITEMS.register("crossbow", () -> new ModifiableCrossbowItem(TOOL, ToolDefinitions.CROSSBOW));
  public static final ItemObject<ModifiableLauncherItem> longbow = ITEMS.register("longbow", () -> new ModifiableBowItem(TOOL, ToolDefinitions.LONGBOW));

  public static final ItemObject<ModifiableItem> flintAndBrick = ITEMS.register("flint_and_brick", () -> new ModifiableItem(TOOL, ToolDefinitions.FLINT_AND_BRICK));
  public static final ItemObject<ModifiableItem> skyStaff = ITEMS.register("sky_staff", () -> new ModifiableStaffItem(TOOL, ToolDefinitions.SKY_STAFF));
  public static final ItemObject<ModifiableItem> earthStaff = ITEMS.register("earth_staff", () -> new ModifiableStaffItem(TOOL, ToolDefinitions.EARTH_STAFF));
  public static final ItemObject<ModifiableItem> ichorStaff = ITEMS.register("ichor_staff", () -> new ModifiableStaffItem(TOOL, ToolDefinitions.ICHOR_STAFF));
  public static final ItemObject<ModifiableItem> enderStaff = ITEMS.register("ender_staff", () -> new ModifiableStaffItem(TOOL, ToolDefinitions.ENDER_STAFF));

  // armor
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> travelersGear = ITEMS.registerEnum("travelers", ArmorSlotType.values(), type -> new TravelersGearItem(ArmorDefinitions.TRAVELERS, type, TOOL));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> plateArmor = ITEMS.registerEnum("plate", ArmorSlotType.values(), type -> new PlateArmorItem(ArmorDefinitions.PLATE, type, TOOL));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> slimesuit = new EnumObject.Builder<ArmorSlotType,ModifiableArmorItem>(ArmorSlotType.class)
    .putAll(ITEMS.registerEnum("slime", new ArmorSlotType[] {ArmorSlotType.BOOTS, ArmorSlotType.LEGGINGS}, type -> new SlimesuitItem(ArmorDefinitions.SLIMESUIT, type, TOOL)))
    .put(ArmorSlotType.CHESTPLATE, ITEMS.register("slime_chestplate", () -> new SlimelytraItem(ArmorDefinitions.SLIMESUIT, TOOL)))
    .put(ArmorSlotType.HELMET, ITEMS.register("slime_helmet", () -> new SlimeskullItem(ArmorDefinitions.SLIMESUIT, TOOL)))
    .build();

  // shields
  public static final ItemObject<ModifiableItem> travelersShield = ITEMS.register("travelers_shield", () -> new ModifiableStaffItem(TOOL, ArmorDefinitions.TRAVELERS_SHIELD));
  public static final ItemObject<ModifiableItem> plateShield = ITEMS.register("plate_shield", () -> new ModifiableStaffItem(TOOL, ArmorDefinitions.PLATE_SHIELD));

  // arrows
  public static final ItemObject<ArrowItem> crystalshotItem = ITEMS.register("crystalshot", () -> new CrystalshotItem(new Item.Properties().tab(TAB_TOOLS)));

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
    TinkerHooks.init();
    ToolModuleHooks.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    ItemPredicate.register(ToolPredicate.ID, ToolPredicate::deserialize);
    ItemPredicate.register(ToolStackItemPredicate.ID, ToolStackItemPredicate::deserialize);

    // tool definition components
    // harvest
    IHarvestLogic.LOADER.register(TConstruct.getResource("effective_tag"), TagHarvestLogic.LOADER);
    IHarvestLogic.LOADER.register(TConstruct.getResource("modified_tag"), ModifiedHarvestLogic.LOADER);
    IHarvestLogic.LOADER.register(TConstruct.getResource("fixed_tier"), FixedTierHarvestLogic.LOADER);
    // aoe
    IAreaOfEffectIterator.LOADER.register(TConstruct.getResource("box"), BoxAOEIterator.LOADER);
    IAreaOfEffectIterator.LOADER.register(TConstruct.getResource("circle"), CircleAOEIterator.LOADER);
    IAreaOfEffectIterator.LOADER.register(TConstruct.getResource("tree"), TreeAOEIterator.LOADER);
    IAreaOfEffectIterator.LOADER.register(TConstruct.getResource("vein"), VeiningAOEIterator.LOADER);
    IAreaOfEffectIterator.LOADER.register(TConstruct.getResource("fallback"), FallbackAOEIterator.LOADER);
    // attack
    IWeaponAttack.LOADER.register(TConstruct.getResource("sweep"), SweepWeaponAttack.LOADER);
    IWeaponAttack.LOADER.register(TConstruct.getResource("circle"), CircleWeaponAttack.LOADER);
    IWeaponAttack.LOADER.register(TConstruct.getResource("particle"), ParticleWeaponAttack.LOADER);
    // generic tool modules
    IToolModule.LOADER.register(TConstruct.getResource("dual_option_interaction"), DualOptionInteraction.LOADER);
    IToolModule.LOADER.register(TConstruct.getResource("preference_set_interaction"), PreferenceSetInteraction.LOADER);
    // tool predicates
    ToolContextPredicate.LOADER.register(TConstruct.getResource("and"), ToolContextPredicate.AND);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("or"), ToolContextPredicate.OR);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("inverted"), ToolContextPredicate.INVERTED);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("any"), ToolContextPredicate.ANY.getLoader());
    ToolContextPredicate.LOADER.register(TConstruct.getResource("item"), ItemToolPredicate.LOADER);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("has_upgrades"), ToolContextPredicate.HAS_UPGRADES.getLoader());
    ToolContextPredicate.LOADER.register(TConstruct.getResource("has_modifier"), HasModifierPredicate.LOADER);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("stat_in_range"), StatPredicate.LOADER);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("stat_in_set"), StatInSetPredicate.LOADER);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("has_material"), HasMaterialPredicate.LOADER);
    ToolContextPredicate.LOADER.register(TConstruct.getResource("has_stat_type"), HasStatTypePredicate.LOADER);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    if (event.includeServer()) {
      generator.addProvider(new ToolsRecipeProvider(generator));
      generator.addProvider(new MaterialRecipeProvider(generator));
      MaterialDataProvider materials = new MaterialDataProvider(generator);
      generator.addProvider(materials);
      generator.addProvider(new MaterialStatsDataProvider(generator, materials));
      generator.addProvider(new MaterialTraitsDataProvider(generator, materials));
      generator.addProvider(new ToolDefinitionDataProvider(generator));
      generator.addProvider(new StationSlotLayoutProvider(generator));
      generator.addProvider(new MaterialTagProvider(generator, existingFileHelper));
    }
    if (event.includeClient()) {
      TinkerMaterialSpriteProvider materialSprites = new TinkerMaterialSpriteProvider();
      TinkerPartSpriteProvider partSprites = new TinkerPartSpriteProvider();
      generator.addProvider(new MaterialRenderInfoProvider(generator, materialSprites));
      generator.addProvider(new GeneratorPartTextureJsonGenerator(generator, TConstruct.MOD_ID, partSprites));
      generator.addProvider(new MaterialPartTextureGenerator(generator, existingFileHelper, partSprites, materialSprites));
    }
  }
}
