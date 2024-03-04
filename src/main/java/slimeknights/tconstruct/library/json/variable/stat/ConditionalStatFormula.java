package slimeknights.tconstruct.library.json.variable.stat;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE;

/** Variable context for {@link ConditionalMeleeDamageModule} */
public record ConditionalStatFormula(ModifierFormula formula, List<ConditionalStatVariable> variables, String[] variableNames) implements VariableFormula<ConditionalStatVariable> {
  /** Constructor instance for reading */
  private static final BiFunction<ModifierFormula,List<ConditionalStatVariable>,ConditionalStatFormula> CONSTRUCTOR = (formula, variables) -> new ConditionalStatFormula(formula, variables, EMPTY_STRINGS);
  /** Variables for the modifier formula */
  public static final String[] VARIABLES = { "level", "value", "multiplier" };

  public ConditionalStatFormula(ModifierFormula formula, Map<String,ConditionalStatVariable> variables) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables));
  }

  @Override
  public GenericLoaderRegistry<ConditionalStatVariable> loader() {
    return ConditionalStatVariable.LOADER;
  }

  @Override
  public String[] defaultVariableNames() {
    return VARIABLES;
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, float value, float multiplier) {
    int size = variables.size();
    float[] arguments = new float[3 + size];
    arguments[LEVEL] = formula.computeLevel(tool, modifier);
    arguments[VALUE] = value;
    arguments[MULTIPLIER] = multiplier;
    for (int i = 0; i < size; i++) {
      arguments[3+i] = variables.get(i).getValue(tool, entity);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, float value, float multiplier) {
    return formula.apply(getArguments(tool, modifier, entity, value, multiplier));
  }

  /** Deserializes a conditional stat variable context from JSON */
  public static ConditionalStatFormula deserialize(JsonObject json, boolean percent) {
    return VariableFormula.deserialize(ConditionalStatVariable.LOADER, CONSTRUCTOR, json, VARIABLES, percent ? FallbackFormula.PERCENT : FallbackFormula.BOOST);
  }

  /** Reads a conditional stat variable context from the network */
  public static ConditionalStatFormula fromNetwork(FriendlyByteBuf buffer, boolean percent) {
    return VariableFormula.fromNetwork(ConditionalStatVariable.LOADER, CONSTRUCTOR, buffer, VARIABLES.length, percent ? FallbackFormula.PERCENT : FallbackFormula.BOOST);
  }
}
