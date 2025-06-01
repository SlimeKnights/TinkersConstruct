package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Called when the player is using a tool. Unlike {@link GeneralInteractionModifierHook}, these hooks do not require the modifier to be the active modifier.
 */
public interface UsingToolModifierHook {
  /**
   * Called every tick on all modifiers when the player is using any modifier.
   * @param tool            Tool performing interaction
   * @param modifier        Modifier instance
   * @param entity          Interacting entity
   * @param timeLeft        How many ticks of use duration was left
   * @param activeModifier  Currently active modifier.
   */
  default void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {}

  /**
   * Called when the player releases right click or finishes using the tool, but before any finish using logic runs.
   * @param tool            Tool performing interaction
   * @param modifier        Modifier instance
   * @param entity          Interacting entity
   * @param useDuration     Use duration for this tool.
   * @param timeLeft        How many ticks of use duration was left. Will be non-positive if finished being used.
   * @param activeModifier  Modifier that is currently active. Will be {@link ModifierEntry#EMPTY} for bows and other non-modifier usage.
   */
  default void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {}

  /**
   * Called when the player stops using the tool for any reason. Called after all other using hooks.
   * @param tool         Tool performing interaction
   * @param modifier     Modifier instance
   * @param entity       Interacting entity
   * @param useDuration  Use duration for this tool.
   * @param timeLeft     How many ticks of use duration was left. Will be non-positive if finished being used.
   * @param activeModifier  Modifier that is currently active. Will be {@link ModifierEntry#EMPTY} for bows and other non-modifier usage.
   */
  default void afterStopUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {}


  /** Helper to call when the tool is done being used */
  static void afterStopUsing(IToolStackView tool, LivingEntity living, int timeLeft) {
    ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
    int duration = activeModifier.getHook(ModifierHooks.GENERAL_INTERACT).getUseDuration(tool, activeModifier);
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.TOOL_USING).afterStopUsing(tool, entry, living, duration, timeLeft, activeModifier);
    }
  }

  /** Logic to run all nested hooks */
  record AllMerger(Collection<UsingToolModifierHook> modules) implements UsingToolModifierHook {
    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
      for (UsingToolModifierHook module : modules) {
        module.onUsingTick(tool, modifier, entity, useDuration, timeLeft, activeModifier);
      }
    }

    @Override
    public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
      for (UsingToolModifierHook module : modules) {
        module.beforeReleaseUsing(tool, modifier, entity, useDuration, timeLeft, activeModifier);
      }
    }

    @Override
    public void afterStopUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
      for (UsingToolModifierHook module : modules) {
        module.afterStopUsing(tool, modifier, entity, useDuration, timeLeft, activeModifier);
      }
    }
  }
}
