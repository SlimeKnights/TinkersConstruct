package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.math.FormulaLoadable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.TConstruct.RANDOM;

/**
 * Module which reduces damage on a tool by a given percentage
 * @param formula    Formula to use
 * @param condition  Condition for this module to run
 */
public record ReduceToolDamageModule(ModifierFormula formula, ModifierModuleCondition condition) implements ModifierModule, ToolDamageModifierHook, TooltipModifierHook, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_DAMAGE, TinkerHooks.TOOLTIP);
  /** Formula instance for the loader */
  private static final FormulaLoadable FORMULA = new FormulaLoadable(FallbackFormula.IDENTITY, "level");
  /** Loader instance */
  public static final RecordLoadable<ReduceToolDamageModule> LOADER = RecordLoadable.create(FORMULA.directField(ReduceToolDamageModule::formula), ModifierModuleCondition.FIELD, ReduceToolDamageModule::new);

  /** Creates a builder instance */
  public static FormulaLoadable.Builder<ReduceToolDamageModule> builder() {
    return FORMULA.builder(ReduceToolDamageModule::new);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the percentage to reduce tool damage */
  private float getPercent(IToolContext tool, ModifierEntry modifier) {
    return formula.apply(formula.computeLevel(tool, modifier));
  }

  /**
   * Damages the given amount with the reinforced percentage
   * @param amount      Amount to damage
   * @param percentage  Percentage of damage to cancel, runs probabilistically
   * @return  Amount after reinforced
   */
  public static int reduceDamage(int amount, float percentage) {
    // 100% protection? all damage blocked
    if (percentage >= 1) {
      return 0;
    }
    // 0% protection? nothing blocked
    if (percentage <= 0) {
      return amount;
    }
    // no easy closed form formula for this that I know of, and damage amount tends to be small, so take a chance for each durability
    int dealt = 0;
    for (int i = 0; i < amount; i++) {
      if (RANDOM.nextFloat() >= percentage) {
        dealt++;
      }
    }
    return dealt;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    if (this.condition.matches(tool, modifier)) {
      return reduceDamage(amount, getPercent(tool, modifier));
    }
    return amount;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (this.condition.matches(tool, modifier)) {
      tooltip.add(modifier.getModifier().applyStyle(Component.literal(Util.PERCENT_FORMAT.format(getPercent(tool, modifier)) + " ").append(modifier.getModifier().getDisplayName())));
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
