package slimeknights.tconstruct.library.modifiers.hook.special.sling;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Modifier to change the angle of a {@link slimeknights.tconstruct.tools.modifiers.ability.sling.SlingModifier}. */
public interface SlingAngleModifierHook {
  /**
   * Called when a sling is used to allow modifiers to adjust the sling arc.
   *
   * @param tool        Active tool
   * @param modifier    Modifier entry calling the hook
   * @param holder      Entity holding the sling
   * @param target      Entity being moved by the sling. Might be the same as {@code holder}, but not always (see bonking).
   * @param slingSource Modifier entry of the sling modifier, such as {@link slimeknights.tconstruct.tools.TinkerModifiers#flinging}
   * @param power       Force to be applied from power and velocity.
   * @param multiplier  Force multiplier from other sources such as sling type and partial charge.
   * @param angle       Current direction to sling.
   * @return Updated direction
   */
  Vec3 modifySlingAngle(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float power, float multiplier, Vec3 angle);

  /** Merger composing the results of each nested hook */
  record ComposeMerger(Collection<SlingAngleModifierHook> modules) implements SlingAngleModifierHook {
    @Override
    public Vec3 modifySlingAngle(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
      for (SlingAngleModifierHook module : modules) {
        angle = module.modifySlingAngle(tool, modifier, holder, target, slingSource, force, multiplier, angle);
      }
      return angle;
    }
  }


  /** Helper to call {@link #modifySlingAngle(IToolStackView, ModifierEntry, LivingEntity, LivingEntity, ModifierEntry, float, float, Vec3)} on all modifiers. */
  static Vec3 modifySlingAngle(IToolStackView tool, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
    for (ModifierEntry entry : tool.getModifiers()) {
      angle = entry.getHook(ModifierHooks.SLING_ANGLE).modifySlingAngle(tool, entry, holder, target, slingSource, force, multiplier, angle);
    }
    return angle;
  }
}
