package slimeknights.tconstruct.library.json.predicate.material;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

/** Material predicate matching any tier within a range */
public record MaterialTierPredicate(IntRange tier) implements MaterialDefinitionPredicate {
  public static final IntRange TIER_RANGE = new IntRange(0, Integer.MAX_VALUE);
  public static final RecordLoadable<MaterialTierPredicate> LOADER = RecordLoadable.create(
    TIER_RANGE.requiredField("tier", MaterialTierPredicate::tier),
    MaterialTierPredicate::new);

  @Override
  public boolean matches(IMaterial material) {
    return tier.test(material.getTier());
  }

  @Override
  public RecordLoadable<? extends MaterialPredicate> getLoader() {
    return LOADER;
  }
}
