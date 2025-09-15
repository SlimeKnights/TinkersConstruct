package slimeknights.tconstruct.library.modifiers.hook.build;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import javax.annotation.Nullable;
import java.util.Collection;

/** Hook for modifying a stat conditioned on the holder. */
public interface ConditionalStatModifierHook {
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
  static float getModifiedStat(IToolStackView tool, @Nullable LivingEntity living, FloatToolStat stat, float value) {
    if (living != null) {
      float multiplier = tool.getMultiplier(stat);
      for (ModifierEntry entry : tool.getModifierList()) {
        value = entry.getHook(ModifierHooks.CONDITIONAL_STAT).modifyStat(tool, entry, living, stat, value, multiplier);
      }
    }
    return stat.clamp(value);
  }

  /** Gets the given stat from the tool, as modified by this hook */
  static float getModifiedStat(IToolStackView tool, @Nullable LivingEntity living, FloatToolStat stat) {
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
