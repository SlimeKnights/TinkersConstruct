package slimeknights.tconstruct.library.json.predicate.block;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;

public interface TinkerBlockPredicate {
  /** Predicate that matches any block */
  BlockPredicate ANY = SingletonLoader.singleton(loader -> new BlockPredicate() {
    @Override
    public boolean matches(BlockState input) {
      return true;
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<BlockState>> getLoader() {
      return loader;
    }
  });
}
