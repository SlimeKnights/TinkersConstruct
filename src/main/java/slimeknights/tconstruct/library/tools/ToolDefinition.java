package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.tools.MeleeHarvestToolStatsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The data defining a tinkers tool, e.g. a pickaxe or a hammer.
 * Note that this defines the tool metadata itself, not an instance of the tool.
 * Contains information about what's needed to craft the tool, how it behaves...
 */
public class ToolDefinition {
  public static final ToolDefinition EMPTY = new ToolDefinition(new ToolBaseStatDefinition.Builder().build(), Collections::emptyList, Collections::emptyList);
  /** Stat builder for tools with no parts */
  public static final BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> NO_PARTS_STATS_BUILDER = (definition, materials) -> ToolStatsBuilder.noParts(definition);
  /** Default stat builder for melee and harvest tools */
  public static final BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> MELEE_HARVEST_STATS_BUILDER = MeleeHarvestToolStatsBuilder::from;

  /** Inherent stats of the tool. */
  private final ToolBaseStatDefinition baseStatDefinition;
  /** The tool parts required to build this tool. */
  protected final Lazy<List<IToolPart>> requiredComponents;
  /** Modifiers applied automatically by this tool */
  protected final Lazy<List<ModifierEntry>> modifiers;
  /** Function to convert from tool definition and materials into tool stats */
  protected final BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> statsBuilder;

  /** Cached indices that can be used to repair this tool */
  private int[] repairIndices;

  /**
   * Full constructor for all parameters
   * @param baseStatDefinition  Base stats
   * @param requiredComponents  Required parts to make this tool, if empty requires no parts
   * @param modifiers           Starting modifiers for this tool
   * @param statsBuilder        Logic mapping a tool definition and material list to the tool stats builder
   */
  public ToolDefinition(ToolBaseStatDefinition baseStatDefinition, Supplier<List<IToolPart>> requiredComponents, Supplier<List<ModifierEntry>> modifiers, BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> statsBuilder) {
    this.baseStatDefinition = baseStatDefinition;
    this.requiredComponents = Lazy.of(requiredComponents);
    this.modifiers = Lazy.of(modifiers);
    this.statsBuilder = statsBuilder;
  }

  /**
   * Partial constructor using the melee/harvest stats builder
   * @param baseStatDefinition  Base stats
   * @param requiredComponents  Required parts to make this tool, if empty requires no parts
   * @param modifiers           Starting modifiers for this tool
   */
  public ToolDefinition(ToolBaseStatDefinition baseStatDefinition, Supplier<List<IToolPart>> requiredComponents, Supplier<List<ModifierEntry>> modifiers) {
    this(baseStatDefinition, requiredComponents, modifiers, MELEE_HARVEST_STATS_BUILDER);
  }

  /**
   * Creates an tool definition builder
   * @param baseStats  Base stats
   * @return Definition builder
   */
  public static ToolDefinition.Builder builder(ToolBaseStatDefinition baseStats) {
    return new Builder(baseStats);
  }

  /**
   * Gets the current tools base stats definition
   *
   * @return the tools base stats definition
   */
  public ToolBaseStatDefinition getBaseStatDefinition() {
    return this.baseStatDefinition;
  }

  /**
   * Gets the required components for the given tool definition, if empty this is a no part tool
   * @return the required components
   */
  public List<IToolPart> getRequiredComponents() {
    return this.requiredComponents.get();
  }

  /** Checks if the tool uses multipart stats, equivelent to checking the required components are not empty */
  public boolean isMultipart() {
    return !getRequiredComponents().isEmpty();
  }

  /**
   * Builds the stats for this tool definition
   * @param materials  Materials list
   * @return  Stats NBT
   */
  public StatsNBT buildStats(List<IMaterial> materials) {
    return statsBuilder.apply(this, materials).buildStats();
  }

  /** Gets the modifiers applied by this tool */
  public List<ModifierEntry> getModifiers() {
    return modifiers.get();
  }

  /* Repairing */

  /** Returns a list of part material requirements for repair materials */
  public int[] getRepairParts() {
    if (repairIndices == null) {
      // get indices of all head parts
      List<IToolPart> components = requiredComponents.get();
      if (components.isEmpty()) {
        repairIndices = new int[0];
      } else {
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        repairIndices = IntStream.range(0, components.size())
                                 .filter(i -> registry.getDefaultStats(components.get(i).getStatType()) instanceof IRepairableMaterialStats)
                                 .toArray();
      }
    }
    return repairIndices;
  }

  /** Builder to easily create a tool definition */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ToolBaseStatDefinition baseStatDefinition;
    private final ImmutableList.Builder<Supplier<? extends IToolPart>> parts = ImmutableList.builder();
    private final ImmutableList.Builder<Supplier<? extends ModifierEntry>> modifiers = ImmutableList.builder();
    @Setter @Accessors(chain = true)
    private BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> statsBuilder;

    /**
     * Adds a tool part to the list of requirements
     * @param part  Part supplier
     * @return  Builder instance
     */
    public Builder addPart(Supplier<? extends IToolPart> part) {
      parts.add(part);
      return this;
    }

    /**
     * Adds a modifier to the builder
     * @param modifier  Modifier supplier
     * @param level     Modifier level
     * @return Builder instance
     */
    public Builder addModifier(Supplier<? extends Modifier> modifier, int level) {
      modifiers.add(() -> new ModifierEntry(modifier.get(), level));
      return this;
    }

    /**
     * Adds a modifier to the builder at level 1
     * @param modifier  Modifier supplier
     * @return Builder instance
     */
    public Builder addModifier(Supplier<? extends Modifier> modifier) {
      return addModifier(modifier, 1);
    }

    /**
     * Builds the final tool definition
     * @return  Tool definition
     */
    public ToolDefinition build() {
      List<Supplier<? extends IToolPart>> parts = this.parts.build();
      BiFunction<ToolDefinition,List<IMaterial>,? extends ToolStatsBuilder> statsBuilder = this.statsBuilder;
      if (statsBuilder == null) {
        statsBuilder = parts.isEmpty() ? NO_PARTS_STATS_BUILDER : MELEE_HARVEST_STATS_BUILDER;
      }
      List<Supplier<? extends ModifierEntry>> modifiers = this.modifiers.build();
      return new ToolDefinition(baseStatDefinition, supplierListToSupplier(parts), supplierListToSupplier(modifiers), statsBuilder);
    }

    /** Converts a list of suppliers into a supplier of a list, specifically without resolving suppliers */
    private static <T> Supplier<List<T>> supplierListToSupplier(List<Supplier<? extends T>> list) {
      // quick exit if no entries
      if (list.isEmpty()) {
        return Collections::emptyList;
      }
      // full supplier to convert
      return () -> {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (Supplier<? extends T> supplier : list) {
          builder.add(supplier.get());
        }
        return builder.build();
      };
    }
  }
}
