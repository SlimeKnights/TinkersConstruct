package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
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
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.SingleKeyMultimap;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ToolAttackUtil {
  private static final UUID OFFHAND_DAMAGE_MODIFIER_UUID = UUID.fromString("fd666e50-d2cc-11eb-b8bc-0242ac130003");
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;

  /**
   * Gets the attack damage for the given hand, acting as though it was used in the main hand
   *
   * If your goal is damage for display, you are better off checking the tool attack damage stat directly, then displaying relevant attribute modifiers in the tooltip
   * @param tool     Held tool
   * @param holder   Entity holding the tool
   * @param hand     Hand used
   * @return  Attack damage
   */
  public static float getAttributeAttackDamage(IModifierToolStack tool, LivingEntity holder, Hand hand) {
    if (hand == Hand.OFF_HAND && !holder.world.isRemote()) {
      // first, get a map of existing damage modifiers to exclude
      Multimap<Attribute,AttributeModifier> mainModifiers = new SingleKeyMultimap<>(Attributes.ATTACK_DAMAGE, holder.getHeldItemMainhand().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE));

      // next, build a list of damage modifiers from the offhand stack, handled directly as it saves parsing the tool twice and lets us simplify by filtering
      ImmutableList.Builder<AttributeModifier> listBuilder = ImmutableList.builder();
      listBuilder.add(new AttributeModifier(OFFHAND_DAMAGE_MODIFIER_UUID, "tconstruct.tool.offhand_attack_damage", tool.getStats().getFloat(ToolStats.ATTACK_DAMAGE), AttributeModifier.Operation.ADDITION));
      BiConsumer<Attribute, AttributeModifier> attributeConsumer = (attribute, modifier) -> {
        if (attribute == Attributes.ATTACK_DAMAGE) {
          listBuilder.add(modifier);
        }
      };
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().addAttributes(tool, entry.getLevel(), EquipmentSlotType.MAINHAND, attributeConsumer);
      }
      Multimap<Attribute,AttributeModifier> offhandModifiers = new SingleKeyMultimap<>(Attributes.ATTACK_DAMAGE, listBuilder.build());

      // remove the old, add the new
      AttributeModifierManager modifiers = holder.getAttributeManager();
      modifiers.removeModifiers(mainModifiers);
      modifiers.reapplyModifiers(offhandModifiers);
      // fetch damage using these temporary modifiers
      float damage = (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
      // revert modifiers to the original state
      modifiers.removeModifiers(offhandModifiers);
      modifiers.reapplyModifiers(mainModifiers);
      return damage;
    } else {
      // if is the held tool, attributes are already set up
      return (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }
  }

  /** Performs a standard attack */
  public static boolean dealDefaultDamage(LivingEntity attacker, Entity target, float damage) {
    if (attacker instanceof PlayerEntity) {
      return target.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), damage);
    }
    return target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
  }

  /**
   * General version of attackEntity. Applies cooldowns but has no projectile entity
   */
  public static boolean attackEntity(ItemStack stack, IModifiableWeapon weapon, PlayerEntity attacker, Entity targetEntity) {
    return attackEntity(weapon, ToolStack.from(stack), attacker, Hand.MAIN_HAND, targetEntity, true, false);
  }

  /**
   * Base attack logic, used by normal attacks, projectils, and extra attacks
   */
  public static boolean attackEntity(IModifiableWeapon weapon, IModifierToolStack tool, LivingEntity attackerLiving, Hand hand,
                                     Entity targetEntity, boolean applyCoolDown, boolean isExtraAttack) {
    // TODO: general modifiable
    // broken? give to vanilla
    if (tool.isBroken()) {
      return false;
    }
    // nothing to do? cancel
    // TODO: is it a problem that we return true instead of false when isExtraAttack and the final damage is 0 or we fail to hit? I don't think anywhere clientside uses that
    if (attackerLiving.getEntityWorld().isRemote || !targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(attackerLiving)) {
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
    // hack for offhand attributes: remove mainhand temporarily, and apply offhand
    float damage = getAttributeAttackDamage(tool, attackerLiving, hand);

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

    // shared context for all modifier hooks
    ToolAttackContext context = new ToolAttackContext(attackerLiving, attackerPlayer, hand, targetLiving, isCritical, cooldown, isExtraAttack);

    // calculate actual damage
    // boost damage from traits
    float baseDamage = damage;
    List<ModifierEntry> modifiers = tool.getModifierList();
    if (targetLiving != null) {
      for (ModifierEntry entry : modifiers) {
        damage = entry.getModifier().applyLivingDamage(tool, entry.getLevel(), context, baseDamage, damage);
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
        knockback = entry.getModifier().beforeLivingHit(tool, entry.getLevel(), context, damage, baseKnockback, knockback);
      }
    }

    // set hand for proper looting context
    ModifierLootingHandler.setLootingHand(attackerLiving, hand);

    ///////////////////
    // actual attack //
    ///////////////////

    // removed: sword special attack check and logic, replaced by this
    boolean didHit;
    if (isExtraAttack) {
      didHit = dealDefaultDamage(attackerLiving, targetEntity, damage);
    } else {
      didHit = weapon.dealDamage(tool, context, damage);
    }
    // reset hand to make sure we don't mess with vanilla tools
    ModifierLootingHandler.setLootingHand(attackerLiving, Hand.MAIN_HAND);
    if (!didHit) {
      if (!isExtraAttack) {
        attackerLiving.world.playSound(null, attackerLiving.getPosX(), attackerLiving.getPosY(), attackerLiving.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attackerLiving.getSoundCategory(), 1.0F, 1.0F);
      }
      // alert modifiers nothing was hit, mainly used for fiery
      if (targetLiving != null) {
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().failedLivingHit(tool, entry.getLevel(), context);
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
        durabilityLost += entry.getModifier().afterLivingHit(tool, entry.getLevel(), context, damageDealt);
      }
    }

    // final attack hooks
    if (attackerPlayer != null) {
      if (targetLiving != null) {
        if (!attackerLiving.world.isRemote && !isExtraAttack) {
          ItemStack held = attackerLiving.getHeldItem(hand);
          if (!held.isEmpty()) {
            held.hitEntity(targetLiving, attackerPlayer);
          }
        }
        attackerPlayer.addStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
      }
      // removed: fire damage, handled in modifier hook above
      attackerPlayer.addExhaustion(0.1F);
    }

    // damage the tool
    if (!TinkerTags.Items.MELEE_PRIMARY.contains(tool.getItem())) {
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
  public static boolean extraEntityAttack(IModifiableWeapon weapon, IModifierToolStack tool, LivingEntity attackerLiving, Hand hand, Entity targetEntity) {
    return attackEntity(weapon, tool, attackerLiving, hand, targetEntity, false, true);
  }

  /**
   * Spawns a given particle at the given entity's position with an offset
   *
   * @param particleData the selected particle
   * @param entity the entity
   * @param height the height offset for the particle position
   */
  public static void spawnAttackParticle(IParticleData particleData, Entity entity, double height) {
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
