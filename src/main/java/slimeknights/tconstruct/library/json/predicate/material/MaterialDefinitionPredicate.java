package slimeknights.tconstruct.library.json.predicate.material;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.function.Predicate;

public interface MaterialDefinitionPredicate extends MaterialPredicate {
  /** Checks if the predicate matches the given material definition */
  boolean matches(IMaterial material);

  @Override
  default boolean matches(MaterialVariantId input) {
    return matches(MaterialRegistry.getMaterial(input.getId()));
  }


  /* Simple */

  /** Checks that the material is not hidden */
  MaterialDefinitionPredicate NOT_HIDDEN = simple(mat -> !mat.isHidden());
  /** Checks that the material can be crafted */
  MaterialDefinitionPredicate CRAFTABLE = simple(IMaterial::isCraftable);
  /** Checks that the material exists in the material registry */
  MaterialDefinitionPredicate REGISTERED = simple(mat -> mat != IMaterial.UNKNOWN);

  /** Creates a new simple predicate */
  static MaterialDefinitionPredicate simple(Predicate<IMaterial> predicate) {
    return SingletonLoader.singleton(loader -> new MaterialDefinitionPredicate() {
      @Override
      public boolean matches(IMaterial material) {
        return predicate.test(material);
      }

      @Override
      public RecordLoadable<? extends MaterialPredicate> getLoader() {
        return loader;
      }
    });
  }
}
