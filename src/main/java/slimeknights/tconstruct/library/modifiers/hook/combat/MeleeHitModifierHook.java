package slimeknights.tconstruct.library.modifiers.hook.combat;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hooks that run when an entity is attacked to allow applying special effects
 */
public interface MeleeHitModifierHook {

  /**
   * Called right before an entity is hit, used to modify knockback applied or to apply special effects that need to run before damage. Damage is final damage including critical damage.
   * Note there is still a chance this attack won't deal damage, if that happens {@link #failedMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float)} will run.
   * TODO 1.19: Separate before entity hit from knockback?
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float)}: Perform special attacks on entity hit beyond knockback boosts</li>
   * </ul>
   * @param tool           Tool used to attack
   * @param modifier       Modifier level
   * @param context        Attack context
   * @param damage         Damage to deal to the attacker
   * @param baseKnockback  Base knockback before modifiers
   * @param knockback      Computed knockback from all prior modifiers
   * @return  New knockback to apply. 0.5 is equivelent to 1 level of the vanilla enchant
   */
  default float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    return knockback;
  }

  /**
   * Called after a living entity is successfully attacked. Used to apply special effects on hit.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook}: Adjusts the base tool stats that affect damage</li>
   *   <li>{@link MeleeDamageModifierHook}: Change the amount of damage dealt with attacker context</li>
   *   <li>{@link #beforeMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float, float, float)}: Change the amount of knockback dealt</li>
   *   <li>{@link #failedMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float)}: Called after living hit when damage was not dealt</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param modifier      Modifier level
   * @param context       Attack context
   * @param damageDealt   Amount of damage successfully dealt
   */
  default void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {}

  /**
   * Called after attacking an entity when no damage was dealt
   * @param tool             Tool used to attack
   * @param modifier         Modifier level
   * @param context          Attack context
   * @param damageAttempted  Amount of damage that was attempted to be dealt
   */
  default void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {}

  /** Merger that runs all nested hooks */
  record AllMerger(Collection<MeleeHitModifierHook> modules) implements MeleeHitModifierHook {
    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
      for (MeleeHitModifierHook module : modules) {
        knockback = module.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
      }
      return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
      for (MeleeHitModifierHook module : modules) {
        module.afterMeleeHit(tool, modifier, context, damageDealt);
      }
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
      for (MeleeHitModifierHook module : modules) {
        module.failedMeleeHit(tool, modifier, context, damageAttempted);
      }
    }
  }
}
