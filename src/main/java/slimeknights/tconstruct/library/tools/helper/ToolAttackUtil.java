package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

public class ToolAttackUtil {
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;

  /**
   * Gets the actual damage a tool does
   *
   * @param stack the ItemStack to check
   * @param player the current player
   * @return the actual damage of the tool
   */
  public static float getActualDamage(ToolStack stack, @Nullable LivingEntity player) {
    float damage = (float) Attributes.ATTACK_DAMAGE.getDefaultValue();
    if (player != null) {
      ModifiableAttributeInstance instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
      if (instance != null) {
        damage = (float) instance.getValue();
      }
    }
    return damage + stack.getStats().getAttackDamage();
  }

  /**
   * General version of attackEntity. Applies cooldowns but has no projectile entity
   */
  public static boolean attackEntity(ItemStack stack, IModifiableWeapon weapon, PlayerEntity attacker, Entity targetEntity) {
    return attackEntity(stack, weapon, attacker, targetEntity, null, true);
  }

  /**
   * Version of attackEntity for use with projectiles. Does not apply cooldowns and has a separate projectile for logic
   */
  public static boolean attackEntity(ItemStack stack, IModifiableWeapon weapon, PlayerEntity attacker, Entity targetEntity, Entity projectileEntity) {
    return attackEntity(stack, weapon, attacker, targetEntity, projectileEntity, false);
  }

  /** Performs a standard attack */
  public static boolean dealDefaultDamage(LivingEntity attacker, Entity target, float damage) {
    if (attacker instanceof PlayerEntity) {
      return target.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), damage);
    }
    return target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
  }

  /**
   * Makes all the calls to attack an entity. Takes enchantments and potions and traits into account. Basically call this when a tool deals damage.
   * Most of this function is the same as {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem(Entity targetEntity)}
   * @return true if replaces vanilla logic
   */
  public static boolean attackEntity(ItemStack stack, IModifiableWeapon weapon, LivingEntity attackerLiving, Entity targetEntity, @Nullable Entity projectileEntity, boolean applyCoolDown) {
    return attackEntity(stack, weapon, ToolStack.from(stack), attackerLiving, targetEntity, projectileEntity, applyCoolDown, false);
  }

  /**
   * Base attack logic, used by normal attacks, projectils, and extra attacks
   */
  public static boolean attackEntity(ItemStack stack, IModifiableWeapon weapon, ToolStack tool, LivingEntity attackerLiving, Entity targetEntity,
                                     @Nullable Entity projectileEntity, boolean applyCoolDown, boolean isExtraAttack) {
    // no NBT? give to vanilla
    // TODO: general modifiable
    // broken? give to vanilla
    if (tool.isBroken()) {
      return false;
    }
    // nothing to do? cancel
    if (!targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(attackerLiving)) {
      return true;
    }

    // fetch relevant entities
    LivingEntity targetLiving = null;
    if (targetEntity instanceof LivingEntity) {
      targetLiving = (LivingEntity) targetEntity;
    } else if (targetEntity instanceof PartEntity) {
      Entity parent = ((PartEntity<?>) targetEntity).getParent();
      if (parent instanceof LivingEntity) {
        targetLiving = (LivingEntity)parent;
      }
    }
    PlayerEntity attackerPlayer = null;
    if (attackerLiving instanceof PlayerEntity) {
      attackerPlayer = (PlayerEntity) attackerLiving;
    }


    // players base damage (includes tools damage stat)
    float damage = (float) attackerLiving.getAttributeValue(Attributes.ATTACK_DAMAGE);

    // missing: enchantment modifiers, we handle ourselves

    // determine cooldown
    float cooldown = 1.0f;
    if (applyCoolDown && attackerPlayer != null) {
      cooldown = attackerPlayer.getCooledAttackStrength(0.5f);
      // cooldown reset by player controller
    }
    boolean fullyCharged = cooldown > 0.9f;


    // calculate if it's a critical hit
    // that is, in the air, not blind, targeting living, and not sprinting
    boolean isCritical = !isExtraAttack && fullyCharged && attackerLiving.fallDistance > 0.0F && !attackerLiving.isOnGround() && !attackerLiving.isOnLadder()
                         && !attackerLiving.isInWater() && !attackerLiving.isPotionActive(Effects.BLINDNESS)
                         && !attackerLiving.isPassenger() && targetLiving != null && !attackerLiving.isSprinting();

    // calculate actual damage
    // boost damage from traits
    float baseDamage = damage;
    List<ModifierEntry> modifiers = tool.getModifierList();
    if (targetLiving != null) {
      for (ModifierEntry entry : modifiers) {
        damage = entry.getModifier().applyLivingDamage(tool, entry.getLevel(), attackerLiving, targetLiving, baseDamage, damage, isCritical, fullyCharged);
      }
    }

    // no damage? do nothing
    if (damage <= 0) {
      return !isExtraAttack;
    }

    // if sprinting, deal bonus knockback
    float knockback = 0;
    SoundEvent sound;
    if (attackerLiving.isSprinting() && fullyCharged) {
      sound = SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK;
      knockback = 0.5f;
    } else if (fullyCharged) {
      sound = SoundEvents.ENTITY_PLAYER_ATTACK_STRONG;
    } else {
      sound = SoundEvents.ENTITY_PLAYER_ATTACK_WEAK;
    }

    // knockback moved lower

    // apply critical boost
    if (!isExtraAttack) {
      float criticalModifier = isCritical ? 1.5f : 1.0f;
      if (attackerPlayer != null) {
        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(attackerPlayer, targetEntity, isCritical, isCritical ? 1.5F : 1.0F);
        isCritical = hitResult != null;
        if (isCritical) {
          criticalModifier = hitResult.getDamageModifier();
        }
      }
      if (isCritical) {
        damage *= criticalModifier;
      }
    }

    // removed: sword check hook, replaced by weapon callback
    // removed: fire aspect check, replaced by before damage lower

    // apply cutoff and cooldown, store if damage was above base for magic particles
    boolean isMagic = damage > baseDamage;
    if (cooldown < 1) {
      damage *= (0.2f + cooldown * cooldown * 0.8f);
    }

    // track original health and motion before attack
    Vector3d originalTargetMotion = targetEntity.getMotion();
    float oldHealth = 0.0F;
    if (targetLiving != null) {
      oldHealth = targetLiving.getHealth();
    }

    // removed: vanilla knockback enchant support
    // changed: knockback halved for simplicity

    // apply modifier knockback and special effects
    float baseKnockback = knockback;
    if (targetLiving != null) {
      for (ModifierEntry entry : modifiers) {
        knockback = entry.getModifier().beforeLivingHit(tool, entry.getLevel(), attackerLiving, targetLiving, damage, baseKnockback, knockback, isCritical, fullyCharged);
      }
    }

    ///////////////////
    // actual attack //
    ///////////////////

    // removed: sword special attack check and logic, replaced by this
    boolean didHit;
    if (isExtraAttack) {
      didHit = dealDefaultDamage(attackerLiving, targetEntity, damage);
    } else {
      didHit = weapon.dealDamage(tool, attackerLiving, targetEntity, damage, isCritical, fullyCharged);
    }
    if (!didHit) {
      if (!isExtraAttack) {
        attackerLiving.world.playSound(null, attackerLiving.getPosX(), attackerLiving.getPosY(), attackerLiving.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attackerLiving.getSoundCategory(), 1.0F, 1.0F);
      }
      // alert modifiers nothing was hit, mainly used for fiery
      if (targetLiving != null) {
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().failedLivingHit(tool, entry.getLevel(), attackerLiving, targetLiving, isCritical, fullyCharged);
        }
      }

      return !isExtraAttack;
    }

    // determine damage actually dealt
    float damageDealt = damage;
    if (targetLiving != null) {
      damageDealt = oldHealth - targetLiving.getHealth();
    }

    // apply knockback
    if (knockback > 0) {
      if (targetLiving != null) {
        targetLiving.applyKnockback(knockback, MathHelper.sin(attackerLiving.rotationYaw * DEGREE_TO_RADIANS), -MathHelper.cos(attackerLiving.rotationYaw * DEGREE_TO_RADIANS));
      } else {
        targetEntity.addVelocity(-MathHelper.sin(attackerLiving.rotationYaw * DEGREE_TO_RADIANS) * knockback, 0.1d, MathHelper.cos(attackerLiving.rotationYaw * DEGREE_TO_RADIANS) * knockback);
      }
      attackerLiving.setMotion(attackerLiving.getMotion().mul(0.6D, 1.0D, 0.6D));
      attackerLiving.setSprinting(false);
    }

    // removed: sword sweep attack, handled above

    // apply velocity change to players if needed
    if (targetEntity.velocityChanged && targetEntity instanceof ServerPlayerEntity) {
      ((ServerPlayerEntity)targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
      targetEntity.velocityChanged = false;
      targetEntity.setMotion(originalTargetMotion);
    }

    // play sound effects and particles
    if (attackerPlayer != null) {
      // particles
      if (isCritical) {
        sound = SoundEvents.ENTITY_PLAYER_ATTACK_CRIT;
        attackerPlayer.onCriticalHit(targetEntity);
      }
      if (isMagic) {
        attackerPlayer.onEnchantmentCritical(targetEntity);
      }
      // sounds
      if (sound != null) {
        attackerLiving.world.playSound(null, attackerLiving.getPosX(), attackerLiving.getPosY(), attackerLiving.getPosZ(), sound, attackerLiving.getSoundCategory(), 1.0F, 1.0F);
      }
    }
    if (attackerLiving.world instanceof ServerWorld && damageDealt > 2.0F) {
      int particleCount = (int)(damageDealt * 0.5f);
      ((ServerWorld)attackerLiving.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosYHeight(0.5), targetEntity.getPosZ(), particleCount, 0.1, 0, 0.1, 0.2);
    }

    // deal attacker thorns damage
    attackerLiving.setLastAttackedEntity(targetEntity);
    if (targetLiving != null) {
      EnchantmentHelper.applyThornEnchantments(targetLiving, attackerLiving);
    }

    // apply modifier effects
    // removed: bane of arthropods hook, replaced by this
    int durabilityLost = 1;
    if (targetLiving != null) {
      for (ModifierEntry entry : modifiers) {
        durabilityLost += entry.getModifier().afterLivingHit(tool, entry.getLevel(), attackerLiving, targetLiving, damageDealt, isCritical, fullyCharged);
      }
    }

    // final attack hooks
    if (attackerPlayer != null) {
      if (targetLiving != null) {
        if (!attackerLiving.world.isRemote && !stack.isEmpty()) {
          stack.hitEntity(targetLiving, attackerPlayer);
        }
        attackerPlayer.addStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
      }
      // removed: fire damage, handled in modifier hook above
      attackerPlayer.addExhaustion(0.1F);
    }

    // damage the tool
    if (!TinkerTags.Items.COMBAT.contains(tool.getItem())) {
      durabilityLost *= 2;
    }
    ToolDamageUtil.damageAnimated(tool, durabilityLost, attackerLiving);

    return true;
  }

  /**
   * Applies a secondary attack to an entity, notably not running AOE attacks from the tool logic
   * @param weapon          Weapon doing attacking
   * @param tool            Tool instance
   * @param attackerLiving  Attacker
   * @param targetEntity    Target
   * @return  True if hit
   */
  public static boolean extraEntityAttack(IModifiableWeapon weapon, ToolStack tool, LivingEntity attackerLiving, Entity targetEntity) {
    return attackEntity(ItemStack.EMPTY, weapon, tool, attackerLiving, targetEntity, null, false, true);
  }

  /**
   * Spawns a given particle at the given entity's position with an offset
   *
   * @param particleData the selected particle
   * @param entity the entity
   * @param height the height offset for the particle position
   */
  public static void spawnAttachParticle(IParticleData particleData, Entity entity, double height) {
    double xd = -MathHelper.sin(entity.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
    double zd = +MathHelper.cos(entity.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
    double yd = -MathHelper.sin(entity.rotationPitch / 180.0F * (float) Math.PI);

    xd *= 1f;
    yd *= 1f;
    zd *= 1f;

    if (entity.world instanceof ServerWorld) {
      ((ServerWorld) entity.world).spawnParticle(particleData, entity.getPosX() + xd, entity.getPosY() + entity.getHeight() * height, entity.getPosZ() + zd, 0, xd, yd, zd, 1.0D);
    }
  }
}
