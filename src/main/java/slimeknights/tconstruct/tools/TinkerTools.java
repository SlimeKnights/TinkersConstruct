package slimeknights.tconstruct.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tools.helper.BlockSideHitListener;
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.harvest.HandAxeTool;
import slimeknights.tconstruct.tools.harvest.HarvestTool;
import slimeknights.tconstruct.tools.harvest.KamaTool;
import slimeknights.tconstruct.tools.harvest.MattockTool;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;
import slimeknights.tconstruct.tools.harvest.broad.BroadAxeTool;
import slimeknights.tconstruct.tools.harvest.broad.ExcavatorTool;
import slimeknights.tconstruct.tools.harvest.broad.ScytheTool;
import slimeknights.tconstruct.tools.harvest.broad.SledgeHammerTool;
import slimeknights.tconstruct.tools.harvest.broad.VeinHammerTool;
import slimeknights.tconstruct.tools.melee.BroadSwordTool;
import slimeknights.tconstruct.tools.melee.CleaverTool;

import java.util.function.Supplier;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {
  public TinkerTools() {
    BlockSideHitListener.init();
  }

  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.modID, "tools", () -> TinkerTools.pickaxe.get().buildToolForRendering());

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

  public static final ItemObject<BroadSwordTool> broadSword = ITEMS.register("broad_sword", () -> new BroadSwordTool(TOOL.get().addToolType(ToolType.get("sword"), 0), ToolDefinitions.BROADSWORD));
  public static final ItemObject<BroadSwordTool> cleaver = ITEMS.register("cleaver", () -> new CleaverTool(TOOL.get().addToolType(ToolType.get("sword"), 0), ToolDefinitions.CLEAVER));

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack", () -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack", () -> new BasicParticleType(false));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<IndestructibleEntityItem>> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleEntityItem>create(IndestructibleEntityItem::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .immuneToFire();
  });

  /*
   * Events
   */

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new ToolsRecipeProvider(datagenerator));
      datagenerator.addProvider(new ModifierRecipeProvider(datagenerator));
      datagenerator.addProvider(new MaterialDataProvider(datagenerator));
      datagenerator.addProvider(new MaterialStatsDataProvider(datagenerator));
    }
  }
}
