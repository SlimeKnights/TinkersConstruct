package slimeknights.tconstruct.library.tools.stat;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * Extendable utilities for a stats builder.
 * <p>
 * It's encouraged to extend this for the base of your calculation. Using this class directly will give a no parts stat builder
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolStatsBuilder {
  /** Tool base stats, primarily for bonuses. The stat builder is responsible for using the bonuses */
  protected final ToolDefinitionData toolData;

  /**
   * Gets the stat builder for no tool parts
   * @param definition  Tool definition
   * @return  Stats builder
   */
  public static ToolStatsBuilder noParts(ToolDefinition definition) {
    return new ToolStatsBuilder(definition.getData());
  }

  /**
   * Called after bonuses are processed to set the unique stats for this builder.
   * Any stats added to the builder here need to return true in {@link #handles(IToolStat)} to prevent errors on tool building with tool definitions setting those stats
   * @param builder  Stats builder
   */
  protected void setStats(StatsNBT.Builder builder) {}

  /**
   * Checks if the given stat type is handled by this builder and should be skipped in the bonuses
   * @param stat  Stat type
   * @return  True if handled
   */
  protected boolean handles(IToolStat<?> stat) {
    return false;
  }

  /** Builds default stats */
  public StatsNBT buildStats() {
    StatsNBT.Builder builder = StatsNBT.builder();
    // start by adding in all relevant bonuses that are not handled elsewhere.
    // the handled check is needed becuase the immutable map builder does not like duplicate keys
    for (FloatToolStat stat : toolData.getAllBaseStats()) {
      if (!handles(stat)) {
        builder.set(stat, toolData.getBaseStat(stat));
      }
    }
    setStats(builder);
    return builder.build();
  }


  /* Helpers */

  /**
   * Fetches the given stat from the material, getting the default stats if missing
   * @param material   Material type
   * @param statsId    Stat type
   * @param <T>  Stat type
   * @return  Stat, or default if the part type accepts it, null if the part type does not
   */
  @Nullable
  public static <T extends IMaterialStats> T fetchStatsOrDefault(IMaterial material, MaterialStatsId statsId) {
      return MaterialRegistry.getInstance().<T>getMaterialStats(material.getIdentifier(), statsId)
        .orElseGet(() -> MaterialRegistry.getInstance().getDefaultStats(statsId));
  }

  /**
   * Gets a list of all stats for the given part type
   * @param statsId             Stat type
   * @param materials           Materials list
   * @param parts  List of required components, filters stat types
   * @param <T>  Type of stats
   * @return  List of stats
   */
  public static <T extends IMaterialStats> List<T> listOfCompatibleWith(MaterialStatsId statsId, List<IMaterial> materials, List<PartRequirement> parts) {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    // iterating both lists at once, precondition that they have the same size
    int size = parts.size();
    for (int i = 0; i < size; i++) {
      // ensure stat type is valid
      PartRequirement part = parts.get(i);
      if (part.getStatType().equals(statsId)) {
        T stats = fetchStatsOrDefault(materials.get(i), part.getStatType());
        if (stats != null) {
          // add a copy of the stat once per weight, lazy way to do weighting
          for (int w = 0; w < part.getWeight(); w++) {
            builder.add(stats);
          }
        }
      }
    }
    return builder.build();
  }

  /**
   * Gets the average value from a list of stat types
   * @param stats       Stat list
   * @param statGetter  Function to get the value
   * @param <T>  Material type
   * @return  Average value
   */
  public static <T extends IMaterialStats> double getAverageValue(List<T> stats, Function<T, ? extends Number> statGetter) {
    return getAverageValue(stats, statGetter, 0);
  }

  /**
   * Gets the average value from a list of stat types
   * @param stats         Stat list
   * @param statGetter    Function to get the value
   * @param missingValue  Default value to use for missing stats
   * @param <T>  Material type
   * @return  Average value
   */
  public static <T extends IMaterialStats, N extends Number> double getAverageValue(List<T> stats, Function<T, N> statGetter, double missingValue) {
    return stats.stream()
                .mapToDouble(value -> statGetter.apply(value).doubleValue())
                .average()
                .orElse(missingValue);
  }
}
