package slimeknights.tconstruct.library.json.variable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.floats.FloatStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.math.PostFixFormula;
import slimeknights.tconstruct.library.json.math.StackOperation;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.BASE_VALUE;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE;

/** Shared logic for various variable formulas */
public interface VariableFormula<T extends IHaveLoader<T>> {
  /** used client side to satisfy the variable name parameter */
  String[] EMPTY_STRINGS = new String[0];

  /** Gets the loader registry for this context */
  GenericLoaderRegistry<T> loader();
  /** Gets the formula instance */
  ModifierFormula formula();
  /** Gets the list of variables in this context */
  List<T> variables();
  /** Gets the names of variables in this context */
  String[] variableNames();
  /** Gets the names of default variables */
  String[] defaultVariableNames();


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

  /** Serializes this object to JSON */
  default JsonObject serialize(JsonObject json) {
    List<T> variables = variables();
    if (!variables.isEmpty()) {
      JsonObject variablesObject = new JsonObject();
      GenericLoaderRegistry<T> loader = loader();
      String[] variableNames = variableNames();
      for (int i = 0; i < variableNames.length; i++) {
        variablesObject.add(variableNames[i], loader.serialize(variables.get(i)));
      }
      json.add("variables", variablesObject);
    }
    formula().serialize(json, defaultVariableNames());
    return json;
  }

  /** Deserializes a variable context from JSON */
  static <C extends VariableFormula<T>, T extends IHaveLoader<T>> C deserialize(GenericLoaderRegistry<T> loader, BiFunction<ModifierFormula,List<T>,C> constructor, JsonObject json, String[] defaultNames, FallbackFormula fallback) {
    if (json.has("variables")) {
      if (!json.has("formula")) {
        throw new JsonSyntaxException("Cannot set variables when not using formula");
      }
      ImmutableList.Builder<T> variables = ImmutableList.builder();
      int index = defaultNames.length;
      JsonObject variableObj = GsonHelper.getAsJsonObject(json, "variables");
      String[] newNames = Arrays.copyOf(defaultNames, index + variableObj.size());
      for (Entry<String,JsonElement> entry : variableObj.entrySet()) {
        String key = entry.getKey();
        if (LogicHelper.isInList(defaultNames, key)) {
          throw new JsonSyntaxException("Variable " + key + " is already defined for this module");
        }
        newNames[index] = key;
        variables.add(loader.deserialize(entry.getValue()));
        index++;
      }
      // we only store the variable names in the VariableFormula during datagen, any other time its an empty list as we don't need it at runtime
      // we do need them to parse the post fix formula though
      return constructor.apply(PostFixFormula.deserialize(json, newNames), variables.build());
    }
    // no variables? use the standard loading logic
    return constructor.apply(ModifierFormula.deserialize(json, defaultNames, fallback), List.of());
  }


  /* Network */

  /** Writes this context to the network */
  default void toNetwork(FriendlyByteBuf buffer) {
    List<T> variables = variables();
    GenericLoaderRegistry<T> loader = loader();
    buffer.writeVarInt(variables.size());
    for (T variable : variables) {
      loader.toNetwork(variable, buffer);
    }
    formula().toNetwork(buffer);
  }

  /** Reads a variable context from the network */
  static <C extends VariableFormula<T>, T extends IHaveLoader<T>> C fromNetwork(GenericLoaderRegistry<T> loader, BiFunction<ModifierFormula, List<T>, C> constructor, FriendlyByteBuf buffer, int defaultVariables, FallbackFormula fallback) {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      builder.add(loader.fromNetwork(buffer));
    }
    List<T> variables = builder.build();
    return constructor.apply(ModifierFormula.fromNetwork(buffer, defaultVariables + variables.size(), fallback), variables);
  }

  /** Gets the variable names from the given map */
  static String[] getNames(Map<String,?> variables) {
    return variables.keySet().toArray(new String[0]);
  }

  /** Shared builder logic for modifiers using custom variables and formulas */
  abstract class Builder<T extends Builder<T,M,V>,M,V> extends ModifierFormula.Builder<T,M> {
    /** Map of variable names to variable objects */
    protected final Map<String,V> variables = new LinkedHashMap<>();
    public Builder(String[] variables) {
      super(variables);
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
