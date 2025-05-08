package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicateField;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Tool predicate checking for the given material on the tool
 * @param material   Material variant to locate.
 * @param index      Index to check for the material. If -1, will check all materials on the tool.
 */
public record HasMaterialPredicate(IJsonPredicate<MaterialVariantId> material, int index) implements ToolContextPredicate {
  public static final RecordLoadable<HasMaterialPredicate> LOADER = RecordLoadable.create(
    new MaterialPredicateField<>("material", HasMaterialPredicate::material),
    IntLoadable.FROM_MINUS_ONE.defaultField("index", -1, HasMaterialPredicate::index),
    HasMaterialPredicate::new);

  public HasMaterialPredicate(MaterialVariantId material, int index) {
    this(MaterialPredicate.variant(material), index);
  }

  public HasMaterialPredicate(MaterialVariantId material) {
    this(material, -1);
  }

  @Override
  public boolean matches(IToolContext input) {
    // if given an index, use exact location match
    if (index >= 0) {
      return material.matches(input.getMaterial(index).getVariant());
    }
    // otherwise, search each material
    for (MaterialVariant variant : input.getMaterials().getList()) {
      if (material.matches(variant.getVariant())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public RecordLoadable<HasMaterialPredicate> getLoader() {
    return LOADER;
  }
}
