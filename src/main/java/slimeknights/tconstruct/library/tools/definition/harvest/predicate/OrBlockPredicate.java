package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;

import java.util.List;

/** Predicate that requires any child to match */
@RequiredArgsConstructor
public class OrBlockPredicate implements BlockPredicate {
  public static final IGenericLoader<OrBlockPredicate> LOADER = new NestedBlockPredicateLoader<>(OrBlockPredicate::new, p -> p.requirements);

  private final List<BlockPredicate> requirements;
  public OrBlockPredicate(BlockPredicate... predicates) {
    this(ImmutableList.copyOf(predicates));
  }

  @Override
  public boolean matches(BlockState state) {
    for (BlockPredicate requirement : requirements) {
      if (requirement.matches(state)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }
}
