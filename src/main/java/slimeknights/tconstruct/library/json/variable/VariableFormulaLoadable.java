package slimeknights.tconstruct.library.json.variable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Function3;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.math.PostFixFormula;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/** Loadable for a variable formula */
public record VariableFormulaLoadable<V extends IHaveLoader, F extends VariableFormula<V>>(
  GenericLoaderRegistry<V> variableLoader, String[] defaultNames,
  FallbackFormula boostFallback, FallbackFormula percentFallback,
  Function3<ModifierFormula,List<V>,Boolean,F> constructor
) implements RecordLoadable<F> {
  /** Loader with default fallback formulas */
  public VariableFormulaLoadable(GenericLoaderRegistry<V> variableLoader, String[] defaultNames, Function3<ModifierFormula,List<V>,Boolean,F> constructor) {
    this(variableLoader, defaultNames, FallbackFormula.BOOST, FallbackFormula.PERCENT, constructor);
  }

  /** Loader with just 1 fallback formula */
  public VariableFormulaLoadable(GenericLoaderRegistry<V> variableLoader, String[] defaultNames, FallbackFormula formula, Function3<ModifierFormula,List<V>,Boolean,F> constructor) {
    this(variableLoader, defaultNames, formula, formula, constructor);
  }

  /** Gets the fallback formula given percent */
  private FallbackFormula fallback(boolean percent) {
    return percent ? percentFallback : boostFallback;
  }

  @Override
  public F deserialize(JsonObject json, TypedMap context) {
    boolean percent = boostFallback != percentFallback && GsonHelper.getAsBoolean(json, "percent", false);
    if (json.has("variables")) {
      if (!json.has("formula")) {
        throw new JsonSyntaxException("Cannot set variables when not using formula");
      }
      ImmutableList.Builder<V> variables = ImmutableList.builder();
      int index = defaultNames.length;
      JsonObject variableObj = GsonHelper.getAsJsonObject(json, "variables");
      String[] newNames = Arrays.copyOf(defaultNames, index + variableObj.size());
      for (Entry<String,JsonElement> entry : variableObj.entrySet()) {
        String key = entry.getKey();
        if (LogicHelper.isInList(defaultNames, key)) {
          throw new JsonSyntaxException("Variable " + key + " is already defined for this module");
        }
        newNames[index] = key;
        variables.add(variableLoader.convert(entry.getValue(), key, context));
        index++;
      }
      // we only store the variable names in the VariableFormula during datagen, any other time its an empty list as we don't need it at runtime
      // we do need them to parse the post fix formula though
      return constructor.apply(PostFixFormula.deserialize(json, newNames), variables.build(), percent);
    }
    // no variables? use the standard loading logic
    return constructor.apply(ModifierFormula.deserialize(json, defaultNames, fallback(percent)), List.of(), percent);
  }

  @Override
  public void serialize(F object, JsonObject json) {
    if (boostFallback != percentFallback) {
      json.addProperty("percent", object.percent());
    }
    List<V> variables = object.variables();
    if (!variables.isEmpty()) {
      JsonObject variablesObject = new JsonObject();
      GenericLoaderRegistry<V> loader = variableLoader();
      String[] variableNames = object.variableNames();
      for (int i = 0; i < variableNames.length; i++) {
        variablesObject.add(variableNames[i], loader.serialize(variables.get(i)));
      }
      json.add("variables", variablesObject);
    }
    object.formula().serialize(json, defaultNames);
  }

  @Override
  public F decode(FriendlyByteBuf buffer, TypedMap context) {
    boolean percent = boostFallback != percentFallback && buffer.readBoolean();
    ImmutableList.Builder<V> builder = ImmutableList.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      builder.add(variableLoader.decode(buffer, context));
    }
    List<V> variables = builder.build();
    return constructor.apply(ModifierFormula.fromNetwork(buffer, defaultNames.length + variables.size(), fallback(percent)), variables, percent);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, F object) throws EncoderException {
    if (boostFallback != percentFallback) {
      buffer.writeBoolean(object.percent());
    }
    List<V> variables = object.variables();
    buffer.writeVarInt(variables.size());
    for (V variable : variables) {
      variableLoader.encode(buffer, variable);
    }
    object.formula().toNetwork(buffer);
  }
}
