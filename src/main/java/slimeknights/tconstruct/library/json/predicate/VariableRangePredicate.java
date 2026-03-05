package slimeknights.tconstruct.library.json.predicate;

import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;

/** Common logic for variable range predicates. */
public interface VariableRangePredicate {
  LoadableField<Float,VariableRangePredicate> MIN_FIELD = FloatLoadable.ANY.defaultField("min", Float.NEGATIVE_INFINITY, VariableRangePredicate::min);
  LoadableField<Float,VariableRangePredicate> MAX_FIELD = FloatLoadable.ANY.defaultField("max", Float.POSITIVE_INFINITY, VariableRangePredicate::max);
  LoadableField<IntervalType,VariableRangePredicate> INTERVAL_FIELD = IntervalType.LOADABLE.defaultField("interval", IntervalType.CLOSED, true, VariableRangePredicate::interval);

  /** Min value for this range */
  float min();

  /** Max value for this range */
  float max();

  /** Type of interval to match */
  IntervalType interval();

  /** Checks if this predicate matches the given value */
  default boolean matches(float value) {
    return interval().test(value, min(), max());
  }

  /** Represents different types of intervals. */
  enum IntervalType {
    OPEN {
      @Override
      public boolean test(float value, float min, float max) {
        return min < value && value < max;
      }
    },
    CLOSED {
      @Override
      public boolean test(float value, float min, float max) {
        return min <= value && value <= max;
      }
    },
    LEFT_OPEN {
      @Override
      public boolean test(float value, float min, float max) {
        return min < value && value <= max;
      }
    },
    RIGHT_OPEN {
      @Override
      public boolean test(float value, float min, float max) {
        return min <= value && value < max;
      }
    };

    /** Loadable instance for fields */
    public static final EnumLoadable<IntervalType> LOADABLE = new EnumLoadable<>(IntervalType.class);

    /** Tests if the value is within the range */
    public abstract boolean test(float value, float min, float max);
  }
}
