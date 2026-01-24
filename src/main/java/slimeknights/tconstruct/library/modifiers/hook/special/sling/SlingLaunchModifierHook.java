package slimeknights.tconstruct.library.modifiers.hook.special.sling;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Callback run after a sling modifier slings. */
public interface SlingLaunchModifierHook {
  /**
   * Called after sling is used to allow modifiers to react to it activating.
   *
   * @param tool        Active tool
   * @param modifier    Modifier entry calling the hook
   * @param holder      Entity holding the sling
   * @param target      Entity being moved by the sling. Might be the same as {@code holder}, but not always (see bonking).
   * @param slingSource Modifier entry of the sling modifier, such as {@link slimeknights.tconstruct.tools.TinkerModifiers#flinging}
   * @param force       Final force that was applied.
   * @param multiplier  Force multiplier from other sources such as sling type and partial charge.
   * @param angle       Final angle that was applied.
   */
  void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle);

  /** Merger running all nested hooks in sequence. */
  record AllMerger(Collection<SlingLaunchModifierHook> modules) implements SlingLaunchModifierHook {
    @Override
    public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
      for (SlingLaunchModifierHook module : modules) {
        module.afterSlingLaunch(tool, modifier, holder, target, slingSource, force, multiplier, angle);
      }
    }
  }


  /* Helpers */

  /** Helper to call {@link #afterSlingLaunch(IToolStackView, ModifierEntry, LivingEntity, LivingEntity, ModifierEntry, float, float, Vec3)} on all modifiers. */
  static void afterSlingLaunch(IToolStackView tool, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.SLING_LAUNCH).afterSlingLaunch(tool, entry, holder, target, slingSource, force, multiplier, angle);
    }
  }
}
