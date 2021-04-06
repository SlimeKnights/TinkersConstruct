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
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.harvest.AxeTool;
import slimeknights.tconstruct.tools.harvest.ExcavatorTool;
import slimeknights.tconstruct.tools.harvest.HarvestTool;
import slimeknights.tconstruct.tools.harvest.KamaTool;
import slimeknights.tconstruct.tools.harvest.MattockTool;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;
import slimeknights.tconstruct.tools.harvest.SledgeHammerTool;
import slimeknights.tconstruct.tools.melee.BroadSword;

import java.util.function.Supplier;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {

  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.modID, "tools", () -> TinkerTools.pickaxe.get().buildToolForRendering());

  /*
   * Items
   */
  private static final Supplier<Item.Properties> TOOL = () -> new Item.Properties().group(TAB_TOOLS);

  public static final ItemObject<HarvestTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(TOOL.get().addToolType(ToolType.PICKAXE, 0), ToolDefinitions.PICKAXE));
  public static final ItemObject<SledgeHammerTool> sledgeHammer = ITEMS.register("sledge_hammer", () -> new SledgeHammerTool(TOOL.get().addToolType(ToolType.PICKAXE, 0), ToolDefinitions.SLEDGE_HAMMER));

  public static final ItemObject<MattockTool> mattock = ITEMS.register("mattock", () -> new MattockTool(TOOL.get().addToolType(ToolType.SHOVEL, 0), ToolDefinitions.MATTOCK));
  public static final ItemObject<ExcavatorTool> excavator = ITEMS.register("excavator", () -> new ExcavatorTool(TOOL.get().addToolType(ToolType.SHOVEL, 0), ToolDefinitions.EXCAVATOR));

  public static final ItemObject<AxeTool> axe = ITEMS.register("axe", () -> new AxeTool(TOOL.get().addToolType(ToolType.AXE, 0), ToolDefinitions.AXE));

  public static final ItemObject<KamaTool> kama = ITEMS.register("kama", () -> new KamaTool(TOOL.get().addToolType(ToolType.HOE, 0).addToolType(ToolType.get("shears"), 0), ToolDefinitions.KAMA));

  public static final ItemObject<BroadSword> broadSword = ITEMS.register("broad_sword", () -> new BroadSword(TOOL.get().addToolType(ToolType.get("sword"), 0), ToolDefinitions.BROADSWORD));

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
      datagenerator.addProvider(new MaterialDataProvider(datagenerator));
      datagenerator.addProvider(new MaterialStatsDataProvider(datagenerator));
    }
  }
}
