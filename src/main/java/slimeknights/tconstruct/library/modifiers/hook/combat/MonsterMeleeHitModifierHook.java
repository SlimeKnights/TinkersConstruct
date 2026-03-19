package slimeknights.tconstruct.library.modifiers.hook.combat;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook used to allow monsters to run melee hit effects.
 * Unlike players, monsters run all melee logic right before the actual hit happens, so we can guarantee success.
 * @see MeleeHitModifierHook
 */
public interface MonsterMeleeHitModifierHook {
  /**
   * Called before a monster deals melee damage to a target with a modifiable melee weapon.
   * It is too late for the hit to fail, but we also lack a post-hit callback.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook}: Adjusts the base tool stats that affect damage</li>
   *   <li>{@link MeleeDamageModifierHook}: Change the amount of damage dealt with attacker context</li>
   *   <li>{@link MeleeHitModifierHook}: Used for players to apply effects on melee hit.</li>
   * </ul>
   * @param tool       Tool used to attack
   * @param modifier   Modifier level
   * @param context    Attack context
   * @param damage     Amount of damage to deal. Should match exactly to the damage that will be taken, but has not been dealt yet.
   */
  void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage);

  /** Merger that runs all nested hooks */
  record AllMerger(Collection<MonsterMeleeHitModifierHook> modules) implements MonsterMeleeHitModifierHook {
    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
      for (MonsterMeleeHitModifierHook module : modules) {
        module.onMonsterMeleeHit(tool, modifier, context, damageDealt);
      }
    }
  }

  /** Helper that just redirects the monster method to the melee hit hook. Should only be used when certain the after hook can run before the damage is dealt */
  interface RedirectAfter extends MonsterMeleeHitModifierHook, MeleeHitModifierHook {
    @Override
    default void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
      afterMeleeHit(tool, modifier, context, damage);
    }
  }
}
