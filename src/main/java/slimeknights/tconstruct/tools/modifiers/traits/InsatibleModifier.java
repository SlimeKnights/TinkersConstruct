package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class InsatibleModifier extends Modifier {
  public InsatibleModifier() {
    super(0x9261cc);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    // gives +3 damage per level at max
    int effectLevel = TinkerModifiers.insatiableEffect.get().getLevel(context.getAttacker()) + 1;
    return damage + level * effectLevel / 3f;
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // 16 hits gets you to max, levels faster at higher levels
    if (!context.isExtraAttack()) {
      LivingEntity attacker = context.getAttacker();
      int effectLevel = Math.min(8, TinkerModifiers.insatiableEffect.get().getLevel(attacker) + 1);
      TinkerModifiers.insatiableEffect.get().apply(attacker, 5 * 20, effectLevel);
    }
    return 0;
  }
}
