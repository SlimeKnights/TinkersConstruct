package slimeknights.tconstruct.library.json.predicate;

import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

/**
 * Generic interface for predicate based JSON loaders
 * TODO 1.19: replace with Mantle version (generics prevent doing so in 1.18)
 */
public interface IJsonPredicate<I> extends IHaveLoader<IJsonPredicate<I>> {
  /** Returns true if this json predicate matches the given input */
  boolean matches(I input);

  /** Inverts the given predicate */
  IJsonPredicate<I> inverted();
}
