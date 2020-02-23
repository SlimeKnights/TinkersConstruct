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
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

  public ToolStatsBuilder() {
    this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public static ToolStatsBuilder from(List<IMaterial> materials, ToolDefinition toolDefinition) {
    List<PartMaterialType> requiredComponents = toolDefinition.getRequiredComponents();
    if (materials.size() != requiredComponents.size()) {
      throw TinkerAPIMaterialException.statBuilderWithInvalidMaterialCount();
    }

    return new ToolStatsBuilder(
      listOfCompatibleWith(HeadMaterialStats.ID, materials, requiredComponents),
      listOfCompatibleWith(HandleMaterialStats.ID, materials, requiredComponents),
      listOfCompatibleWith(ExtraMaterialStats.ID, materials, requiredComponents)
    );
  }

  private static <T extends IMaterialStats> List<T> listOfCompatibleWith(MaterialStatsId statsId, List<IMaterial> materials, List<PartMaterialType> requiredComponents) {
    return Streams.zip(materials.stream(), requiredComponents.stream(),
      (material, partMaterialType) -> (T) foo(statsId, material, partMaterialType))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  @Nullable
  private static <T extends IMaterialStats> T foo(MaterialStatsId statsId, IMaterial material, PartMaterialType requiredComponent) {
    if (requiredComponent.usesStat(statsId)) {
      return (T) MaterialRegistry.getMaterialStats(material.getIdentifier(), statsId).orElseGet(() -> MaterialRegistry.getDefaultStats(statsId));
    } else {
      return null;
    }
  }

}
