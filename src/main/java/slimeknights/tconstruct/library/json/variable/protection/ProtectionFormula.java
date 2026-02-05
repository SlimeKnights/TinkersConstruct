package slimeknights.tconstruct.library.json.variable.protection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormulaLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/** Variable context for {@link ConditionalMeleeDamageModule} */
public record ProtectionFormula(ModifierFormula formula, List<ProtectionVariable> variables, String[] variableNames, boolean percent) implements VariableFormula<ProtectionVariable> {
  /** Variables for the modifier formula. TODO 1.21: replace "protection" with "value". */
  public static final String[] VARIABLES = { "level", "protection" };
  /** Loader instance for protection */
  public static final RecordLoadable<ProtectionFormula> LOADER = new VariableFormulaLoadable<>(ProtectionVariable.LOADER, VARIABLES, FallbackFormula.ADD, (formula, variables, percent) -> new ProtectionFormula(formula, variables, EMPTY_STRINGS));
  /** Loader instance for damage adjustment */
  public static final RecordLoadable<ProtectionFormula> DAMAGE_LOADER = new VariableFormulaLoadable<>(ProtectionVariable.LOADER, VARIABLES, FallbackFormula.ADD, FallbackFormula.PERCENT, (formula, variables, percent) -> new ProtectionFormula(formula, variables, EMPTY_STRINGS, percent));

  public ProtectionFormula(ModifierFormula formula, List<ProtectionVariable> variables, String[] variableNames) {
    this(formula, variables, variableNames, true);
  }

  public ProtectionFormula(ModifierFormula formula, Map<String,ProtectionVariable> variables, boolean percent) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables), percent);
  }

  public ProtectionFormula(ModifierFormula formula, Map<String,ProtectionVariable> variables) {
    this(formula, variables, true);
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier, @Nullable EquipmentContext context, @Nullable LivingEntity target, @Nullable DamageSource source, @Nullable EquipmentSlot slotType, float protection) {
    int size = variables.size();
    float[] arguments = new float[VARIABLES.length + size];
    arguments[ModifierFormula.LEVEL] = formula.processLevel(modifier);
    arguments[ModifierFormula.VALUE] = protection;
    for (int i = 0; i < size; i++) {
      arguments[VARIABLES.length+i] = variables.get(i).getValue(tool, context, target, source, slotType);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier, @Nullable EquipmentContext context, @Nullable LivingEntity target, @Nullable EquipmentSlot slotType, @Nullable DamageSource source, float protection) {
    return formula.apply(getArguments(tool, modifier, context, target, source, slotType, protection));
  }
}
