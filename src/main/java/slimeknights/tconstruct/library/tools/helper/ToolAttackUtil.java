package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.traits.ITrait;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ToolAttackUtil {
  /**
   * Gets the actual damage a tool does
   *
   * @param stack the ItemStack to check
   * @param player the current player
   * @return the actual damage of the tool
   */
  public static float getActualDamage(ItemStack stack, @Nullable LivingEntity player) {
    float damage = (float) Attributes.ATTACK_DAMAGE.getDefaultValue();

    if (player != null) {
      ModifiableAttributeInstance instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
      if (instance != null) {
        damage = (float) instance.getValue();
      }
    }

    float toolDamage = ToolStack.from(stack).getStats().getAttackDamage();

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      toolDamage *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageModifier();
    }

    damage += toolDamage;

    if (stack.getItem() instanceof ToolCore) {
      damage = calculateCutoffDamage(damage, ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageCutoff());
    }

    return damage;
  }

  /**
   * Used to calculate the damage to start doing diminishing returns
   *
   * @param damageIn the current damage the tool does
   * @param cutoffDamage the fixed damage value for the diminishing effects to kick in
   * @return the damage to use from the cutoff
   */
  public static float calculateCutoffDamage(float damageIn, float cutoffDamage) {
    float percent = 1f;
    float oldDamage = damageIn;

    damageIn = 0f;
    while (oldDamage > cutoffDamage) {
      damageIn += percent * cutoffDamage;
      // safety for ridiculous values
      if (percent > 0.001f) {
        percent *= 0.9f;
      }
      else {
        damageIn += percent * cutoffDamage * ((oldDamage / cutoffDamage) - 1f);
        return damageIn;
      }

      oldDamage -= cutoffDamage;
    }

    damageIn += percent * oldDamage;

    return damageIn;
  }

  /**
   * General version of attackEntity. Applies cooldowns but has no projectile entity
   */
  public static boolean attackEntity(ItemStack stack, ToolCore tool, LivingEntity attacker, Entity targetEntity) {
    return attackEntity(stack, tool, attacker, targetEntity, null, true);
  }

  /**
   * Version of attackEntity for use with projectiles. Does not apply cooldowns and has a separate projectile for logic
   */
  public static boolean attackEntity(ItemStack stack, ToolCore tool, LivingEntity attacker, Entity targetEntity, Entity projectileEntity) {
    return attackEntity(stack, tool, attacker, targetEntity, projectileEntity, false);
  }

  /**
   * Makes all the calls to attack an entity. Takes enchantments and potions and traits into account. Basically call this when a tool deals damage.
   * Most of this function is the same as {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem(Entity targetEntity)}
   */
  public static boolean attackEntity(ItemStack stack, ToolCore tool, LivingEntity attacker, Entity targetEntity, @Nullable Entity projectileEntity, boolean applyCoolDown) {
    // nothing to do, no target?
    if (!targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(attacker) || !stack.hasTag()) {
      return false;
    }

    if (ToolDamageUtil.isBroken(stack)) {
      return false;
    }

    boolean isProjectile = projectileEntity != null;
    LivingEntity target = null;
    PlayerEntity player = null;

    if (targetEntity instanceof LivingEntity) {
      target = (LivingEntity) targetEntity;
    }

    if (attacker instanceof PlayerEntity) {
      player = (PlayerEntity) attacker;
      if (target instanceof PlayerEntity) {
        if (!player.canAttackPlayer((PlayerEntity) target)) {
          return false;
        }
      }
    }

    // traits on the tool
    // todo traits
    List<ITrait> traits = new ArrayList<>();//TinkerUtil.getTraitsOrdered(stack);

    // players base damage (includes tools damage stat)
    float baseDamage = (float) attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();

    // missing because not supported by tcon tools: vanilla damage enchantments, we have our own modifiers
    // missing because not supported by tcon tools: vanilla knockback enchantments, we have our own modifiers
    float baseKnockBack = attacker.isSprinting() ? 1 : 0;

    // calculate if it's a critical hit
    boolean isCritical = attacker.fallDistance > 0.0F && !attacker.isOnGround() && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(Effects.BLINDNESS) && !attacker.isPassenger();

    for (ITrait trait : traits) {
      if (trait.isCriticalHit(stack, attacker, target)) {
        isCritical = true;
      }
    }

    // calculate actual damage
    float damage = baseDamage;
    if (target != null) {
      for (ITrait trait : traits) {
        damage = trait.damage(stack, attacker, target, baseDamage, damage, isCritical);
      }
    }

    // apply critical damage
    if (isCritical) {
      damage *= 1.5f;
    }

    // calculate cutoff
    damage = calculateCutoffDamage(damage, tool.getToolDefinition().getBaseStatDefinition().getDamageCutoff());

    // calculate actual knock back
    float knockBack = baseKnockBack;

    if (target != null) {
      for (ITrait trait : traits) {
        knockBack = trait.knockBack(stack, attacker, target, damage, baseKnockBack, knockBack, isCritical);
      }
    }

    // missing because not supported by tcon tools: vanilla fire aspect enchantments, we have our own modifiers

    float oldHP = 0;

    double oldVelX;
    double oldVelY;
    double oldVelZ;

    if (target != null) {
      oldHP = target.getHealth();
    }

    // apply cooldown damage decrease
    SoundEvent sound = null;

    if (player != null) {
      float coolDown = ((PlayerEntity) attacker).getCooledAttackStrength(0.5F);

      sound = coolDown > 0.9f ? SoundEvents.ENTITY_PLAYER_ATTACK_STRONG : SoundEvents.ENTITY_PLAYER_ATTACK_WEAK;
      damage *= (0.2F + coolDown * coolDown * 0.8F);
    }

    // deal the damage
    if (target != null) {
      int hurtResistantTime = target.hurtResistantTime;

      for (ITrait trait : traits) {
        trait.onHit(stack, attacker, target, damage, isCritical);

        // reset hurt resistant time
        target.hurtResistantTime = hurtResistantTime;
      }
    }

    boolean hit = tool.dealDamage(stack, attacker, targetEntity, damage);
    // todo fix
    /*
    if(isProjectile && tool instanceof IProjectile) {
      hit = ((IProjectile) tool).dealDamageRanged(stack, projectileEntity, attacker, targetEntity, damage);
    }
    else {
      hit = tool.dealDamage(stack, attacker, targetEntity, damage);
    }*/

    // did we hit?
    if (hit && target != null) {
      // actual damage dealt
      float damageDealt = oldHP - target.getHealth();

      // apply knockback modifier
      oldVelX = target.getMotion().getX();
      oldVelY = target.getMotion().getY();
      oldVelZ = target.getMotion().getZ();

      target.setMotion(oldVelX + (target.getMotion().getX() - oldVelX) * tool.getToolDefinition().getBaseStatDefinition().getKnockbackModifier(),
        oldVelY + (target.getMotion().getY() - oldVelY) * tool.getToolDefinition().getBaseStatDefinition().getKnockbackModifier() / 3f,
        oldVelZ + (target.getMotion().getZ() - oldVelZ) * tool.getToolDefinition().getBaseStatDefinition().getKnockbackModifier()
      );

      // apply knockback
      if (knockBack > 0f) {
        double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockBack * 0.5F;
        double velZ = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockBack * 0.5F;
        targetEntity.addVelocity(velX, 0.1d, velZ);

        // slow down player
        attacker.setMotion(attacker.getMotion().mul(0.6D, 1.0D, 0.6D));
        attacker.setSprinting(false);
      }

      if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged) {
        TinkerNetwork.getInstance().sendVanillaPacket(targetEntity, new SEntityVelocityPacket(targetEntity));
        targetEntity.velocityChanged = false;
        targetEntity.setMotion(oldVelX, oldVelY, oldVelZ);
      }

      if (player != null) {
        // vanilla critical callback
        if (isCritical) {
          player.onCriticalHit(target);
          sound = SoundEvents.ENTITY_PLAYER_ATTACK_CRIT;
        }

        // "magical" critical damage? (aka caused by modifiers)
        if (damage > baseDamage) {
          // this usually only displays some particles :)
          player.onEnchantmentCritical(targetEntity);
        }
      }

      attacker.setLastAttackedEntity(target);

      // call post-hit callbacks before reducing the durability
      for (ITrait trait : traits) {
        trait.afterHit(stack, attacker, target, damageDealt, isCritical, true); // hit is always true
      }

      // damage the tool
      if (player != null) {
        stack.hitEntity(target, player);

        if (!player.abilities.isCreativeMode && !isProjectile) {
          float damageOut = Math.max(1f, damage / 10f);

          if (!tool.getToolDefinition().hasCategory(Category.WEAPON)) {
            damageOut *= 2;
          }

          stack.damageItem((int) damageOut, player, livingEntity -> livingEntity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }

        player.addStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10f));
        player.addExhaustion(0.3f);

        if (player.getEntityWorld() instanceof ServerWorld && damageDealt > 2f) {
          int k = (int) (damageDealt * 0.5);

          ((ServerWorld) player.getEntityWorld()).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosY() + targetEntity.getHeight() * 0.5F, targetEntity.getPosZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
        }

        // cooldown for non-projectiles
        if (!isProjectile && applyCoolDown) {
          player.resetCooldown();
        }
      }
      else if (!isProjectile) {
        float damageOut = Math.max(1f, damage / 10f);

        if (!tool.getToolDefinition().hasCategory(Category.WEAPON)) {
          damageOut *= 2;
        }

        stack.damageItem((int) damageOut, player, livingEntity -> livingEntity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
      }
    }
    else {
      sound = SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE;
    }

    if (player != null && sound != null) {
      player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), sound, player.getSoundCategory(), 1.0F, 1.0F);
    }

    return true;
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
