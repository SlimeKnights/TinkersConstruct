package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.mantle.util.SingleKeyMultimap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

public class ToolAttackUtil {
  private static final UUID OFFHAND_DAMAGE_MODIFIER_UUID = UUID.fromString("fd666e50-d2cc-11eb-b8bc-0242ac130003");
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(TConstruct.MOD_ID + ".anti_knockback", 1f, Operation.ADDITION);
  /** Function to ignore attack cooldown */
  public static final DoubleSupplier NO_COOLDOWN = () -> 1.0;

  /**
   * Gets the cooldown function for the given player and hand
   * @param player  Player instance
   * @param hand    Attacking hand
   * @return  Cooldown function
   */
  public static DoubleSupplier getCooldownFunction(Player player, InteractionHand hand) {
    if (hand == InteractionHand.OFF_HAND) {
      return () -> OffhandCooldownTracker.getCooldown(player);
    }
    return () -> player.getAttackStrengthScale(0.5f);
  }

  /**
   * Gets the attack damage for the given hand, acting as though it was used in the main hand
   *
   * If your goal is damage for display, you are better off checking the tool attack damage stat directly, then displaying relevant attribute modifiers in the tooltip
   * @param tool     Held tool
   * @param holder   Entity holding the tool
   * @param slotType Slot with tool
   * @return  Attack damage
   */
  public static float getAttributeAttackDamage(IToolStackView tool, LivingEntity holder, EquipmentSlot slotType) {
    if (slotType == EquipmentSlot.MAINHAND || holder.level().isClientSide) {
      return (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    // first, get a map of existing damage modifiers to exclude
    ItemStack mainStack = holder.getMainHandItem();
    Multimap<Attribute,AttributeModifier> mainModifiers = null;
    if (!mainStack.isEmpty()) {
      mainModifiers = new SingleKeyMultimap<>(Attributes.ATTACK_DAMAGE, holder.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE));
    }

    // next, build a list of damage modifiers from the offhand stack, handled directly as it saves parsing the tool twice and lets us simplify by filtering
    ImmutableList.Builder<AttributeModifier> listBuilder = ImmutableList.builder();
    listBuilder.add(new AttributeModifier(OFFHAND_DAMAGE_MODIFIER_UUID, "tconstruct.tool.offhand_attack_damage", tool.getStats().get(ToolStats.ATTACK_DAMAGE), AttributeModifier.Operation.ADDITION));
    BiConsumer<Attribute, AttributeModifier> attributeConsumer = (attribute, modifier) -> {
      if (attribute == Attributes.ATTACK_DAMAGE) {
        listBuilder.add(modifier);
      }
    };
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.ATTRIBUTES).addAttributes(tool, entry, EquipmentSlot.MAINHAND, attributeConsumer);
    }
    Multimap<Attribute,AttributeModifier> offhandModifiers = new SingleKeyMultimap<>(Attributes.ATTACK_DAMAGE, listBuilder.build());

    // remove the old, add the new
    AttributeMap modifiers = holder.getAttributes();
    if (mainModifiers != null) modifiers.removeAttributeModifiers(mainModifiers);
    modifiers.addTransientAttributeModifiers(offhandModifiers);
    // fetch damage using these temporary modifiers
    float damage = (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
    // revert modifiers to the original state
    modifiers.removeAttributeModifiers(offhandModifiers);
    if (mainModifiers != null) modifiers.addTransientAttributeModifiers(mainModifiers);
    return damage;
  }

  /** Performs a standard attack */
  public static boolean dealDefaultDamage(LivingEntity attacker, Entity target, float damage) {
    if (attacker instanceof Player player) {
      return target.hurt(attacker.damageSources().playerAttack(player), damage);
    }
    return target.hurt(attacker.damageSources().mobAttack(attacker), damage);
  }

  /**
   * General version of attackEntity. Applies cooldowns but has no projectile entity
   */
  public static boolean attackEntity(ItemStack stack, Player attacker, Entity targetEntity) {
    return attackEntity(ToolStack.from(stack), attacker, targetEntity);
  }

  /**
   * General version of attackEntity. Applies cooldowns but has no projectile entity
   */
  public static boolean attackEntity(IToolStackView tool, Player attacker, Entity targetEntity) {
    return attackEntity(tool, attacker, InteractionHand.MAIN_HAND, targetEntity, getCooldownFunction(attacker, InteractionHand.MAIN_HAND), false);
  }

  /** Normal attacking from a tool in the hand */
  public static boolean attackEntity(IToolStackView tool, LivingEntity attackerLiving, InteractionHand hand,
                                     Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack) {
    return attackEntity(tool, attackerLiving, hand, targetEntity, cooldownFunction, isExtraAttack, Util.getSlotType(hand));
  }

  /**
   * Gets a living entity from the given entity, getting the parent if needed
   * @param entity  Entity instance
   * @return  Living entity, or null if its not living
   */
  @Nullable
  public static LivingEntity getLivingEntity(Entity entity) {
    if (entity instanceof PartEntity<?> part) {
      entity = part.getParent();
    }
    return entity instanceof LivingEntity living ? living : null;
  }

  /**
   * Base attack logic, used by normal attacks, projectiles, and extra attacks.
   * Based on {@link Player#attack(Entity)}
   */
  public static boolean attackEntity(IToolStackView tool, LivingEntity attackerLiving, InteractionHand hand,
                                     Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack, EquipmentSlot sourceSlot) {
    // broken? give to vanilla
    if (tool.isBroken() || !tool.hasTag(TinkerTags.Items.MELEE)) {
      return false;
    }
    // nothing to do? cancel
    // TODO: is it a problem that we return true instead of false when isExtraAttack and the final damage is 0 or we fail to hit? I don't think anywhere clientside uses that
    Level level = attackerLiving.level();
    if (level.isClientSide || !targetEntity.isAttackable() || targetEntity.skipAttackInteraction(attackerLiving)) {
      return true;
    }

    // fetch relevant entities
    LivingEntity targetLiving = getLivingEntity(targetEntity);
    Player attackerPlayer = null;
    if (attackerLiving instanceof Player player) {
      attackerPlayer = player;
    }

    // players base damage (includes tools damage stat)
    // hack for offhand attributes: remove mainhand temporarily, and apply offhand
    float damage = getAttributeAttackDamage(tool, attackerLiving, sourceSlot);

    // missing: enchantment modifiers, we handle ourselves

    // determine cooldown
    float cooldown = (float)cooldownFunction.getAsDouble();
    boolean fullyCharged = cooldown > 0.9f;

    // calculate if it's a critical hit
    // that is, in the air, not blind, targeting living, and not sprinting
    boolean isCritical = !isExtraAttack && fullyCharged && attackerLiving.fallDistance > 0.0F && !attackerLiving.onGround() && !attackerLiving.onClimbable()
                         && !attackerLiving.isInWater() && !attackerLiving.hasEffect(MobEffects.BLINDNESS)
                         && !attackerLiving.isPassenger() && targetLiving != null && !attackerLiving.isSprinting();

    // shared context for all modifier hooks
    ToolAttackContext context = new ToolAttackContext(attackerLiving, attackerPlayer, hand, sourceSlot, targetEntity, targetLiving, isCritical, cooldown, isExtraAttack);

    // calculate actual damage
    // boost damage from traits
    float baseDamage = damage;
    List<ModifierEntry> modifiers = tool.getModifierList();
    for (ModifierEntry entry : modifiers) {
      damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, baseDamage, damage);
    }

    // no damage? do nothing
    if (damage <= 0) {
      return !isExtraAttack;
    }

    // forge patches in the knockback attribute for use on players
    // vanilla halves the knockback attribute later, we half it in all our hooks, so halving the attribute makes it equivelent
    float knockback = (float)attackerLiving.getAttributeValue(Attributes.ATTACK_KNOCKBACK) / 2f;
    // vanilla applies 0.4 knockback to living via the attack hook
    if (targetLiving != null) {
      knockback += 0.4f;
    }
    // if sprinting, deal bonus knockback
    SoundEvent sound;
    if (attackerLiving.isSprinting() && fullyCharged) {
      sound = SoundEvents.PLAYER_ATTACK_KNOCKBACK;
      knockback += 0.5f;
    } else if (fullyCharged) {
      sound = SoundEvents.PLAYER_ATTACK_STRONG;
    } else {
      sound = SoundEvents.PLAYER_ATTACK_WEAK;
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
    // Vec3 originalTargetMotion = targetEntity.getDeltaMovement();
    float oldHealth = 0.0F;
    if (targetLiving != null) {
      oldHealth = targetLiving.getHealth();
    }

    // removed: vanilla knockback enchant support
    // changed: knockback halved for simplicity

    // apply modifier knockback and special effects
    float baseKnockback = knockback;
    for (ModifierEntry entry : modifiers) {
      knockback = entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, entry, context, damage, baseKnockback, knockback);
    }

    // set hand for proper looting context
    ModifierLootingHandler.setLootingSlot(attackerLiving, sourceSlot);

    // prevent knockback if needed
    Optional<AttributeInstance> knockbackModifier = getKnockbackAttribute(targetLiving);
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
      didHit = MeleeHitToolHook.dealDamage(tool, context, damage);
    }

    // reset hand to make sure we don't mess with vanilla tools
    ModifierLootingHandler.setLootingSlot(attackerLiving, EquipmentSlot.MAINHAND);

    // reset knockback if needed
    if (canceledKnockback) {
      knockbackModifier.ifPresent(ToolAttackUtil::enableKnockback);
    }

    // if we failed to hit, fire failure hooks
    if (!didHit) {
      if (!isExtraAttack) {
        level.playSound(null, attackerLiving.getX(), attackerLiving.getY(), attackerLiving.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, attackerLiving.getSoundSource(), 1.0F, 1.0F);
      }
      // alert modifiers nothing was hit, mainly used for fiery
      for (ModifierEntry entry : modifiers) {
        entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, entry, context, damage);
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
        targetLiving.knockback(knockback, Mth.sin(attackerLiving.getYRot() * DEGREE_TO_RADIANS), -Mth.cos(attackerLiving.getYRot() * DEGREE_TO_RADIANS));
      } else {
        targetEntity.push(-Mth.sin(attackerLiving.getYRot() * DEGREE_TO_RADIANS) * knockback, 0.1d, Mth.cos(attackerLiving.getYRot() * DEGREE_TO_RADIANS) * knockback);
      }
      attackerLiving.setDeltaMovement(attackerLiving.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
      attackerLiving.setSprinting(false);
    }

    // removed: sword sweep attack, handled above

    // apply velocity change to players if needed
    if (targetEntity.hurtMarked && targetEntity instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(targetEntity));
      targetEntity.hurtMarked = false;
      // TODO: why was this needed before? targetEntity.setDeltaMovement(originalTargetMotion);
    }

    // play sound effects and particles
    if (attackerPlayer != null) {
      // particles
      if (isCritical) {
        sound = SoundEvents.PLAYER_ATTACK_CRIT;
        attackerPlayer.crit(targetEntity);
      }
      if (isMagic) {
        attackerPlayer.magicCrit(targetEntity);
      }
      // sounds
      level.playSound(null, attackerLiving.getX(), attackerLiving.getY(), attackerLiving.getZ(), sound, attackerLiving.getSoundSource(), 1.0F, 1.0F);
    }
    if (damageDealt > 2.0F && level instanceof ServerLevel server) {
      int particleCount = (int)(damageDealt * 0.5f);
      server.sendParticles(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getX(), targetEntity.getY(0.5), targetEntity.getZ(), particleCount, 0.1, 0, 0.1, 0.2);
    }

    // deal attacker thorns damage
    attackerLiving.setLastHurtMob(targetEntity);
    if (targetLiving != null) {
      EnchantmentHelper.doPostHurtEffects(targetLiving, attackerLiving);
    }

    // apply modifier effects
    // removed: bane of arthropods hook, replaced by this
    for (ModifierEntry entry : modifiers) {
      entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, damageDealt);
    }

    // hurt resistance adjustment for high speed weapons
    float speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
    int time = Math.round(20f / speed);
    if (time < targetEntity.invulnerableTime) {
      targetEntity.invulnerableTime = (targetEntity.invulnerableTime + time) / 2;
    }

    // final attack hooks
    if (attackerPlayer != null) {
      if (targetLiving != null) {
        if (!level.isClientSide && !isExtraAttack) {
          ItemStack held = attackerLiving.getItemBySlot(sourceSlot);
          if (!held.isEmpty()) {
            held.hurtEnemy(targetLiving, attackerPlayer);
          }
        }
        attackerPlayer.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
      }
      // removed: fire damage, handled in modifier hook above
      attackerPlayer.causeFoodExhaustion(0.1F);

      // add usage stat
      if (!isExtraAttack) {
        attackerPlayer.awardStat(Stats.ITEM_USED.get(tool.getItem()));
      }
    }

    // damage the tool
    if (!tool.hasTag(TinkerTags.Items.UNARMED)) {
      int durabilityLost = targetLiving != null ? 1 : 0;
      if (!tool.hasTag(TinkerTags.Items.MELEE_PRIMARY)) {
        durabilityLost *= 2;
      }
      ToolDamageUtil.damageAnimated(tool, durabilityLost, attackerLiving);
    }

    return true;
  }

  /**
   * Applies a secondary attack to an entity, notably not running AOE attacks from the tool logic
   * @param tool            Tool instance
   * @param attackerLiving  Attacker
   * @param targetEntity    Target
   * @return  True if hit
   */
  public static boolean extraEntityAttack(IToolStackView tool, LivingEntity attackerLiving, InteractionHand hand, Entity targetEntity) {
    return attackEntity(tool, attackerLiving, hand, targetEntity, NO_COOLDOWN, true);
  }

  /**
   * Spawns a given particle at the given entity's position with an offset
   *
   * @param particleData the selected particle
   * @param entity the entity
   * @param height the height offset for the particle position
   */
  public static void spawnAttackParticle(ParticleOptions particleData, Entity entity, double height) {
    if (entity.level() instanceof ServerLevel server) {
      double xd = -Mth.sin(entity.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(entity.getXRot() / 180.0F * (float) Math.PI);
      double zd =  Mth.cos(entity.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(entity.getXRot() / 180.0F * (float) Math.PI);
      double yd = -Mth.sin(entity.getXRot() / 180.0F * (float) Math.PI);

      server.sendParticles(particleData, entity.getX() + xd, entity.getY() + entity.getBbHeight() * height, entity.getZ() + zd, 0, xd, yd, zd, 0.0D);
    }
  }

  /** Gets the knockback attribute instance if the modifier is not already present */
  private static Optional<AttributeInstance> getKnockbackAttribute(@Nullable LivingEntity living) {
    return Optional.ofNullable(living)
                   .map(e -> e.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                   .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MODIFIER));
  }

  /** Enable the anti-knockback modifier */
  private static void disableKnockback(AttributeInstance instance) {
    instance.addTransientModifier(ANTI_KNOCKBACK_MODIFIER);
  }

  /** Disables the anti knockback modifier */
  private static void enableKnockback(AttributeInstance instance) {
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
  @SuppressWarnings("UnusedReturnValue")
  public static boolean attackEntitySecondary(DamageSource source, float damage, Entity target, @Nullable LivingEntity living, boolean noKnockback) {
    Optional<AttributeInstance> knockbackResistance = getKnockbackAttribute(living);
    // store last damage before secondary attack
    float oldLastDamage = living == null ? 0 : living.lastHurt;

    // prevent knockback in secondary attacks, if requested
    if (noKnockback) {
      knockbackResistance.ifPresent(ToolAttackUtil::disableKnockback);
    }

    // set hurt resistance time to 0 because we always want to deal damage in traits
    int lastInvulnerableTime = target.invulnerableTime;
    target.invulnerableTime = 0;
    boolean hit = target.hurt(source, damage);
    target.invulnerableTime = lastInvulnerableTime; // reset to the old time so bows work right
    // set total received damage, important for AI and stuff
    if (living != null) {
      living.lastHurt += oldLastDamage;
    }

    // remove no knockback marker
    if (noKnockback) {
      knockbackResistance.ifPresent(ToolAttackUtil::enableKnockback);
    }

    return hit;
  }
}
