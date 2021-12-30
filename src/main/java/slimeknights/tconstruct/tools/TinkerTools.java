package slimeknights.tconstruct.tools;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
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
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.loot.RandomMaterial;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolPredicate;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
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
import slimeknights.tconstruct.tools.item.SlimelytraItem;
import slimeknights.tconstruct.tools.item.SlimeskullItem;
import slimeknights.tconstruct.tools.item.broad.BroadAxeTool;
import slimeknights.tconstruct.tools.item.broad.CleaverTool;
import slimeknights.tconstruct.tools.item.broad.ExcavatorTool;
import slimeknights.tconstruct.tools.item.broad.ScytheTool;
import slimeknights.tconstruct.tools.item.broad.SledgeHammerTool;
import slimeknights.tconstruct.tools.item.broad.VeinHammerTool;
import slimeknights.tconstruct.tools.item.small.DaggerTool;
import slimeknights.tconstruct.tools.item.small.HandAxeTool;
import slimeknights.tconstruct.tools.item.small.HarvestTool;
import slimeknights.tconstruct.tools.item.small.KamaTool;
import slimeknights.tconstruct.tools.item.small.MattockTool;
import slimeknights.tconstruct.tools.item.small.PickaxeTool;
import slimeknights.tconstruct.tools.item.small.SweepingSwordTool;
import slimeknights.tconstruct.tools.item.small.SwordTool;
import slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import java.util.function.Supplier;

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
  public static LootItemFunctionType lootAddToolData;

  /*
   * Items
   */
  private static final Supplier<Item.Properties> TOOL = () -> new Item.Properties().tab(TAB_TOOLS);

  public static final ItemObject<HarvestTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(TOOL.get(), ToolDefinitions.PICKAXE));
  public static final ItemObject<SledgeHammerTool> sledgeHammer = ITEMS.register("sledge_hammer", () -> new SledgeHammerTool(TOOL.get(), ToolDefinitions.SLEDGE_HAMMER));
  public static final ItemObject<VeinHammerTool> veinHammer = ITEMS.register("vein_hammer", () -> new VeinHammerTool(TOOL.get(), ToolDefinitions.VEIN_HAMMER));

  public static final ItemObject<MattockTool> mattock = ITEMS.register("mattock", () -> new MattockTool(TOOL.get(), ToolDefinitions.MATTOCK));
  public static final ItemObject<ExcavatorTool> excavator = ITEMS.register("excavator", () -> new ExcavatorTool(TOOL.get(), ToolDefinitions.EXCAVATOR));

  public static final ItemObject<HandAxeTool> handAxe = ITEMS.register("hand_axe", () -> new HandAxeTool(TOOL.get(), ToolDefinitions.HAND_AXE));
  public static final ItemObject<BroadAxeTool> broadAxe = ITEMS.register("broad_axe", () -> new BroadAxeTool(TOOL.get(), ToolDefinitions.BROAD_AXE));

  public static final ItemObject<KamaTool> kama = ITEMS.register("kama", () -> new KamaTool(TOOL.get(), ToolDefinitions.KAMA));
  public static final ItemObject<KamaTool> scythe = ITEMS.register("scythe", () -> new ScytheTool(TOOL.get(), ToolDefinitions.SCYTHE));

  public static final ItemObject<SwordTool> dagger = ITEMS.register("dagger", () -> new DaggerTool(TOOL.get(), ToolDefinitions.DAGGER));
  public static final ItemObject<SweepingSwordTool> sword = ITEMS.register("sword", () -> new SweepingSwordTool(TOOL.get(), ToolDefinitions.SWORD));
  public static final ItemObject<CleaverTool> cleaver = ITEMS.register("cleaver", () -> new CleaverTool(TOOL.get(), ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableItem> flintAndBronze = ITEMS.register("flint_and_bronze", () -> new ModifiableItem(TOOL.get(), ToolDefinitions.FLINT_AND_BRONZE));

  // armor
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> travelersGear = ITEMS.registerEnum("travelers", ArmorSlotType.values(), type -> new ModifiableArmorItem(ArmorDefinitions.TRAVELERS, type, TOOL.get()));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> plateArmor = ITEMS.registerEnum("plate", ArmorSlotType.values(), type -> new ModifiableArmorItem(ArmorDefinitions.PLATE, type, TOOL.get()));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> slimesuit = new EnumObject.Builder<ArmorSlotType,ModifiableArmorItem>(ArmorSlotType.class)
    .putAll(ITEMS.registerEnum("slime", new ArmorSlotType[] {ArmorSlotType.BOOTS, ArmorSlotType.LEGGINGS}, type -> new ModifiableArmorItem(ArmorDefinitions.SLIMESUIT, type, TOOL.get())))
    .put(ArmorSlotType.CHESTPLATE, ITEMS.register("slime_chestplate", () -> new SlimelytraItem(ArmorDefinitions.SLIMESUIT, TOOL.get())))
    .put(ArmorSlotType.HELMET, ITEMS.register("slime_helmet", () -> new SlimeskullItem(ArmorDefinitions.SLIMESUIT, TOOL.get())))
    .build();

  /* Particles */
  public static final RegistryObject<SimpleParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack", () -> new SimpleParticleType(false));
  public static final RegistryObject<SimpleParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack", () -> new SimpleParticleType(false));

  /* Entities */
  public static final RegistryObject<EntityType<IndestructibleItemEntity>> indestructibleItem = ENTITIES.register("indestructible_item", () ->
    EntityType.Builder.<IndestructibleItemEntity>of(IndestructibleItemEntity::new, MobCategory.MISC)
                      .sized(0.25F, 0.25F)
                      .fireImmune());

  /* Containers */
  public static final RegistryObject<MenuType<ToolContainerMenu>> toolContainer = CONTAINERS.register("tool_container", ToolContainerMenu::forClient);


  /*
   * Events
   */

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    EquipmentChangeWatcher.register();
    ToolCapabilityProvider.register(ToolFluidCapability.Provider::new);
    ToolCapabilityProvider.register(ToolInventoryCapability.Provider::new);
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    ItemPredicate.register(ToolPredicate.ID, ToolPredicate::deserialize);
    lootAddToolData = Registry.register(Registry.LOOT_FUNCTION_TYPE, AddToolDataFunction.ID, new LootItemFunctionType(AddToolDataFunction.SERIALIZER));
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    if (event.includeServer()) {
      generator.addProvider(new ToolsRecipeProvider(generator));
      generator.addProvider(new MaterialRecipeProvider(generator));
      generator.addProvider(new ModifierRecipeProvider(generator));
      MaterialDataProvider materials = new MaterialDataProvider(generator);
      generator.addProvider(materials);
      generator.addProvider(new MaterialStatsDataProvider(generator, materials));
      generator.addProvider(new MaterialTraitsDataProvider(generator, materials));
      generator.addProvider(new ToolDefinitionDataProvider(generator));
      generator.addProvider(new StationSlotLayoutProvider(generator));
    }
    if (event.includeClient()) {
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      TinkerMaterialSpriteProvider materialSprites = new TinkerMaterialSpriteProvider();
      TinkerPartSpriteProvider partSprites = new TinkerPartSpriteProvider();
      generator.addProvider(new MaterialRenderInfoProvider(generator, materialSprites));
      generator.addProvider(new GeneratorPartTextureJsonGenerator(generator, TConstruct.MOD_ID, partSprites));
      generator.addProvider(new MaterialPartTextureGenerator(generator, existingFileHelper, partSprites, materialSprites));
    }
  }
}
