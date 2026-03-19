package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.predicate.VariableRangePredicate;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Predicate that checks the value of a {@link ToolVariable} falls in the given range.
 * @param variable  Variable to resolve from the tool.
 * @param min       Minimum allowed value.
 * @param max       Maximum allowed value.
 */
public record ToolVariableRangePredicate(ToolVariable variable, float min, float max, IntervalType interval) implements ToolStackPredicate, VariableRangePredicate {
  public static final RecordLoadable<ToolVariableRangePredicate> LOADER = RecordLoadable.create(ToolVariable.LOADER.requiredField("variable", ToolVariableRangePredicate::variable), MIN_FIELD, MAX_FIELD, INTERVAL_FIELD, ToolVariableRangePredicate::new);

  /** Creates a predicate with the given min value */
  public static ToolVariableRangePredicate min(ToolVariable variable, float min, boolean open) {
    return new ToolVariableRangePredicate(variable, min, Float.POSITIVE_INFINITY, open ? IntervalType.OPEN : IntervalType.RIGHT_OPEN);
  }

  /** Creates a predicate with the given min max */
  public static ToolVariableRangePredicate max(ToolVariable variable, float max, boolean open) {
    return new ToolVariableRangePredicate(variable, Float.NEGATIVE_INFINITY, max, open ? IntervalType.OPEN : IntervalType.LEFT_OPEN);
  }

  @Override
  public boolean matches(IToolStackView tool) {
    return matches(variable.getValue(tool));
  }

  @Override
  public RecordLoadable<ToolVariableRangePredicate> getLoader() {
    return LOADER;
  }
}
