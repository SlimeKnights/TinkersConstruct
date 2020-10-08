package slimeknights.tconstruct.tools;

import com.google.common.collect.Streams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Common stats builder. Allows you to calculate the default stats of a tool, adhering to the standards
 * set by all TiC tools. Of course you can always set your own too.
 * <p>
 * It's encouraged to use this for the base of your calculation, and then modify the result as needed.
 */
@With
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public final class ToolStatsBuilder {

  private final List<HeadMaterialStats> heads;
  private final List<HandleMaterialStats> handles;
  private final List<ExtraMaterialStats> extras;

  public static ToolStatsBuilder from(List<IMaterial> materials, ToolDefinition toolDefinition) {
    List<PartMaterialRequirement> requiredComponents = toolDefinition.getRequiredComponents();
    if (materials.size() != requiredComponents.size()) {
      throw TinkerAPIMaterialException.statBuilderWithInvalidMaterialCount();
    }

    return new ToolStatsBuilder(
      listOfCompatibleWith(HeadMaterialStats.ID, materials, requiredComponents),
      listOfCompatibleWith(HandleMaterialStats.ID, materials, requiredComponents),
      listOfCompatibleWith(ExtraMaterialStats.ID, materials, requiredComponents)
    );
  }

  private static <T extends IMaterialStats> List<T> listOfCompatibleWith(MaterialStatsId statsId, List<IMaterial> materials, List<PartMaterialRequirement> requiredComponents) {
    return Streams.zip(materials.stream(), requiredComponents.stream(),
      (material, partMaterialType) -> ToolStatsBuilder.<T>fetchStatsOrDefault(statsId, material, partMaterialType))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  @Nullable
  private static <T extends IMaterialStats> T fetchStatsOrDefault(MaterialStatsId statsId, IMaterial material, PartMaterialRequirement requiredComponent) {
    if (requiredComponent.usesStat(statsId)) {
      return MaterialRegistry.getInstance().<T>getMaterialStats(material.getIdentifier(), statsId)
        .orElseGet(() -> MaterialRegistry.getInstance().getDefaultStats(statsId));
    } else {
      return null;
    }
  }

  public StatsNBT buildDefaultStats() {
    return new StatsNBT(
      buildDurability(),
      buildHarvestLevel(),
      buildAttack(),
      buildMiningSpeed(),
      1f,
      StatsNBT.DEFAULT_MODIFIERS,
      false
    );
  }

  public int buildDurability() {
    double averageHeadDurability = getAverageValue(heads, HeadMaterialStats::getDurability);
    double averageExtraDurability = getAverageValue(extras, ExtraMaterialStats::getDurability);
    double averageHandleDurability = getAverageValue(handles, HandleMaterialStats::getDurability);
    double averageHandleMultiplier = getAverageValue(handles, HandleMaterialStats::getDurabilityMultiplier, 1);

    double durability = (averageHeadDurability + averageExtraDurability) * averageHandleMultiplier + averageHandleDurability;

    // durability should never be below 1
    return Math.max(1, (int)durability);
  }

  public float buildMiningSpeed() {
    double averageHeadSpeed = getAverageValue(heads, HeadMaterialStats::getMiningSpeed);

    return (float)Math.max(0.1d, averageHeadSpeed);
  }

  public int buildHarvestLevel() {
    return heads.stream()
      .mapToInt(HeadMaterialStats::getHarvestLevel)
      .max()
      .orElse(0);
  }

  public float buildAttack() {
    double averageHeadAttack = getAverageValue(heads, HeadMaterialStats::getAttack);

    return (float)Math.max(0.1d, averageHeadAttack);
  }

  private <T extends IMaterialStats> double getAverageValue(List<T> stats, Function<T, ? extends Number> statGetter) {
    return getAverageValue(stats, statGetter, 0);
  }

  private <T extends IMaterialStats, N extends Number> double getAverageValue(List<T> stats, Function<T, N> statGetter, double missingValue ) {
    return stats.stream()
      .mapToDouble(value -> statGetter.apply(value).doubleValue())
      .average()
      .orElse(missingValue);
  }
}
