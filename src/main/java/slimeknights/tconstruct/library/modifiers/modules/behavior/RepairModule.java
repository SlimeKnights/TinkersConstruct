package slimeknights.tconstruct.library.modifiers.modules.behavior;

import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.FormulaModuleLoader;
import slimeknights.tconstruct.library.modifiers.modules.FormulaModuleLoader.FormulaModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for multiplying tool repair */
public record RepairModule(ModifierFormula formula, ModifierModuleCondition condition) implements RepairFactorModifierHook, ModifierModule, FormulaModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.REPAIR_FACTOR);
  public static final int FACTOR = 1;
  /** Loader instance and builder creator */
  public static final FormulaModuleLoader<RepairModule> LOADER = new FormulaModuleLoader<>(RepairModule::new, FallbackFormula.PERCENT, "level", "factor");

  /** Creates a builder instance */
  public static FormulaModuleLoader<RepairModule>.Builder builder() {
    return LOADER.builder();
  }

  @Override
  public float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor) {
    return formula.apply(formula.computeLevel(tool, entry), factor);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
