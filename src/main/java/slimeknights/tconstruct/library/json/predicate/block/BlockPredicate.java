package slimeknights.tconstruct.library.json.predicate.block;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.AndJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.InvertedJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.NestedJsonPredicateLoader;
import slimeknights.tconstruct.library.json.predicate.OrJsonPredicate;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/**
 * Simple serializable block predicate
 * @deprecated use {@link slimeknights.mantle.data.predicate.block.BlockPredicate}
 */
@Deprecated
public interface BlockPredicate extends IJsonPredicate<BlockState> {
  /** Loader for block state predicates */
  GenericLoaderRegistry<IJsonPredicate<BlockState>> LOADER = new GenericLoaderRegistry<>(true);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<BlockState> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<BlockState,AndJsonPredicate<BlockState>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<BlockState,OrJsonPredicate<BlockState>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<BlockState> inverted() {
    return INVERTED.create(this);
  }


  /* Singleton */

  /** Predicate that matches blocks with no harvest tool */
  BlockPredicate REQUIRES_TOOL = singleton(loader -> new BlockPredicate() {
    @Override
    public boolean matches(BlockState input) {
      return input.requiresCorrectToolForDrops();
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<BlockState>> getLoader() {
      return loader;
    }
  });
}
