package slimeknights.tconstruct.library.modifiers.hook.combat;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook to modify damage dealt to an entity in melee
 */
public interface MeleeDamageModifierHook {
  /**
   * Called when an entity is attacked, before critical hit damage is calculated. Allows modifying the damage dealt.
   * Do not modify the entity here, its possible the attack will still be canceled without calling further hooks due to 0 damage being dealt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook}: Adjusts the base tool stats that show in the tooltip, but has less context for modification</li>
   *   <li>{@link MeleeHitModifierHook}: Allows modifying the entity directly instead of just adjusting damage to deal</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param modifier      Modifier level
   * @param context       Attack context
   * @param baseDamage    Base damage dealt before modifiers
   * @param damage        Computed damage from all prior modifiers
   * @return  New damage to deal
   */
  float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage);

  /** Merger that runs all nested hooks */
  record AllMerger(Collection<MeleeDamageModifierHook> modules) implements MeleeDamageModifierHook {
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
      for (MeleeDamageModifierHook module : modules) {
        damage = module.getMeleeDamage(tool, modifier, context, baseDamage, damage);
      }
      return damage;
    }
  }
}
