package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;

public class DecayModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook {
  /* gets the effect for the given level, including a random time */
  private static MobEffectInstance makeDecayEffect(int level) {
    // potions are 0 indexed instead of 1 indexed
    // wither skeletons apply 10 seconds of wither for comparison
    return new MobEffectInstance(MobEffects.WITHER, 20 * (5 + (RANDOM.nextInt(level * 3))), level - 1);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged()) {
      // note the time of each effect is calculated independantly

      // 25% chance to poison yourself
      if (RANDOM.nextInt(3) == 0) {
        context.getAttacker().addEffect(makeDecayEffect(level));
      }

      // always poison the target, means it works twice as often as lacerating
      LivingEntity target = context.getLivingTarget();
      if (target != null && target.isAlive()) {
        target.addEffect(makeDecayEffect(level));
      }
    }
    return 0;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && (!(projectile instanceof AbstractArrow arrow) || arrow.isCritArrow())) {
      // always poison the target, means it works twice as often as lacerating
      target.addEffect(makeDecayEffect(modifier.getLevel()));
    }
    return false;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary && (arrow == null || arrow.isCritArrow()) && RANDOM.nextInt(3) == 0) {
      // 25% chance to poison yourself
      shooter.addEffect(makeDecayEffect(modifier.getLevel()));
    }
  }
}
