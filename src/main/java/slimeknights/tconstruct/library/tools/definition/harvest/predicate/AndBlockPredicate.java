package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;

import java.util.List;

/** Predicate that requires all children to match */
@RequiredArgsConstructor
public class AndBlockPredicate implements BlockPredicate {
  public static final IGenericLoader<AndBlockPredicate> LOADER = new NestedBlockPredicateLoader<>(AndBlockPredicate::new, p -> p.requirements);

  private final List<BlockPredicate> requirements;

  public AndBlockPredicate(BlockPredicate... predicates) {
    this(ImmutableList.copyOf(predicates));
  }

  @Override
  public boolean matches(BlockState state) {
    for (BlockPredicate requirement : requirements) {
      if (!requirement.matches(state)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }
}
