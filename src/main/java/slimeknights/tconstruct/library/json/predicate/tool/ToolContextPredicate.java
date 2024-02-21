package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.AndJsonPredicate;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.InvertedJsonPredicate;
import slimeknights.mantle.data.predicate.NestedJsonPredicateLoader;
import slimeknights.mantle.data.predicate.OrJsonPredicate;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Simple serializable tool predicate */
public interface ToolContextPredicate extends IJsonPredicate<IToolContext> {
  /** Predicate that matches all tools */
  ToolContextPredicate ANY = singleton(loader -> new ToolContextPredicate() {
    @Override
    public boolean matches(IToolContext input) {
      return true;
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
      return loader;
    }
  });

  /** Loader for tool predicates */
  GenericLoaderRegistry<IJsonPredicate<IToolContext>> LOADER = new GenericLoaderRegistry<>(ANY, true);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<IToolContext> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<IToolContext,AndJsonPredicate<IToolContext>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<IToolContext,OrJsonPredicate<IToolContext>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);


  /** Predicate checking for a tool having any upgrades */
  ToolContextPredicate HAS_UPGRADES = singleton(loader -> new ToolContextPredicate() {
    @Override
    public boolean matches(IToolContext tool) {
      return !tool.getUpgrades().isEmpty();
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
      return loader;
    }
  });

  @Override
  default IJsonPredicate<IToolContext> inverted() {
    return INVERTED.create(this);
  }
}
