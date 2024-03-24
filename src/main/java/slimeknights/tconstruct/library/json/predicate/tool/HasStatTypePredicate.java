package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Checks if the tool has the given stat type.
 * @param statType    Stat type to locate.
 * @param material    If non-null, requires the given material in that stat type. If null, materials are ignored.
 */
public record HasStatTypePredicate(MaterialStatsId statType, @Nullable MaterialVariantId material) implements ToolContextPredicate {
  public static final RecordLoadable<HasStatTypePredicate> LOADER = RecordLoadable.create(
    MaterialStatsId.PARSER.requiredField("stat_type", HasStatTypePredicate::statType),
    MaterialVariantId.LOADABLE.nullableField("material", HasStatTypePredicate::material),
    HasStatTypePredicate::new);

  public HasStatTypePredicate(MaterialStatsId statType) {
    this(statType, null);
  }

  @Override
  public boolean matches(IToolContext tool) {
    List<PartRequirement> parts = tool.getDefinition().getData().getParts();
    for (int i = 0; i < parts.size(); i++) {
      if (statType.equals(parts.get(i).getStatType()) && (material == null || material.matchesVariant(tool.getMaterial(i)))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IGenericLoader<? extends ToolContextPredicate> getLoader() {
    return LOADER;
  }
}
