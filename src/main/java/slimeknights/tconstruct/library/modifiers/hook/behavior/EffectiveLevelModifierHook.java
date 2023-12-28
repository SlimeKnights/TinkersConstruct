package slimeknights.tconstruct.library.modifiers.hook.behavior;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import java.util.Collection;

/**
 * Gets the effective level of a given modifier. Used for incremental modifiers to reduce the effect power when at partial levels.
 * Can also be used to cap levels to a certain amount.
 * Unlikely other modifier hooks, this is not supported by every modifier, mainly composable supports it. See {@link Modifier#getEffectiveLevel(IToolContext, int)} for ordinary modifiers.
 */
public interface EffectiveLevelModifierHook {
  /**
   * Gets the level scaled based on attributes of modifier data. Used mainly for incremental modifiers.
   * @param tool       Tool context
   * @param modifier   Modifier checking effective level
   * @param level      Modifier level
   * @return  Modifier level, possibly adjusted by tool properties
   */
  float getEffectiveLevel(IToolContext tool, Modifier modifier, float level);

  /** Merger that applies submodules one after another, order matters here */
  record ComposeMerger(Collection<EffectiveLevelModifierHook> modules) implements EffectiveLevelModifierHook {
    @Override
    public float getEffectiveLevel(IToolContext tool, Modifier modifier, float level) {
      for (EffectiveLevelModifierHook module : modules) {
        level = module.getEffectiveLevel(tool, modifier, level);
      }
      return level;
    }
  }
}
