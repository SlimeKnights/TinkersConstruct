package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class FieryModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook, OnAttackedModifierHook, MeleeHitModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT);
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // vanilla hack: apply fire so the entity drops the proper items on instant kill
    LivingEntity target = context.getLivingTarget();
    if (target != null && !target.isOnFire()) {
      target.setRemainingFireTicks(1);
    }
    return knockback;
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    // conclusion of vanilla hack: we don't want the target on fire if we did not hit them
    LivingEntity target = context.getLivingTarget();
    if (target != null && target.isOnFire()) {
      target.clearFire();
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    projectile.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 20));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    hit.getEntity().setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker != null) {
      // 15% chance of working per level, doubled bonus on shields
      int level = modifier.getLevel();
      if (slotType.getType() == Type.HAND) {
        level *= 2;
      }
      if (RANDOM.nextFloat() < (level * 0.15f)) {
        attacker.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }
}
