package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.floats.FloatStack;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.StackOperation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.BASE_VALUE;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE;

/** Shared logic for various variable formulas */
public interface VariableFormula<T extends IHaveLoader> {
  /** used client side to satisfy the variable name parameter */
  String[] EMPTY_STRINGS = new String[0];

  /** Gets the formula instance */
  ModifierFormula formula();
  /** Gets the list of variables in this context */
  List<T> variables();
  /** Gets the names of variables in this context */
  String[] variableNames();
  /** If true, this formula behaves as a multiplier, if false it behaves as a boost. Used for display mainly */
  boolean percent();


  /** Adds the level, base value, value, and multiplier into the array at their common locations */
  static float[] statModuleArguments(int variables, float level, float baseValue, float value, float multiplier) {
    float[] arguments = new float[4 + variables];
    arguments[LEVEL] = level;
    arguments[VALUE] = value;
    arguments[MULTIPLIER] = multiplier;
    arguments[BASE_VALUE] = baseValue;
    return arguments;
  }


  /* JSON */


  /** Gets the variable names from the given map */
  static String[] getNames(Map<String,?> variables) {
    return variables.keySet().toArray(new String[0]);
  }

  /** Shared builder logic for modifiers using custom variables and formulas */
  abstract class Builder<T extends Builder<T,M,V>,M,V> extends ModifierFormula.Builder<T,M> {
    /** If true, using a percent formula. If false, using a boost formula */
    protected boolean percent = false;
    /** Map of variable names to variable objects */
    protected final Map<String,V> variables = new LinkedHashMap<>();
    public Builder(String[] variables) {
      super(variables);
    }

    /** Sets this to a percent boost formula */
    @SuppressWarnings("unchecked")
    public T percent() {
      this.percent = true;
      return (T) this;
    }

    /** Adds a variable to the builder */
    @SuppressWarnings("unchecked")
    public T customVariable(String key, V variable) {
      // disallow a variable that is already there
      if (LogicHelper.isInList(variableNames, key)) {
        throw new IllegalArgumentException("Variable " + key + " already exists in the module's variables");
      }
      // disallow adding the same name multiple times
      V original = this.variables.put(key, variable);
      if (original != null) {
        throw new IllegalStateException("Duplicate variable name " + key + ", previous entry " + original);
      }
      return (T) this;
    }

    @Override
    public FormulaVariableBuilder formula() {
      return new FormulaVariableBuilder();
    }

    /** Extended to add custom variable */
    public class FormulaVariableBuilder extends FormulaBuilder<FormulaVariableBuilder,M> {
      protected FormulaVariableBuilder() {
        super(Builder.this);
      }

      /** Pushes a custom variable onto the stack, from variables added via {@link #customVariable(String, Object)} */
      public FormulaVariableBuilder customVariable(String name) {
        if (!variables.containsKey(name)) {
          throw new IllegalArgumentException("Unknown custom variable " + name);
        }
        return operation(new SerializeVariableName(name));
      }

      /** Pushes a custom variable onto the stack while also defining it */
      public FormulaVariableBuilder customVariable(String name, V variable) {
        Builder.this.customVariable(name, variable);
        return operation(new SerializeVariableName(name));
      }

      /** Small hack, this is a stack operation that only exists in the serializer as it's easier than trying to track indices */
      private record SerializeVariableName(String name) implements StackOperation {
        @Override
        public void perform(FloatStack stack, float[] variables) {
          // we need to test the formula after building it, but are just going to test it with 0s
          // does not really matter if its the zero they gave us or one we made up :)
          stack.push(0);
        }

        @Override
        public JsonPrimitive serialize(String[] variableNames) {
          return new JsonPrimitive('$' + name);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer) {
          throw new UnsupportedOperationException();
        }
      }
    }
  }
}
