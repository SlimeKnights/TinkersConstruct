package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Represents a modifier formula that may be either simple or complex.
 */
public sealed interface ModifierFormula permits PostFixFormula, SimpleLevelingFormula {
  /** Variable index for the modifier level for the sake of the builder */
  int LEVEL = 0;

  /** Computes the level value for this formula, allows some optimizations to not compute level when not needed */
  float computeLevel(IToolContext tool, ModifierEntry modifier);

  /** Applies this formula to the given arguments */
  float apply(float... arguments);

  /** Serializes this object to JSON */
  JsonObject serialize(JsonObject json);

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
      // TODO: string formulas using Shunting yard algorithm
      return PostFixFormula.deserialize(json, variableNames);
    }
    LevelingValue leveling = LevelingValue.deserialize(json);
    return new SimpleLevelingFormula(leveling, fallback);
  }

  /**
   * Reads a formula from the network
   * @param buffer         Buffer instance
   * @param variableNames  Variable names for when post fix is used
   * @param fallback       Fallback for when not using post fix
   * @return  Formula object
   */
  static ModifierFormula fromNetwork(FriendlyByteBuf buffer, String[] variableNames, FallbackFormula fallback) {
    short size = buffer.readShort();
    if (size == -1) {
      LevelingValue leveling = LevelingValue.fromNetwork(buffer);
      return new SimpleLevelingFormula(leveling, fallback);
    }
    return PostFixFormula.fromNetwork(buffer, size, variableNames);
  }

  /** Formula to use when not using the post fix formula */
  @FunctionalInterface
  interface FallbackFormula {
    /** Formula that just returns the leveling value directly */
    FallbackFormula IDENTITY = arguments -> arguments[LEVEL];
    /** Formula adding the leveling value to the second argument, requires 1 additional argument */
    FallbackFormula ADD = arguments -> arguments[LEVEL] + arguments[1];
    /** Formula for standard percent boosts, requires 1 additional argument */
    FallbackFormula PERCENT = arguments -> arguments[1] * (1 + arguments[LEVEL]);

    /**
     * Runs this formula
     * @param arguments   Additional arguments passed into the module, the result of {@link LevelingValue} is placed at index 0
     * @return  Value after applying the formula
     */
    float apply(float[] arguments);
  }


  /** Builder for a module containing a modifier formula */
  @RequiredArgsConstructor
  abstract class Builder<T extends Builder<T>> extends ModifierModuleCondition.Builder<T> implements LevelingValue.Builder {
    /** Variables to use for post fix formulas */
    private final String[] variables;
    /** Fallback formula for simple leveling */
    private final FallbackFormula formula;

    /** Builds the module given the formula */
    protected abstract ModifierModule build(ModifierFormula formula);

    @Override
    public ModifierModule amount(float flat, float leveling) {
      return build(new SimpleLevelingFormula(new LevelingValue(flat, leveling), formula));
    }

    /** Switches this builder into formula building mode */
    public FormulaBuilder formula() {
      return new FormulaBuilder();
    }

    /** Builder for the formula segment of this module */
    public class FormulaBuilder extends PostFixFormula.Builder<FormulaBuilder> {
      protected FormulaBuilder() {
        super(variables);
      }

      /** Builds the module given the formula */
      public ModifierModule build() {
        return Builder.this.build(buildFormula());
      }
    }
  }
}
