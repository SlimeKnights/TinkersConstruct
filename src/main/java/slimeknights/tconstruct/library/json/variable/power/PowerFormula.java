package slimeknights.tconstruct.library.json.variable.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormulaLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/** Variable context for {@link slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule} */
public record PowerFormula(ModifierFormula formula, List<PowerVariable> variables, String[] variableNames, boolean percent) implements VariableFormula<PowerVariable> {
  /** Variables for the modifier formula */
  public static final String[] VARIABLES = { "level", "damage", "multiplier" };
  /** Loader instance */
  public static final RecordLoadable<PowerFormula> LOADER = new VariableFormulaLoadable<>(PowerVariable.LOADER, VARIABLES, (formula, variables, percent) -> new PowerFormula(formula, variables, EMPTY_STRINGS, percent));

  public PowerFormula(ModifierFormula formula, Map<String,PowerVariable> variables, boolean percent) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables), percent);
  }

  /** Builds the arguments from the context */
  private float[] getArguments(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, double damage, float multiplier) {
    int size = variables.size();
    float[] arguments = new float[3 + size];
    arguments[ModifierFormula.LEVEL] = formula.processLevel(modifier);
    arguments[ModifierFormula.VALUE] = (float) damage;
    arguments[ModifierFormula.MULTIPLIER] = multiplier;
    for (int i = 0; i < size; i++) {
      arguments[3+i] = variables.get(i).getValue(modifiers, persistentData, modifier, projectile, hit, attacker, target);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, double damage, float multiplier) {
    return formula.apply(getArguments(modifiers, persistentData, modifier, projectile, hit, attacker, target, damage, multiplier));
  }
}
