package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import java.util.Collection;
import java.util.function.Function;

/**
 * Hook for modifying a stat conditioned on the holder.
 * TODO 1.19: move to {@link slimeknights.tconstruct.library.modifiers.hook.build}
 */
public interface ConditionalStatModifierHook {
  /** Default behavior */
  ConditionalStatModifierHook EMPTY = (tool, modifier, living, stat, baseValue, multiplier) -> baseValue;

  /** Constructor for a merger that runs all hooks from the children */
  Function<Collection<ConditionalStatModifierHook>, ConditionalStatModifierHook> ALL_MERGER = AllMerger::new;

  /**
   * Method to modify a stat as the tool is being used
   * @param tool         Tool instance
   * @param modifier     Modifier instance
   * @param living       Entity holding the tool
   * @param stat         Stat to be modified, safe to do instance equality
   * @param baseValue    Value before this hook modified the stat
   * @param multiplier   Global multiplier, same value contained in the tool, but fetched for convenience as it's commonly needed for stat bonuses
   * @return  New value of the stat, or baseValue if you choose not to modify this stat
   */
  float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier);

  /** Gets the given stat from the tool, as modified by this hook */
  static float getModifiedStat(IToolStackView tool, LivingEntity living, FloatToolStat stat, float value) {
    float multiplier = tool.getMultiplier(stat);
    for (ModifierEntry entry : tool.getModifierList()) {
      value = entry.getHook(TinkerHooks.CONDITIONAL_STAT).modifyStat(tool, entry, living, stat, value, multiplier);
    }
    return stat.clamp(value);
  }

  /** Gets the given stat from the tool, as modified by this hook */
  static float getModifiedStat(IToolStackView tool, LivingEntity living, FloatToolStat stat) {
    return getModifiedStat(tool, living, stat, tool.getStats().get(stat));
  }

  /** All hook merger: runs hooks of all children */
  record AllMerger(Collection<ConditionalStatModifierHook> modules) implements ConditionalStatModifierHook {
    @Override
    public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
      for (ConditionalStatModifierHook hook : modules) {
        baseValue = hook.modifyStat(tool, modifier, living, stat, baseValue, multiplier);
      }
      return baseValue;
    }
  }
}
