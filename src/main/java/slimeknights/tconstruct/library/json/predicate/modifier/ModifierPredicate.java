package slimeknights.tconstruct.library.json.predicate.modifier;

import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.PredicateRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.List;

/** Predicate that checks against a modifier */
public interface ModifierPredicate extends IJsonPredicate<ModifierId> {
  /** Instance that always returns true */
  ModifierPredicate ANY = SingletonLoader.singleton(loader -> new ModifierPredicate() {
    @Override
    public boolean matches(ModifierId input) {
      return true;
    }

    @Override
    public IGenericLoader<? extends ModifierPredicate> getLoader() {
      return loader;
    }
  });
  /** Loader for block state predicates */
  PredicateRegistry<ModifierId> LOADER = new PredicateRegistry<>(ANY);

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<ModifierId> inverted() {
    return LOADER.invert(this);
  }

  @Override
  IGenericLoader<? extends ModifierPredicate> getLoader();


  /* Helper methods */

  /** Creates an and predicate */
  @SafeVarargs
  static IJsonPredicate<ModifierId> and(IJsonPredicate<ModifierId>... predicates) {
    return LOADER.and(List.of(predicates));
  }

  /** Creates an or predicate */
  @SafeVarargs
  static IJsonPredicate<ModifierId> or(IJsonPredicate<ModifierId>... predicates) {
    return LOADER.or(List.of(predicates));
  }
}
