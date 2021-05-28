package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.TinkerWorld;

public class EnderSlimeEntity extends SlimeEntity {
  public EnderSlimeEntity(EntityType<? extends EnderSlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  @Override
  protected IParticleData getSquishParticle() {
    return TinkerWorld.enderSlimeParticle.get();
  }

  /** Randomly teleports an entity, mostly copied from chorus fruit */
  private static void teleport(LivingEntity living) {
    double posX = living.getPosX();
    double posY = living.getPosY();
    double posZ = living.getPosZ();

    for(int i = 0; i < 16; ++i) {
      double x = posX + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
      double y = MathHelper.clamp(posY + (double)(living.getRNG().nextInt(16) - 8), 0.0D, living.getEntityWorld().func_234938_ad_() - 1);
      double z = posZ + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
      if (living.isPassenger()) {
        living.stopRiding();
      }

      if (living.attemptTeleport(x, y, z, true)) {
        SoundEvent soundevent = SoundEvents.ENTITY_ENDERMAN_TELEPORT; // TODO: unique sound
        living.getEntityWorld().playSound(null, posX, posY, posZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
        living.playSound(soundevent, 1.0F, 1.0F);
        break;
      }
    }
  }

  @Override
  public void applyEnchantments(LivingEntity slime, Entity target) {
    super.applyEnchantments(slime, target);
    if (target instanceof LivingEntity) {
      teleport((LivingEntity) target);
    }
  }

  @Override
  protected void damageEntity(DamageSource damageSrc, float damageAmount) {
    float oldHealth = getHealth();
    super.damageEntity(damageSrc, damageAmount);
    if (isAlive() && getHealth() < oldHealth) {
      teleport(this);
    }
  }
}
