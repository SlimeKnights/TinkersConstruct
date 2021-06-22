package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class InsatibleModifier extends Modifier {
  public InsatibleModifier() {
    super(0x9261cc);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, Hand hand, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged, boolean isExtraAttack) {
    // gives +3 damage per level at max
    int effectLevel = TinkerModifiers.insatiableEffect.get().getLevel(attacker) + 1;
    return damage + level * effectLevel / 3f;
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, Hand hand, LivingEntity target, float damageDealt, boolean isCritical, float cooldown, boolean isExtraAttack) {
    // 16 hits gets you to max, levels faster at higher levels
    if (!isExtraAttack) {
      int effectLevel = Math.min(8, TinkerModifiers.insatiableEffect.get().getLevel(attacker) + 1);
      TinkerModifiers.insatiableEffect.get().apply(attacker, 5 * 20, effectLevel);
    }
    return 0;
  }
}
