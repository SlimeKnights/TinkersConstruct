package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;

public class FieryModifier extends IncrementalModifier implements ProjectileLaunchModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.PROJECTILE_LAUNCH);
  }

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
      target.setSecondsOnFire(Math.round(getEffectiveLevel(tool, level) * 5));
    }
    return 0;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    projectile.setSecondsOnFire(Math.round(modifier.getEffectiveLevel(tool) * 20));
  }
}
