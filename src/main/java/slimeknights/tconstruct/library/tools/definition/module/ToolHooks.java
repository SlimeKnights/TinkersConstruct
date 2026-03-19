package slimeknights.tconstruct.library.tools.definition.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionToolHook;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolStatsHook;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import slimeknights.tconstruct.library.tools.definition.module.build.VolatileDataToolHook;
import slimeknights.tconstruct.library.tools.definition.module.display.MaterialToolName;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.definition.module.interaction.InteractionToolModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook.MaxMerger;
import slimeknights.tconstruct.library.tools.definition.module.material.MissingMaterialsToolHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningTierToolHook;
import slimeknights.tconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** Modules for tool definition data */
public class ToolHooks {
  private ToolHooks() {}

  /** Loader for tool hooks */
  public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Unknown Tool Hook");

  public static void init() {}


  /* Build */
  /** Hook for getting the material requirements for a tool. */
  public static final ModuleHook<ToolMaterialHook> TOOL_MATERIALS = register("tool_materials", ToolMaterialHook.class, definition -> List.of());
  /** Hook for getting a list of tool parts on a tool. */
  public static final ModuleHook<ToolPartsHook> TOOL_PARTS = register("tool_parts", ToolPartsHook.class, definition -> List.of());
  /** Hook for filling materials on a tool with no materials set */
  public static final ModuleHook<MissingMaterialsToolHook> MISSING_MATERIALS = register("missing_materials", MissingMaterialsToolHook.class, new MissingMaterialsToolHook() {
    @Override
    public MaterialNBT fillMaterials(ToolDefinition definition, RandomSource random) {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      for (MaterialStatsId statType : ToolMaterialHook.stats(definition)) {
        builder.add(MaterialRegistry.firstWithStatType(statType));
      }
      return builder.build();
    }

    @Override
    public MaterialNBT fillMaterials(ToolDefinition definition, MaterialNBT existing, RandomSource random) {
      List<MaterialStatsId> stats = ToolMaterialHook.stats(definition);
      // add original materials
      MaterialNBT.Builder builder = MaterialNBT.builder();
      builder.addAll(existing);
      // fill in missing materials from first with stat type
      for (int i = existing.size(); i < stats.size(); i++) {
        builder.add(MaterialRegistry.firstWithStatType(stats.get(i)));
      }
      return builder.build();
    }
  });

  /** Hook for repairing a tool using a material. */
  public static final ModuleHook<MaterialRepairToolHook> MATERIAL_REPAIR = register("material_repair", MaterialRepairToolHook.class, MaxMerger::new, new MaterialRepairToolHook() {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, MaterialId material) {
      return 0;
    }
  });

  /** Hook for adding raw unconditional stats to a tool */
  public static final ModuleHook<ToolStatsHook> TOOL_STATS = register("tool_stats", ToolStatsHook.class, ToolStatsHook.AllMerger::new, (context, builder) -> {});
  /** Hook for checking if a tool can perform a given action. */
  public static final ModuleHook<VolatileDataToolHook> VOLATILE_DATA = register("volatile_data", VolatileDataToolHook.class, VolatileDataToolHook.AllMerger::new, (context, data) -> {});
  /** Hook for fetching tool traits */
  public static final ModuleHook<ToolTraitHook> TOOL_TRAITS;
  /** Hook for fetching traits for the rebalanced modifier */
  public static final ModuleHook<ToolTraitHook> REBALANCED_TRAIT;
  static {
    Function<Collection<ToolTraitHook>,ToolTraitHook> merger = ToolTraitHook.AllMerger::new;
    ToolTraitHook defaultInstance = (definition, materials, builder) -> {};
    TOOL_TRAITS = register("tool_traits", ToolTraitHook.class, merger, defaultInstance);
    REBALANCED_TRAIT = register("rebalanced_trait", ToolTraitHook.class, merger, defaultInstance);
  }
  /** Hook for checking if a tool can perform a given action. */
  public static final ModuleHook<ToolActionToolHook> TOOL_ACTION = register("tool_actions", ToolActionToolHook.class, ToolActionToolHook.AnyMerger::new, (tool, action) -> false);


  /* Mining */
  /** Hook for checking if a tool is effective against the given block */
  public static final ModuleHook<IsEffectiveToolHook> IS_EFFECTIVE = register("is_effective", IsEffectiveToolHook.class, (tool, state) -> false);
  /** Hook for modifying the tier from the stat */
  public static final ModuleHook<MiningTierToolHook> MINING_TIER = register("mining_tier", MiningTierToolHook.class, MiningTierToolHook.ComposeMerger::new, (tool, tier) -> tier);
  /** Hook for modifying the mining speed from the stat/effectiveness */
  public static final ModuleHook<MiningSpeedToolHook> MINING_SPEED = register("mining_speed_modifier", MiningSpeedToolHook.class, MiningSpeedToolHook.ComposeMerger::new, (tool, state, speed) -> speed);
  /** Logic for finding AOE blocks */
  public static final ModuleHook<AreaOfEffectIterator> AOE_ITERATOR = register("aoe_iterator", AreaOfEffectIterator.class, (tool, context, state, match) -> Collections.emptyList());


  /* Weapon */
  /** Hook that runs after a melee hit to apply extra effects. */
  public static final ModuleHook<MeleeHitToolHook> MELEE_HIT = register("after_melee_hit", MeleeHitToolHook.class, MeleeHitToolHook.AllMerger::new, (tool, context, damage) -> {});


  /** Hook for configuring interaction behaviors on the tool */
  public static final ModuleHook<InteractionToolModule> INTERACTION = register("tool_interaction", InteractionToolModule.class, (t, m, s) -> true);


  /* Display */
  /** Hook for setting the display name on a tool */ // TODO 1.21: make the default show no materials?
  public static final ModuleHook<ToolNameHook> DISPLAY_NAME = register("display_name", ToolNameHook.class, (MaterialToolName) (index, statType, material) -> MaterialRegistry.getInstance().canRepair(statType));


  /* Registration */

  /** Registers a new tool hook that merges */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return LOADER.register(new ModuleHook<>(name, filter, merger, defaultInstance));
  }

  /** Registers a new tool hook that does not merge */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }

  /** Registers a new tool hook under {@code tconstruct} that merges */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return register(TConstruct.getResource(name), filter, merger, defaultInstance);
  }

  /** Registers a new tool hook under {@code tconstruct} that cannot merge */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
