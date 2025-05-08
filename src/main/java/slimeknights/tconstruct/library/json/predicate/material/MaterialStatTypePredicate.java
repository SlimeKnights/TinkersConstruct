package slimeknights.tconstruct.library.json.predicate.material;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

/** Predicate matching materials that can use the given stat type */
public record MaterialStatTypePredicate(MaterialStatsId statType) implements MaterialPredicate {
  public static final RecordLoadable<MaterialStatTypePredicate> LOADER = RecordLoadable.create(MaterialStatsId.PARSER.requiredField("stat_type", MaterialStatTypePredicate::statType), MaterialStatTypePredicate::new);

  @Override
  public boolean matches(MaterialVariantId variant) {
    return statType.canUseMaterial(variant.getId());
  }

  @Override
  public RecordLoadable<? extends MaterialPredicate> getLoader() {
    return LOADER;
  }
}
