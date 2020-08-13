package slimeknights.tconstruct.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.harvest.AxeTool;
import slimeknights.tconstruct.tools.harvest.ExcavatorTool;
import slimeknights.tconstruct.tools.harvest.HammerTool;
import slimeknights.tconstruct.tools.harvest.KamaTool;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;
import slimeknights.tconstruct.tools.harvest.ShovelTool;
import slimeknights.tconstruct.tools.melee.BroadSword;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all complete tool items
 */
@SuppressWarnings("unused")
public final class TinkerTools extends TinkerModule {
  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.modID, "tools", () -> {
    List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getMaterials());
    if (materials.isEmpty()) {
      return new ItemStack(TinkerTools.pickaxe);
    }
    // TODO: use ToolCore::buildToolForRendering
    List<IMaterial> toolMats = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      toolMats.add(materials.get(TConstruct.random.nextInt(materials.size())));
    }
    return ToolBuildHandler.buildItemFromMaterials(TinkerTools.pickaxe.get(), toolMats);
  });

  /*
   * Items
   */
  public static final ItemObject<PickaxeTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.PICKAXE));
  public static final ItemObject<HammerTool> hammer = ITEMS.register("hammer", () -> new HammerTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.HAMMER));

  public static final ItemObject<ShovelTool> shovel = ITEMS.register("shovel", () -> new ShovelTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.SHOVEL));
  public static final ItemObject<ExcavatorTool> excavator = ITEMS.register("excavator", () -> new ExcavatorTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.EXCAVATOR));

  public static final ItemObject<AxeTool> axe = ITEMS.register("axe", () -> new AxeTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.AXE));

  public static final ItemObject<KamaTool> kama = ITEMS.register("kama", () -> new KamaTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.KAMA));

  public static final ItemObject<BroadSword> broadSword = ITEMS.register("broad_sword", () -> new BroadSword(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.BROADSWORD));

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack",() -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack",() -> new BasicParticleType(false));

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
