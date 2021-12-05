package slimeknights.tconstruct.tools;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolPredicate;
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
import slimeknights.tconstruct.tools.item.broad.BroadAxeTool;
import slimeknights.tconstruct.tools.item.broad.CleaverTool;
import slimeknights.tconstruct.tools.item.broad.ExcavatorTool;
import slimeknights.tconstruct.tools.item.broad.ScytheTool;
import slimeknights.tconstruct.tools.item.broad.SledgeHammerTool;
import slimeknights.tconstruct.tools.item.broad.VeinHammerTool;
import slimeknights.tconstruct.tools.item.small.HandAxeTool;
import slimeknights.tconstruct.tools.item.small.HarvestTool;
import slimeknights.tconstruct.tools.item.small.KamaTool;
import slimeknights.tconstruct.tools.item.small.MattockTool;
import slimeknights.tconstruct.tools.item.small.PickaxeTool;
import slimeknights.tconstruct.tools.item.small.SweepingSwordTool;
import slimeknights.tconstruct.tools.item.small.SwordTool;
import slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher;

import java.util.function.Supplier;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {
  public TinkerTools() {
    SlotType.init();
    BlockSideHitListener.init();
    ModifierLootingHandler.init();
  }

  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.MOD_ID, "tools", () -> TinkerTools.pickaxe.get().getRenderTool());

  /*
   * Items
   */
  private static final Supplier<Item.Properties> TOOL = () -> new Item.Properties().group(TAB_TOOLS);

  public static final ItemObject<HarvestTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(TOOL.get().addToolType(ToolType.PICKAXE, 0), ToolDefinitions.PICKAXE));
  public static final ItemObject<SledgeHammerTool> sledgeHammer = ITEMS.register("sledge_hammer", () -> new SledgeHammerTool(TOOL.get().addToolType(ToolType.PICKAXE, 0), ToolDefinitions.SLEDGE_HAMMER));
  public static final ItemObject<VeinHammerTool> veinHammer = ITEMS.register("vein_hammer", () -> new VeinHammerTool(TOOL.get().addToolType(ToolType.PICKAXE, 0), ToolDefinitions.VEIN_HAMMER));

  public static final ItemObject<MattockTool> mattock = ITEMS.register("mattock", () -> new MattockTool(TOOL.get().addToolType(ToolType.SHOVEL, 0), ToolDefinitions.MATTOCK));
  public static final ItemObject<ExcavatorTool> excavator = ITEMS.register("excavator", () -> new ExcavatorTool(TOOL.get().addToolType(ToolType.SHOVEL, 0), ToolDefinitions.EXCAVATOR));

  public static final ItemObject<HandAxeTool> handAxe = ITEMS.register("hand_axe", () -> new HandAxeTool(TOOL.get().addToolType(ToolType.AXE, 0), ToolDefinitions.HAND_AXE));
  public static final ItemObject<BroadAxeTool> broadAxe = ITEMS.register("broad_axe", () -> new BroadAxeTool(TOOL.get().addToolType(ToolType.AXE, 0), ToolDefinitions.BROAD_AXE));

  public static final ItemObject<KamaTool> kama = ITEMS.register("kama", () -> new KamaTool(TOOL.get().addToolType(ToolType.HOE, 0).addToolType(ToolType.get("shears"), 0), ToolDefinitions.KAMA));
  public static final ItemObject<KamaTool> scythe = ITEMS.register("scythe", () -> new ScytheTool(TOOL.get().addToolType(ToolType.HOE, 0), ToolDefinitions.SCYTHE));

  public static final ItemObject<SwordTool> dagger = ITEMS.register("dagger", () -> new SwordTool(TOOL.get().addToolType(SwordTool.TOOL_TYPE, 0), ToolDefinitions.DAGGER));
  public static final ItemObject<SweepingSwordTool> sword = ITEMS.register("sword", () -> new SweepingSwordTool(TOOL.get().addToolType(SwordTool.TOOL_TYPE, 0), ToolDefinitions.SWORD));
  public static final ItemObject<CleaverTool> cleaver = ITEMS.register("cleaver", () -> new CleaverTool(TOOL.get().addToolType(SwordTool.TOOL_TYPE, 0), ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableItem> flintAndBronze = ITEMS.register("flint_and_bronze", () -> new ModifiableItem(TOOL.get(), ToolDefinitions.FLINT_AND_BRONZE));

  // armor
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> travelersGear = ITEMS.registerEnum("travelers", ArmorSlotType.values(), type -> new ModifiableArmorItem(ArmorDefinitions.TRAVELERS, type, TOOL.get()));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> plateArmor = ITEMS.registerEnum("plate", ArmorSlotType.values(), type -> new ModifiableArmorItem(ArmorDefinitions.PLATE, type, TOOL.get()));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> slimesuit = new EnumObject.Builder<ArmorSlotType,ModifiableArmorItem>(ArmorSlotType.class)
    .putAll(ITEMS.registerEnum("slime", new ArmorSlotType[] {ArmorSlotType.BOOTS, ArmorSlotType.LEGGINGS, ArmorSlotType.HELMET}, type -> new ModifiableArmorItem(ArmorDefinitions.SLIMESUIT, type, TOOL.get())))
    .put(ArmorSlotType.CHESTPLATE, ITEMS.register("slime_chestplate", () -> new SlimelytraItem(ArmorDefinitions.SLIMESUIT, TOOL.get())))
    .build();

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack", () -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack", () -> new BasicParticleType(false));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<IndestructibleItemEntity>> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleItemEntity>create(IndestructibleItemEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .immuneToFire();
  });

  /*
   * Events
   */

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    EquipmentChangeWatcher.register();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    ItemPredicate.register(ToolPredicate.ID, ToolPredicate::deserialize);
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
