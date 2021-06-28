package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.OffhandCooldownTracker;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.SingleKeyMultimap;
import slimeknights.tconstruct.tools.network.SwingArmPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

public class ToolAttackUtil {
  private static final UUID OFFHAND_DAMAGE_MODIFIER_UUID = UUID.fromString("fd666e50-d2cc-11eb-b8bc-0242ac130003");
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(TConstruct.modID + ".anti_knockback", 1f, Operation.ADDITION);
  /** Function to ignore attack cooldown */
  public static final DoubleSupplier NO_COOLDOWN = () -> 1.0;

  /**
   * Gets the cooldown function for the given player and hand
   * @param player  Player instance
   * @param hand    Attacking hand
   * @return  Cooldown function
   */
  public static DoubleSupplier getCooldownFunction(PlayerEntity player, Hand hand) {
    if (hand == Hand.OFF_HAND) {
      return () -> OffhandCooldownTracker.getCooldown(player);
    }
    return () -> player.getCooledAttackStrength(0.5f);
  }

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
    return attackEntity(weapon, ToolStack.from(stack), attacker, Hand.MAIN_HAND, targetEntity, getCooldownFunction(attacker, Hand.MAIN_HAND), false);
  }

  /**
   * Base attack logic, used by normal attacks, projectils, and extra attacks
   */
  public static boolean attackEntity(IModifiableWeapon weapon, IModifierToolStack tool, LivingEntity attackerLiving, Hand hand,
                                     Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack) {
    // TODO: general modifiable
    // broken? give to vanilla
    if (tool.isBroken()) {
      return false;
    }
    // nothing to do? cancel
    // TODO: is it a problem that we return true instead of false when isExtraAttack and the final damage is 0 or we fail to hit? I don't think anywhere clientside uses that
    if (attackerLiving.world.isRemote || !targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(attackerLiving)) {
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
    float cooldown = (float)cooldownFunction.getAsDouble();
    boolean fullyCharged = cooldown > 0.9f;


    // calculate if it's a critical hit
    // that is, in the air, not blind, targeting living, and not sprinting
    boolean isCritical = !isExtraAttack && fullyCharged && attackerLiving.fallDistance > 0.0F && !attackerLiving.isOnGround() && !attackerLiving.isOnLadder()
                         && !attackerLiving.isInWater() && !attackerLiving.isPotionActive(Effects.BLINDNESS)
                         && !attackerLiving.isPassenger() && targetLiving != null && !attackerLiving.isSprinting();

    // shared context for all modifier hooks
    ToolAttackContext context = new ToolAttackContext(attackerLiving, attackerPlayer, hand, targetEntity, targetLiving, isCritical, cooldown, isExtraAttack);

    // calculate actual damage
    // boost damage from traits
    float baseDamage = damage;
    List<ModifierEntry> modifiers = tool.getModifierList();
    for (ModifierEntry entry : modifiers) {
      damage = entry.getModifier().getEntityDamage(tool, entry.getLevel(), context, baseDamage, damage);
    }

    // no damage? do nothing
    if (damage <= 0) {
      return !isExtraAttack;
    }

    // if sprinting, deal bonus knockback
    float knockback = targetLiving != null ? 0.4f : 0; // vanilla applies 0.4 knockback to living via the attack hook
    SoundEvent sound;
    if (attackerLiving.isSprinting() && fullyCharged) {
      sound = SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK;
      knockback += 0.5f;
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
    for (ModifierEntry entry : modifiers) {
      knockback = entry.getModifier().beforeEntityHit(tool, entry.getLevel(), context, damage, baseKnockback, knockback);
    }

    // set hand for proper looting context
    ModifierLootingHandler.setLootingHand(attackerLiving, hand);

    // prevent knockback if needed
    Optional<ModifiableAttributeInstance> knockbackModifier = getKnockbackAttribute(targetLiving);
    // if knockback is below the vanilla amount, we need to prevent knockback, the remainder will be applied later
    boolean canceledKnockback = false;
    if (knockback < 0.4f) {
      canceledKnockback = true;
      knockbackModifier.ifPresent(ToolAttackUtil::disableKnockback);
    } else if (targetLiving != null) {
      // we will apply 0.4 of the knockback in the attack hook, need to apply the remainder ourself
      knockback -= 0.4f;
    }

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

    // reset knockback if needed
    if (canceledKnockback) {
      knockbackModifier.ifPresent(ToolAttackUtil::enableKnockback);
    }

    // if we failed to hit, fire failure hooks
    if (!didHit) {
      if (!isExtraAttack) {
        attackerLiving.world.playSound(null, attackerLiving.getPosX(), attackerLiving.getPosY(), attackerLiving.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attackerLiving.getSoundCategory(), 1.0F, 1.0F);
      }
      // alert modifiers nothing was hit, mainly used for fiery
      for (ModifierEntry entry : modifiers) {
        entry.getModifier().failedEntityHit(tool, entry.getLevel(), context);
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
    int durabilityLost = targetLiving != null ? 1 : 0;
    for (ModifierEntry entry : modifiers) {
      durabilityLost += entry.getModifier().afterEntityHit(tool, entry.getLevel(), context, damageDealt);
    }

    // hurt resistance adjustment for high speed weapons
    float speed = tool.getStats().getFloat(ToolStats.ATTACK_SPEED);
    int time = Math.round(20f / speed);
    if (time < targetEntity.hurtResistantTime) {
      targetEntity.hurtResistantTime = (targetEntity.hurtResistantTime + time) / 2;
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
    return attackEntity(weapon, tool, attackerLiving, hand, targetEntity, NO_COOLDOWN, true);
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

  /** Gets the knockback attribute instance if the modifier is not already present */
  private static Optional<ModifiableAttributeInstance> getKnockbackAttribute(@Nullable LivingEntity living) {
    return Optional.ofNullable(living)
                   .map(e -> e.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                   .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MODIFIER));
  }

  /** Enable the anti-knockback modifier */
  private static void disableKnockback(ModifiableAttributeInstance instance) {
    instance.applyNonPersistentModifier(ANTI_KNOCKBACK_MODIFIER);
  }

  /** Disables the anti knockback modifier */
  private static void enableKnockback(ModifiableAttributeInstance instance) {
    instance.removeModifier(ANTI_KNOCKBACK_MODIFIER);
  }

  /**
   * Adds secondary damage to an entity
   * @param source       Damage source
   * @param damage       Damage amount
   * @param target       Target entity
   * @param living       If the target is living, the living target. May be a different entity from target for multipart entities
   * @param noKnockback  If true, prevents extra knockback
   * @return  True if damaged
   */
  public static boolean attackEntitySecondary(DamageSource source, float damage, Entity target, @Nullable LivingEntity living, boolean noKnockback) {
    Optional<ModifiableAttributeInstance> knockbackResistance = getKnockbackAttribute(living);
    // store last damage before secondary attack
    float oldLastDamage = living == null ? 0 : living.lastDamage;

    // prevent knockback in secondary attacks, if requested
    if (noKnockback) {
      knockbackResistance.ifPresent(ToolAttackUtil::disableKnockback);
    }

    // set hurt resistance time to 0 because we always want to deal damage in traits
    target.hurtResistantTime = 0;
    boolean hit = target.attackEntityFrom(source, damage);
    // set total received damage, important for AI and stuff
    if (living != null) {
      living.lastDamage += oldLastDamage;
    }

    // remove no knockback marker
    if (noKnockback) {
      knockbackResistance.ifPresent(ToolAttackUtil::enableKnockback);
    }

    return hit;
  }


  /** Swings the entities hand without resetting cooldown */
  public static void swingHand(LivingEntity entity, Hand hand, boolean updateSelf) {
    if (!entity.isSwingInProgress || entity.swingProgressInt >= entity.getArmSwingAnimationEnd() / 2 || entity.swingProgressInt < 0) {
      entity.swingProgressInt = -1;
      entity.isSwingInProgress = true;
      entity.swingingHand = hand;
      if (!entity.world.isRemote) {
        SwingArmPacket packet = new SwingArmPacket(entity, hand);
        if (updateSelf) {
          TinkerNetwork.getInstance().sendToTrackingAndSelf(packet, entity);
        } else {
          TinkerNetwork.getInstance().sendToTracking(packet, entity);
        }
      }
    }
  }
}
