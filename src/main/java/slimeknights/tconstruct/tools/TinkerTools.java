package slimeknights.tconstruct.tools;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
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
  public static final ItemGroup TAB_TOOLS = FabricItemGroupBuilder.build(id("tools"), () -> TinkerTools.pickaxe.get().buildToolForRendering());

  /*
   * Items
   */
  private static final Supplier<FabricItemSettings> TOOL = () -> new FabricItemSettings().group(TAB_TOOLS);

  public static final ItemObject<HarvestTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(TOOL.get(), ToolDefinitions.PICKAXE));
  public static final ItemObject<SledgeHammerTool> sledgeHammer = ITEMS.register("sledge_hammer", () -> new SledgeHammerTool(TOOL.get(), ToolDefinitions.SLEDGE_HAMMER));

  public static final ItemObject<MattockTool> mattock = ITEMS.register("mattock", () -> new MattockTool(TOOL.get(), ToolDefinitions.MATTOCK));
  public static final ItemObject<ExcavatorTool> excavator = ITEMS.register("excavator", () -> new ExcavatorTool(TOOL.get(), ToolDefinitions.EXCAVATOR));

  public static final ItemObject<AxeTool> axe = ITEMS.register("axe", () -> new AxeTool(TOOL.get(), ToolDefinitions.AXE));

  public static final ItemObject<KamaTool> kama = ITEMS.register("kama", () -> new KamaTool(TOOL.get(), ToolDefinitions.KAMA));

  public static final ItemObject<BroadSword> broadSword = ITEMS.register("broad_sword", () -> new BroadSword(TOOL.get(), ToolDefinitions.BROADSWORD));

  /*
   * Particles
   */
  public static final DefaultParticleType hammerAttackParticle = Registry.register(Registry.PARTICLE_TYPE, id("hammer_attack"), FabricParticleTypes.simple());
  public static final DefaultParticleType axeAttackParticle = Registry.register(Registry.PARTICLE_TYPE, id("axe_attack"), FabricParticleTypes.simple());

  /*
   * Entities
   */
  public static final EntityType<IndestructibleEntityItem> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleEntityItem>create(IndestructibleEntityItem::new, SpawnGroup.MISC)
      .setDimensions(0.25F, 0.25F)
      .makeFireImmune();
  });

  @Override
  public void onInitialize() {
  }
}
