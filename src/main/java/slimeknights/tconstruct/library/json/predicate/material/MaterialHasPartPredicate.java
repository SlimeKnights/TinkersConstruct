package slimeknights.tconstruct.library.json.predicate.material;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

/** Predicate matching materials that can use the given stat type */
public record MaterialHasPartPredicate(IMaterialItem part) implements MaterialPredicate {
  public static final RecordLoadable<MaterialHasPartPredicate> LOADER = RecordLoadable.create(TinkerLoadables.MATERIAL_ITEM.requiredField("part", MaterialHasPartPredicate::part), MaterialHasPartPredicate::new);

  @Override
  public boolean matches(MaterialVariantId variant) {
    return part.canUseMaterial(variant.getId());
  }

  @Override
  public RecordLoadable<? extends MaterialPredicate> getLoader() {
    return LOADER;
  }
}
