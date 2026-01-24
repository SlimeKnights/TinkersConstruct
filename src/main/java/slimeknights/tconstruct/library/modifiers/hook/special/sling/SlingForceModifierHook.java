package slimeknights.tconstruct.library.modifiers.hook.special.sling;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Modifier hook to change the force for {@link slimeknights.tconstruct.tools.modifiers.ability.sling.SlingModifier}. */
public interface SlingForceModifierHook {
  /**
   * Called when a sling is used to allow modifiers to change the force of the effect.
   *
   * @param tool        Active tool
   * @param modifier    Modifier entry calling the hook
   * @param holder      Entity holding the sling
   * @param target      Entity being moved by the sling. Might be the same as {@code holder}, but not always (see bonking).
   * @param slingSource Modifier entry of the sling modifier, such as {@link slimeknights.tconstruct.tools.TinkerModifiers#flinging}
   * @param force       Current force value
   * @param multiplier  Force multiplier from non-power sources such as charge.
   * @return Updated force value.
   */
  float modifySlingForce(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier);

  /** Merger composing the results of each nested hook */
  record ComposeMerger(Collection<SlingForceModifierHook> modules) implements SlingForceModifierHook {
    @Override
    public float modifySlingForce(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier) {
      for (SlingForceModifierHook module : modules) {
        force = module.modifySlingForce(tool, modifier, holder, target, slingSource, force, multiplier);
      }
      return force;
    }
  }


  /** Helper to call {@link #modifySlingForce(IToolStackView, ModifierEntry, LivingEntity, LivingEntity, ModifierEntry, float, float)} on all modifiers. */
  static float modifySlingForce(IToolStackView tool, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier) {
    for (ModifierEntry entry : tool.getModifiers()) {
      force = entry.getHook(ModifierHooks.SLING_FORCE).modifySlingForce(tool, entry, holder, target, slingSource, force, multiplier);
      if (force <= 0) {
        return 0;
      }
    }
    return force;
  }
}
