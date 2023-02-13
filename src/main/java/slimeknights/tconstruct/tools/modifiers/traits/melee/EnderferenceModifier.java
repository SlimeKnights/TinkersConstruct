package slimeknights.tconstruct.tools.modifiers.traits.melee;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class EnderferenceModifier extends Modifier implements ProjectileHitModifierHook {
  private static final DamageSource FALLBACK = new DamageSource("arrow");

  public EnderferenceModifier() {
    MinecraftForge.EVENT_BUS.addListener(EnderferenceModifier::onTeleport);
  }

  private static void onTeleport(EntityTeleportEvent event) {
    if (event.getEntity() instanceof LivingEntity living && living.hasEffect(TinkerModifiers.enderferenceEffect.get())) {
      event.setCanceled(true);
    }
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.PROJECTILE_HIT);
  }

  @Override
  public int getPriority() {
    return 50; // run later so other hooks can run before we cancel it all
  }

  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // hack: do not want them teleporting from this hit
      TinkerModifiers.enderferenceEffect.get().apply(entity, 1, 0, true);
    }
    return knockback;
  }

  @Override
  public void failedEntityHit(IToolStackView tool, int level, ToolAttackContext context) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      entity.removeEffect(TinkerModifiers.enderferenceEffect.get());
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // 5 seconds of interference per level, affect all entities as players may teleport too
      entity.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), level * 100, 0, false, true, true));
    }
    return 0;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null) {
      target.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), modifier.getLevel() * 100, 0, false, true, true));

      // endermen are hardcoded to not take arrow damage, so disagree by reimplementing arrow damage right here
      if (target.getType() == EntityType.ENDERMAN && projectile instanceof AbstractArrow arrow) {
        // first, give up if we reached pierce capacity, and ensure list are created
        if (arrow.getPierceLevel() > 0) {
          if (arrow.piercingIgnoreEntityIds == null) {
            arrow.piercingIgnoreEntityIds = new IntOpenHashSet(5);
          }
          if (arrow.piercedAndKilledEntities == null) {
            arrow.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
          }
          if (arrow.piercingIgnoreEntityIds.size() >= arrow.getPierceLevel() + 1) {
            arrow.discard();
            return true;
          }
          arrow.piercingIgnoreEntityIds.add(target.getId());
        }

        // calculate damage, bonus on crit
        int damage = Mth.ceil(Mth.clamp(arrow.getDeltaMovement().length() * arrow.getBaseDamage(), 0.0D, Integer.MAX_VALUE));
        if (arrow.isCritArrow()) {
          damage = (int)Math.min(RANDOM.nextInt(damage / 2 + 2) + (long)damage, Integer.MAX_VALUE);
        }

        // create damage source, don't use projectile sources as that makes endermen ignore it
        Entity owner = arrow.getOwner();
        DamageSource damageSource;
        if (attacker instanceof Player player) {
          damageSource = DamageSource.playerAttack(player);
        } else if (attacker != null) {
          damageSource = DamageSource.mobAttack(attacker);
        } else {
          damageSource = FALLBACK;
        }
        if (attacker != null) {
          attacker.setLastHurtMob(target);
        }

        // handle fire
        int remainingFire = target.getRemainingFireTicks();
        if (arrow.isOnFire()) {
          target.setSecondsOnFire(5);
        }

        if (target.hurt(damageSource, (float)damage)) {
          if (!arrow.level.isClientSide && arrow.getPierceLevel() <= 0) {
            target.setArrowCount(target.getArrowCount() + 1);
          }

          // knockback from punch
          int knockback = arrow.getKnockback();
          if (knockback > 0) {
            Vec3 knockbackVec = arrow.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(knockback * 0.6D);
            if (knockbackVec.lengthSqr() > 0.0D) {
              target.push(knockbackVec.x, 0.1D, knockbackVec.z);
            }
          }

          if (!arrow.level.isClientSide && attacker != null) {
            EnchantmentHelper.doPostHurtEffects(target, attacker);
            EnchantmentHelper.doPostDamageEffects(attacker, target);
          }

          arrow.doPostHurtEffects(target);

          if (!target.isAlive() && arrow.piercedAndKilledEntities != null) {
            arrow.piercedAndKilledEntities.add(target);
          }

          if (!arrow.level.isClientSide && arrow.shotFromCrossbow() && owner instanceof ServerPlayer player) {
            if (arrow.piercedAndKilledEntities != null) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, arrow.piercedAndKilledEntities);
            } else if (!target.isAlive()) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, List.of(target));
            }
          }

          arrow.playSound(arrow.soundEvent, 1.0F, 1.2F / (RANDOM.nextFloat() * 0.2F + 0.9F));
          if (arrow.getPierceLevel() <= 0) {
            arrow.discard();
          }
        } else {
          // reset fire and drop the arrow
          target.setRemainingFireTicks(remainingFire);
          arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-0.1D));
          arrow.setYRot(arrow.getYRot() + 180.0F);
          arrow.yRotO += 180.0F;
          if (!arrow.level.isClientSide && arrow.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
              arrow.spawnAtLocation(arrow.getPickupItem(), 0.1F);
            }

            arrow.discard();
          }
        }

        return true;
      }
    }
    return false;
  }
}
