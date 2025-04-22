package slimeknights.tconstruct.library.json.predicate.material;

import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.TagPredicateRegistry;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.List;

/** Predicate that checks against a material variant */
public interface MaterialPredicate extends IJsonPredicate<MaterialVariantId> {
  /** Instance that always returns true */
  MaterialPredicate ANY = SingletonLoader.singleton(loader -> new MaterialPredicate() {
    @Override
    public boolean matches(MaterialVariantId input) {
      return true;
    }

    @Override
    public RecordLoadable<? extends MaterialPredicate> getLoader() {
      return loader;
    }
  });
  /** Loader for material predicates */
  TagPredicateRegistry<IMaterial,MaterialVariantId> LOADER = new TagPredicateRegistry<>("Material Predicate", ANY, TinkerLoadables.MATERIAL_TAGS, (tag, source) -> MaterialRegistry.getInstance().isInTag(source.getId(), tag));

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<MaterialVariantId> inverted() {
    return LOADER.invert(this);
  }

  @Override
  RecordLoadable<? extends MaterialPredicate> getLoader();


  /* Helper methods */

  /** Creates a classic style predicate from a material, handling exact ID and the default variant. */
  static MaterialPredicate variant(MaterialVariantId material) {
    String variant = material.getVariant();
    if (variant.isEmpty()) {
      return new MaterialIdPredicate(material.getId());
    }
    // default variant means exact match on ID
    if (MaterialVariantId.DEFAULT_VARIANT.equals(variant)) {
      return new MaterialVariantPredicate(material.getId());
    }
    // anything else is exact match on material
    return new MaterialVariantPredicate(material);
  }

  /** Creates a tag predicate */
  static IJsonPredicate<MaterialVariantId> tag(TagKey<IMaterial> tag) {
    return LOADER.tag(tag);
  }

  /** Creates an and predicate */
  @SafeVarargs
  static IJsonPredicate<MaterialVariantId> and(IJsonPredicate<MaterialVariantId>... predicates) {
    return LOADER.and(List.of(predicates));
  }

  /** Creates an or predicate */
  @SafeVarargs
  static IJsonPredicate<MaterialVariantId> or(IJsonPredicate<MaterialVariantId>... predicates) {
    return LOADER.or(List.of(predicates));
  }
}
