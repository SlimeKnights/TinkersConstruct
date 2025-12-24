package slimeknights.tconstruct.library.tools.context;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/** Object for common context for weapon attack hooks */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolAttackContext {
  /* Attacker */
  /** Entity doing the attacking */
  @Nonnull
  private final LivingEntity attacker;
  /** Player doing the attacking, null if not a player */
  @Nullable
  private final Player playerAttacker;
  /** Hand containing the tool */
  @Nonnull
  private final InteractionHand hand;
  @Nonnull
  private final EquipmentSlot slotType;
  /** The projectile causing this damage. See {@link slimeknights.tconstruct.tools.entity.ThrownTool} */
  @Nullable
  private final Projectile projectile;

  /* Target */
  /** Originally targeted entity, may be different from {@link #getLivingTarget()} for multipart entities */
  @Nonnull
  private final Entity target;
  /** Target entity */
  @Nullable
  private final LivingEntity livingTarget;

  /* Stats */
  /** Damage before any modifiers are applied */
  private final float baseDamage;
  /** Knockback before any modifiers are applied */
  private final float baseKnockback;
  /** Current attack cooldown */
  private final float cooldown;
  /** Damage multiplier due to a critical hit. 1 if no critical hit */
  private final float criticalModifier;

  /** If true, this is an AOE attack, such as for scythes. */
  private final boolean isExtraAttack;
  /** Sound to play for this attack */
  private final SoundEvent sound;

  /** @deprecated use {@link Builder */
  @Deprecated(forRemoval = true)
  public ToolAttackContext(LivingEntity attacker, @Nullable Player playerAttacker, InteractionHand hand, EquipmentSlot slotType, Entity target, @Nullable LivingEntity livingTarget, boolean isCritical, float cooldown, boolean isExtraAttack) {
    this(attacker, playerAttacker, hand, slotType, null, target, livingTarget,
      (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE),
      (float) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + (livingTarget != null ? 0.4f : 0) + (cooldown > 0.9f && attacker.isSprinting() ? 0.5f : 0),
      cooldown, isCritical ? 1.5f : 1.0f, isExtraAttack,
      cooldown > 0.9f ? attacker.isSprinting() ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG : SoundEvents.PLAYER_ATTACK_WEAK);
  }

  /** @deprecated use {@link Builder */
  @Deprecated(forRemoval = true)
  public ToolAttackContext(LivingEntity attacker, @Nullable Player playerAttacker, InteractionHand hand, Entity target, @Nullable LivingEntity livingTarget, boolean isCritical, float cooldown, boolean isExtraAttack) {
    this(attacker, playerAttacker, hand, Util.getSlotType(hand), target, livingTarget, isCritical, cooldown, isExtraAttack);
  }

  /** Returns true if this attack is fully charged */
  public boolean isFullyCharged() {
    return cooldown > 0.9f;
  }

  /** Checks if this attack is a critical hit */
  public boolean isCritical() {
    return criticalModifier > 1;
  }

  /** Returns true if this context was created by a projectile */
  public boolean isProjectile() {
    return projectile != null;
  }

  /** Gets the level for this context */
  public Level getLevel() {
    return attacker.level();
  }

  /** Creates a damage source from the given context */
  public DamageSource makeDamageSource() {
    if (projectile != null) {
      return CombatHelper.damageSource(TinkerDamageTypes.THROWN_TOOL, projectile, attacker);
    }
    if (playerAttacker != null) {
      return attacker.damageSources().playerAttack(playerAttacker);
    }
    return attacker.damageSources().mobAttack(attacker);
  }


  /* AOE */

  /** Creates a new context targeting the given entity */
  public ToolAttackContext withAOETarget(Entity target, @Nullable LivingEntity livingTarget) {
    return new ToolAttackContext(attacker, playerAttacker, hand, slotType, projectile, target, livingTarget, baseDamage, baseKnockback, cooldown, 1.0f, true, sound);
  }

  /** Creates a new context targeting the given entity */
  public ToolAttackContext withAOETarget(Entity target) {
    return withAOETarget(target, ToolAttackUtil.getLivingEntity(target));
  }

  /** Creates a new context targeting the given entity */
  public ToolAttackContext withAOETarget(LivingEntity target) {
    return withAOETarget(target, target);
  }


  /* Builder */

  /** Creates a builder with the given tool and attacker */
  public static Builder attacker(LivingEntity attacker, @Nullable Player playerAttacker) {
    return new Builder(attacker, playerAttacker);
  }

  /** Creates a builder with the given tool and attacker */
  public static Builder attacker(LivingEntity attacker) {
    return attacker(attacker, ModifierUtil.asPlayer(attacker));
  }

  /** Creates a builder with the given tool and attacker */
  public static Builder attacker(Player attacker) {
    return attacker(attacker, attacker);
  }

  /** Builder for creating a tool attack context */
  @Accessors(fluent = true)
  @CanIgnoreReturnValue
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    /** Entity doing the attacking */
    private final LivingEntity attacker;
    /** Player doing the attacking, null if not a player */
    @Nullable
    private final Player playerAttacker;

    /** Hand containing the tool */
    private InteractionHand hand = InteractionHand.MAIN_HAND;
    /** Slot containing the tool */
    private EquipmentSlot slot = EquipmentSlot.MAINHAND;
    /** The projectile causing this damage. See {@link slimeknights.tconstruct.tools.entity.ThrownTool} */
    @Nullable
    @Setter
    private Projectile projectile;

    /** Originally targeted entity, may be different from {@link #getTarget()} for multipart entities */
    private Entity target;
    /** Target entity */
    @Nullable
    private LivingEntity targetLiving;

    /** Damage to be dealt, before modifiers apply. */
    @Setter
    private float baseDamage = 0;
    /** Knockback to be dealt, before modifiers apply. */
    @Setter
    private float baseKnockback = 0;
    /** Current attack cooldown */
    private float cooldown = 1.0f;

    /** If true, this is an AOE attack, such as for scythes */
    private boolean extraAttack = false;
    /** Sound to play for this attack */
    @Nullable
    @Setter
    private SoundEvent sound = null;


    /* Entities */

    /** Sets the target */
    public Builder target(Entity entity, @Nullable LivingEntity living) {
      this.target = entity;
      this.targetLiving = living;
      return this;
    }

    /** Sets the target, automatically checking if its living */
    public Builder target(Entity entity) {
      return target(entity, ToolAttackUtil.getLivingEntity(entity));
    }

    /** Sets the target as a living entity */
    public Builder target(LivingEntity living) {
      return target(living, living);
    }


    /* Slots */

    /** Sets the hand and source slot for the builder */
    public Builder slot(EquipmentSlot slotType, InteractionHand hand) {
      this.slot = slotType;
      this.hand = hand;
      return this;
    }

    /** Sets the hand and source slot for the builder */
    public Builder hand(InteractionHand hand) {
      return slot(Util.getSlotType(hand), hand);
    }


    /* Stats */

    /** Sets the base damage based on tool stats */
    public Builder applyStats(IToolStackView tool) {
      baseDamage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
      return this;
    }

    /** Sets the base damage and knockback based on the player attributes. */
    public Builder applyAttributes() {
      baseDamage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
      baseKnockback = (float) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) / 2;
      return this;
    }

    /** Sets the base damage from the player attributes for the given slot containing the given tool. Should not be used if the tool is in mainhand, call {@link #applyAttributes()} instead. */
    public Builder toolAttributes(IToolStackView tool) {
      baseDamage = ToolAttackUtil.getToolAttribute(tool, attacker, Attributes.ATTACK_DAMAGE, tool.getStats().get(ToolStats.ATTACK_DAMAGE));
      baseKnockback = ToolAttackUtil.getToolAttribute(tool, attacker, Attributes.ATTACK_KNOCKBACK, baseKnockback) / 2;
      return this;
    }

    /** Sets the cooldown */
    public Builder cooldown(float cooldown) {
      if (cooldown > 1 || cooldown < 0) {
        throw new IllegalArgumentException("Cooldown must be between 0 and 1");
      }
      this.cooldown = cooldown;
      return this;
    }

    /** Sets the cooldown for the player using the standard mainhand function. */
    public Builder defaultCooldown() {
      if (playerAttacker != null) {
        this.cooldown = playerAttacker.getAttackStrengthScale(0.5f);
      }
      return this;
    }

    /** Sets the cooldown for the player using the offhand tracker. */
    public Builder offhandCooldown() {
      if (playerAttacker != null) {
        this.cooldown = OffhandCooldownTracker.getCooldown(playerAttacker);
      }
      return this;
    }


    /* Misc */

    /** Sets this to an extra attack, which skips the tool AOE hooks */
    public Builder extraAttack() {
      this.extraAttack = true;
      return this;
    }


    /* Build */

    /** Builds the final instance, returning null if no attack will happen. */
    @CheckReturnValue
    public ToolAttackContext build() {
      Entity target = Objects.requireNonNull(this.target, "Must set target to build a tool attack context");
      boolean fullyCharged = cooldown > 0.9f;
      float criticalModifier = 1.0f;
      if (!extraAttack && projectile == null) {
        criticalModifier = ToolAttackUtil.getCriticalModifier(attacker, playerAttacker, target, targetLiving, fullyCharged);
      }

      // knockback
      float baseKnockback = this.baseKnockback;
      // vanilla applies 0.4 knockback to living via the attack hook
      if (targetLiving != null) {
        baseKnockback += 0.4f;
      }
      // if sprinting, deal bonus knockback
      if (fullyCharged && attacker.isSprinting()) {
        baseKnockback += 0.5f;
      }

      // sound
      SoundEvent sound = this.sound;
      if (sound == null) {
        if (criticalModifier > 1) {
          sound = SoundEvents.PLAYER_ATTACK_CRIT;
        } else if (fullyCharged) {
          if (attacker.isSprinting()) {
            sound = SoundEvents.PLAYER_ATTACK_KNOCKBACK;
          } else {
            sound = SoundEvents.PLAYER_ATTACK_STRONG;
          }
        } else {
          sound = SoundEvents.PLAYER_ATTACK_WEAK;
        }
      }
      // build final context
      return new ToolAttackContext(attacker, playerAttacker, hand, slot, projectile, target, targetLiving, baseDamage, baseKnockback, cooldown, criticalModifier, extraAttack, sound);
    }
  }
}
