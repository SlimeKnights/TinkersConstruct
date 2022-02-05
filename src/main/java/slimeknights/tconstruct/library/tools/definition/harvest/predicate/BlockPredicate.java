package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

/**
 * Simple serializable block predicate
 */
public interface BlockPredicate extends IHaveLoader {
  GenericLoaderRegistry<BlockPredicate> LOADER = new GenericLoaderRegistry<>();

  /**
   * Checks if this modifier matches the block
   */
  boolean matches(BlockState state);

  /** Gets an inverted condition */
  default BlockPredicate inverted() {
    return new InvertedBlockPredicate(this);
  }
}
