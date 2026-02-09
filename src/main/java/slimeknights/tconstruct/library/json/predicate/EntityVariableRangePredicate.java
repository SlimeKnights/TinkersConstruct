package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;

/**
 * Predicate that checks the value of a {@link BlockVariable} falls in the given range.
 * @param variable  Variable to resolve from the block state.
 * @param min       Minimum allowed value.
 * @param max       Maximum allowed value.
 */
public record EntityVariableRangePredicate(EntityVariable variable, float min, float max, IntervalType interval) implements LivingEntityPredicate, VariableRangePredicate {
  public static final RecordLoadable<EntityVariableRangePredicate> LOADER = RecordLoadable.create(EntityVariable.LOADER.requiredField("variable", EntityVariableRangePredicate::variable), MIN_FIELD, MAX_FIELD, INTERVAL_FIELD, EntityVariableRangePredicate::new);

  /** Creates a predicate with the given min value */
  public static EntityVariableRangePredicate min(EntityVariable variable, float min, boolean open) {
    return new EntityVariableRangePredicate(variable, min, Float.POSITIVE_INFINITY, open ? IntervalType.OPEN : IntervalType.RIGHT_OPEN);
  }

  /** Creates a predicate with the given min max */
  public static EntityVariableRangePredicate max(EntityVariable variable, float max, boolean open) {
    return new EntityVariableRangePredicate(variable, Float.NEGATIVE_INFINITY, max, open ? IntervalType.OPEN : IntervalType.LEFT_OPEN);
  }

  @Override
  public boolean matches(LivingEntity entity) {
    return matches(variable.getValue(entity));
  }

  @Override
  public RecordLoadable<EntityVariableRangePredicate> getLoader() {
    return LOADER;
  }
}
