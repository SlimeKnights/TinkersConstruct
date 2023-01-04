package slimeknights.tconstruct.library.json.predicate.modifier;

import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.mantle.data.predicate.AndJsonPredicate;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.InvertedJsonPredicate;
import slimeknights.mantle.data.predicate.NestedJsonPredicateLoader;
import slimeknights.mantle.data.predicate.OrJsonPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/** Predicate that checks against a modifier */
public interface ModifierPredicate extends IJsonPredicate<ModifierId> {
  /** Loader for block state predicates */
  GenericLoaderRegistry<IJsonPredicate<ModifierId>> LOADER = new GenericLoaderRegistry<>(false);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<ModifierId> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<ModifierId,AndJsonPredicate<ModifierId>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<ModifierId,OrJsonPredicate<ModifierId>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);
  /** Instance that always returns true */
  ModifierPredicate ALWAYS = SingletonLoader.singleton(loader -> new ModifierPredicate() {
    @Override
    public boolean matches(ModifierId input) {
      return true;
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<ModifierId>> getLoader() {
      return loader;
    }
  });

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<ModifierId> inverted() {
    return INVERTED.create(this);
  }
}
