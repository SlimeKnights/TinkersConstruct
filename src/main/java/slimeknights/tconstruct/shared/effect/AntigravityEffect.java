package slimeknights.tconstruct.shared.effect;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.ToString;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.function.IntFunction;

/** Logic for handling the antigravity effect */
public class AntigravityEffect extends TinkerEffect {
  /** Map of previous tick velocities for affected entities. Working under the assumption there are not many anti-gravity entities */
  private final Int2ObjectMap<LastVelocity> LAST_VELOCITY = new Int2ObjectArrayMap<>();
  /** Cached constructor as we call this every tick */
  private final IntFunction<LastVelocity> CONSTRUCTOR = i -> new LastVelocity();

  public AntigravityEffect() {
    super(MobEffectCategory.HARMFUL, 0xff970d, true);
    this.addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "5bd6b8c8-8de9-4357-a74e-afb2a8f00c20", -2, Operation.MULTIPLY_TOTAL);
    MinecraftForge.EVENT_BUS.addListener(this::onLivingJump);
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return true;
  }

  /** Tracks the last velocity for the entity */
  @ToString
  private static class LastVelocity {
    private double lastTick = 0;
    private double twoTicks = 0;

    /** Updates the last velocity */
    void update(double current) {
      twoTicks = lastTick;
      lastTick = current;
    }
  }

  /** Handles movement while under anti-gravity */
  @Override
  public void applyEffectTick(LivingEntity living, int amplifier) {
    // ensure we are actually under the effects of antigrav, might have a double negative
    if (living.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) < 0) {
      Level level = living.level();
      if (!living.level().isClientSide) {
        // 6100 meters is when it starts becoming hard to breathe, assuming world height is 320
        // really just need some arbitrarily big number to start damaging entities so you don't get entities falling up forever
        if (living.getY() > level.getMaxBuildHeight() + 5780) {
          living.hurt(level.damageSources().fellOutOfWorld(), 4.0F);
        }
      }

      LastVelocity lastVelocity = LAST_VELOCITY.computeIfAbsent(living.getId(), CONSTRUCTOR);
      if (living.verticalCollision && !living.verticalCollisionBelow) {
        if (lastVelocity.twoTicks > 0.1f) {
          Vec3 velocity = living.getDeltaMovement();
          living.setDeltaMovement(velocity.x, lastVelocity.twoTicks, velocity.z);
          BlockPos above = BlockPos.containing(living.getX(), living.getBoundingBox().maxY + 0.1, living.getZ());
          BlockState hit = level.getBlockState(above);
          float height = (float)(lastVelocity.twoTicks * 10 - 3 - living.getAttributeValue(TinkerAttributes.SAFE_FALL_DISTANCE.get()) - TinkerEffect.getLevel(living, MobEffects.JUMP));
          if (height > 0.0F) {
            hit.getBlock().fallOn(level, hit, above, living, height);
          }
          hit.getBlock().updateEntityAfterFallOn(level, living);
        }
        lastVelocity.update(0);
      } else {
        lastVelocity.update(living.getDeltaMovement().y);
      }

      // handle falling up ladders
      Vec3 velocity = living.getDeltaMovement();
      double y = velocity.y;
      if (living.onClimbable()) {
        // moving forwards or jumping means climb
        if (living.horizontalCollision || living.jumping) {
          y = -0.2;
        }
        // shift means stop moving
        else if (y > 0 && living.isSuppressingSlidingDownLadder() && living instanceof Player) {
          y = 0;
          // otherwise slow descent
        } else if (y > 0.15f) {
          y = 0.15f;
        }
      }
      // handle friction
      float friction = 1f;
      if (living.verticalCollision && !living.verticalCollisionBelow && !living.shouldDiscardFriction()) {
        BlockPos above = BlockPos.containing(living.getX(), living.getBoundingBox().maxY + 0.1, living.getZ());
        friction = level.getBlockState(above).getFriction(level, above, living);
      }
      // update speed based on ladders and friction
      living.setDeltaMovement(velocity.x * friction, y, velocity.z * friction);
    }
  }

  @Override
  public void removeAttributeModifiers(LivingEntity living, AttributeMap attributeMap, int amplifier) {
    super.removeAttributeModifiers(living, attributeMap, amplifier);
    LAST_VELOCITY.remove(living.getId());
  }

  /** Handles making the player jump down instead of up */
  private void onLivingJump(LivingJumpEvent event) {
    // handles jumping down instead of up
    LivingEntity entity = event.getEntity();
    if (entity.hasEffect(this) && entity.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) < 0) {
      Vec3 movement = entity.getDeltaMovement();
      entity.setDeltaMovement(movement.x, -movement.y, movement.z);
    }
  }

  /** Called by controls to jump when the player is affected by negative gravity */
  public boolean antigravityJump(Player player) {
    // must be on the ground, not swimming, not on a ladder, and have antigravity to jump
    // jump reversal is handled in ModifierEvents to ensure ordering between that and the attribute boost
    if (player.verticalCollision && !player.verticalCollisionBelow && !player.isInWaterOrBubble()
      && player.hasEffect(this) && player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) < 0 && !player.onClimbable()) {
      player.jumpFromGround();
      return true;
    }
    return false;
  }
}
