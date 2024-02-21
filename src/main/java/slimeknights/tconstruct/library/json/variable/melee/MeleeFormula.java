package slimeknights.tconstruct.library.json.variable.melee;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.BOOST;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.PERCENT;

/** Variable context for {@link ConditionalMeleeDamageModule} */
public record MeleeFormula(ModifierFormula formula, List<MeleeVariable> variables, String[] variableNames) implements VariableFormula<MeleeVariable> {
  /** Constructor instance for reading */
  private static final BiFunction<ModifierFormula,List<MeleeVariable>,MeleeFormula> CONSTRUCTOR = (formula, variables) -> new MeleeFormula(formula, variables, EMPTY_STRINGS);
  /** Variables for the modifier formula */
  public static final String[] VARIABLES = { "level", "damage", "multiplier", "base_damage" };

  public MeleeFormula(ModifierFormula formula, Map<String,MeleeVariable> variables) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables));
  }

  @Override
  public GenericLoaderRegistry<MeleeVariable> loader() {
    return MeleeVariable.LOADER;
  }

  @Override
  public String[] defaultVariableNames() {
    return VARIABLES;
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker, float baseDamage, float damage) {
    int size = variables.size();
    float[] arguments = VariableFormula.statModuleArguments(size, formula.computeLevel(tool, modifier), baseDamage, damage, tool.getMultiplier(ToolStats.ATTACK_DAMAGE));
    for (int i = 0; i < size; i++) {
      arguments[4+i] = variables.get(i).getValue(tool, context, attacker);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker, float baseDamage, float damage) {
    return formula.apply(getArguments(tool, modifier, context, attacker, baseDamage, damage));
  }

  /** Deserializes a melee variable context from JSON */
  public static MeleeFormula deserialize(JsonObject json, boolean percent) {
    return VariableFormula.deserialize(MeleeVariable.LOADER, CONSTRUCTOR, json, VARIABLES, percent ? PERCENT : BOOST);
  }

  /** Reads a melee variable context from the network */
  public static MeleeFormula fromNetwork(FriendlyByteBuf buffer, boolean percent) {
    return VariableFormula.fromNetwork(MeleeVariable.LOADER, CONSTRUCTOR, buffer, VARIABLES.length, percent ? PERCENT : BOOST);
  }
}
