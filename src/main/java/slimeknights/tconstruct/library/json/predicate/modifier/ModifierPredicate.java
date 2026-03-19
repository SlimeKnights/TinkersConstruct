package slimeknights.tconstruct.library.json.predicate.modifier;

import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.PredicateRegistry;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.List;
import java.util.function.Predicate;

/** Predicate that checks against a modifier */
public interface ModifierPredicate extends IJsonPredicate<ModifierId> {
  /** Instance that always returns true */
  ModifierPredicate ANY = simple(modifier -> true);
  /** Instance that always returns false */
  ModifierPredicate NONE = simple(modifier -> false);
  /** Loader for modifier predicates */
  PredicateRegistry<ModifierId> LOADER = new PredicateRegistry<>("Modifier Predicate", ANY, NONE);

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<ModifierId> inverted() {
    return LOADER.invert(this);
  }

  @Override
  RecordLoadable<? extends ModifierPredicate> getLoader();


  /* Helper methods */

  /** Creates a new simple predicate */
  static ModifierPredicate simple(Predicate<ModifierId> predicate) {
    return SingletonLoader.singleton(loader -> new ModifierPredicate() {
      @Override
      public boolean matches(ModifierId modifier) {
        return predicate.test(modifier);
      }

      @Override
      public RecordLoadable<? extends ModifierPredicate> getLoader() {
        return loader;
      }
    });
  }

  /** Creates a tag predicate */
  @SuppressWarnings("removal")
  static IJsonPredicate<ModifierId> tag(TagKey<Modifier> tag) {
    return new TagModifierPredicate(tag);
  }

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
