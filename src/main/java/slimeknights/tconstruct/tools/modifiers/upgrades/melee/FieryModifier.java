package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class FieryModifier extends IncrementalModifier {
  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // vanilla hack: apply fire so the entity drops the proper items on instant kill
    LivingEntity target = context.getLivingTarget();
    if (target != null && !target.isOnFire()) {
      target.setRemainingFireTicks(1);
    }
    return knockback;
  }

  @Override
  public void failedEntityHit(IToolStackView tool, int level, ToolAttackContext context) {
    // conclusion of vanilla hack: we don't want the target on fire if we did not hit them
    LivingEntity target = context.getLivingTarget();
    if (target != null && target.isOnFire()) {
      target.clearFire();
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.setRemainingFireTicks(Math.round(getScaledLevel(tool, level) * 5));
    }
    return 0;
  }
}
