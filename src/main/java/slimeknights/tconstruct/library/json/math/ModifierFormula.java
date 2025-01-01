package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;

/**
 * Represents a modifier formula that may be either simple or complex.
 */
public sealed interface ModifierFormula permits PostFixFormula, SimpleLevelingFormula {
  // common variable indexes, not required to use but make things easier
  /** Variable index for the modifier level for the sake of the builder */
  int LEVEL = 0;
  /** Variable index for the original value the formula is computing */
  int VALUE = 1;
  /** Variable index for the common multiplier for this stat */
  int MULTIPLIER = 2;
  /** Variable index for the base value before modifiers changed anything */
  int BASE_VALUE = 3;

  /** Computes the scaled level for this formula, allows deciding which index gets the simple formula's scale. For post fix this is equivelent to just calling {@link ModifierEntry#getEffectiveLevel()} */
  float processLevel(ModifierEntry modifier);

  /** Applies this formula to the given arguments */
  float apply(float... arguments);

  /** Serializes this object to JSON */
  JsonObject serialize(JsonObject json, String[] variableNames);

  /** Writes this object to the network */
  void toNetwork(FriendlyByteBuf buffer);


  /* Constructors */

  /**
   * Deserializes a formula from JSON
   * @param json           JSON object
   * @param variableNames  Variable names for when post fix is used
   * @param fallback       Fallback for when not using post fix
   * @return  Formula object
   */
  static ModifierFormula deserialize(JsonObject json, String[] variableNames, FallbackFormula fallback) {
    if (json.has("formula")) {
      return PostFixFormula.deserialize(json, variableNames);
    }
    LevelingValue leveling = LevelingValue.LOADABLE.deserialize(json);
    return new SimpleLevelingFormula(leveling, fallback);
  }

  /**
   * Reads a formula from the network
   * @param buffer         Buffer instance
   * @param numArguments   Number of arguments for the formula for validation
   * @param fallback       Fallback for when not using post fix
   * @return  Formula object
   */
  static ModifierFormula fromNetwork(FriendlyByteBuf buffer, int numArguments, FallbackFormula fallback) {
    short size = buffer.readShort();
    if (size == -1) {
      LevelingValue leveling = LevelingValue.LOADABLE.decode(buffer);
      return new SimpleLevelingFormula(leveling, fallback);
    }
    return PostFixFormula.fromNetwork(buffer, size, numArguments);
  }

  /** Formula to use when not using the post fix formula */
  @FunctionalInterface
  interface FallbackFormula {
    /** Formula that just returns the leveling value directly */
    FallbackFormula IDENTITY = arguments -> arguments[LEVEL];
    /** Formula adding the leveling value to the second argument, requires 1 additional argument */
    FallbackFormula ADD = arguments -> arguments[LEVEL] + arguments[VALUE];
    /** Formula for standard percent boosts, requires 1 additional argument */
    FallbackFormula PERCENT = arguments -> arguments[VALUE] * (1 + arguments[LEVEL]);
    /** Formula for standard boosts, requires argument 1 to be the base value and argument 2 to be the multiplier */
    FallbackFormula BOOST = arguments -> arguments[VALUE] + arguments[LEVEL] * arguments[MULTIPLIER];

    /**
     * Runs this formula
     * @param arguments   Additional arguments passed into the module, the result of {@link LevelingValue} is placed at index 0
     * @return  Value after applying the formula
     */
    float apply(float[] arguments);
  }


  /** Builder for a module containing a modifier formula */
  @RequiredArgsConstructor
  abstract class Builder<T extends Builder<T,M>,M> extends ModuleBuilder.Stack<T> implements LevelingValue.Builder<M> {
    /** Variables to use for post fix formulas */
    protected final String[] variableNames;

    /** Builds the module given the formula */
    protected abstract M build(ModifierFormula formula);

    @Override
    public M amount(float flat, float leveling) {
      // formula does not actually matter during datagen, so just pass in a dummy formula to save constructor arguments
      return build(new SimpleLevelingFormula(new LevelingValue(flat, leveling), FallbackFormula.IDENTITY));
    }

    /** Switches this builder into formula building mode */
    public FormulaBuilder<?,M> formula() {
      return new FormulaBuilder<>(this);
    }

    /** Builder for the formula segment of this module */
    public static class FormulaBuilder<P extends FormulaBuilder<P,M>,M> extends PostFixFormula.Builder<P> {
      private final Builder<?,M> parent;
      // for some reason having this as a non-static class breaks the M generic, a static class fixes the issue even if its less "pretty"
      protected FormulaBuilder(Builder<?,M> parent) {
        super(parent.variableNames);
        this.parent = parent;
      }

      /** Builds the module given the formula */
      public M build() {
        return parent.build(buildFormula());
      }
    }
  }
}
