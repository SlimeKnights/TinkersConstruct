package slimeknights.tconstruct.library.json.predicate;

import lombok.RequiredArgsConstructor;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;

import java.util.List;

/** Predicate that requires any child to match */
@RequiredArgsConstructor
public class OrJsonPredicate<I> implements IJsonPredicate<I> {
  private final NestedJsonPredicateLoader<I, OrJsonPredicate<I>> loader;
  private final List<IJsonPredicate<I>> children;

  @Override
  public boolean matches(I input) {
    for (IJsonPredicate<I> child : children) {
      if (child.matches(input)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IJsonPredicate<I> inverted() {
    return loader.invert(this);
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<I>> getLoader() {
    return loader;
  }

  /** Creates a new loader for the given loader registry */
  public static <I> NestedJsonPredicateLoader<I,OrJsonPredicate<I>> createLoader(GenericLoaderRegistry<IJsonPredicate<I>> loader, InvertedJsonPredicate.Loader<I> inverted) {
    return new NestedJsonPredicateLoader<>(loader, inverted, OrJsonPredicate::new, t -> t.children);
  }
}
