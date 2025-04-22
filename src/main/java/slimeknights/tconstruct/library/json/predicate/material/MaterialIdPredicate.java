package slimeknights.tconstruct.library.json.predicate.material;

import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Set;

/** Predicate matching materials with an exact ID, ignoring variants */
public record MaterialIdPredicate(Set<MaterialId> ids) implements MaterialPredicate {
  public static final RecordLoadable<MaterialIdPredicate> LOADER = RecordLoadable.create(MaterialId.PARSER.set(ArrayLoadable.COMPACT).requiredField("id", MaterialIdPredicate::ids), MaterialIdPredicate::new);

  public MaterialIdPredicate(MaterialId... ids) {
    this(Set.of(ids));
  }

  @Override
  public boolean matches(MaterialVariantId variant) {
    return ids.contains(variant.getId());
  }

  @Override
  public RecordLoadable<? extends MaterialPredicate> getLoader() {
    return LOADER;
  }
}
