package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;

/**
 * Predicate that checks the value of a {@link BlockVariable} falls in the given range.
 * @param variable  Variable to resolve from the block state.
 * @param min       Minimum allowed value.
 * @param max       Maximum allowed value.
 */
public record BlockVariableRangePredicate(BlockVariable variable, float min, float max, IntervalType interval) implements BlockPredicate, VariableRangePredicate {
  public static final RecordLoadable<BlockVariableRangePredicate> LOADER = RecordLoadable.create(BlockVariable.LOADER.requiredField("variable", BlockVariableRangePredicate::variable), MIN_FIELD, MAX_FIELD, INTERVAL_FIELD, BlockVariableRangePredicate::new);

  /** Creates a predicate with the given min value */
  public static BlockVariableRangePredicate min(BlockVariable variable, float min, boolean open) {
    return new BlockVariableRangePredicate(variable, min, Float.POSITIVE_INFINITY, open ? IntervalType.OPEN : IntervalType.RIGHT_OPEN);
  }

  /** Creates a predicate with the given min max */
  public static BlockVariableRangePredicate max(BlockVariable variable, float max, boolean open) {
    return new BlockVariableRangePredicate(variable, Float.NEGATIVE_INFINITY, max, open ? IntervalType.OPEN : IntervalType.LEFT_OPEN);
  }

  @Override
  public boolean matches(BlockState state) {
    return matches(variable.getValue(state));
  }

  @Override
  public RecordLoadable<BlockVariableRangePredicate> getLoader() {
    return LOADER;
  }
}
