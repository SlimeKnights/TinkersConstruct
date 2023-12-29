package slimeknights.tconstruct.library.json.predicate.block;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.RegistrySetLoader;

import java.util.Set;

/**
 * Modifier matching a block
 */
@RequiredArgsConstructor
public
class SetBlockPredicate implements BlockPredicate {
  public static final IGenericLoader<SetBlockPredicate> LOADER = new RegistrySetLoader<>(ForgeRegistries.BLOCKS, SetBlockPredicate::new, predicate -> predicate.blocks, "blocks");

  private final Set<Block> blocks;

  @Override
  public boolean matches(BlockState state) {
    return blocks.contains(state.getBlock());
  }

  @Override
  public IGenericLoader<? extends BlockPredicate> getLoader() {
    return LOADER;
  }
}
