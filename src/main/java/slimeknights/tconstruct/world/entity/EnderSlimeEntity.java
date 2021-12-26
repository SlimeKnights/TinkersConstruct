package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.events.teleport.EnderSlimeTeleportEvent;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import slimeknights.tconstruct.world.TinkerWorld;

public class EnderSlimeEntity extends SlimeEntity {
  /** Predicate for this ender slime to allow teleporting */
  private final ITeleportEventFactory teleportPredicate = (entity, x, y, z) -> new EnderSlimeTeleportEvent(entity, x, y, z, this);

  public EnderSlimeEntity(EntityType<? extends EnderSlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  @Override
  protected IParticleData getParticleType() {
    return TinkerWorld.enderSlimeParticle.get();
  }

  @Override
  public void doEnchantDamageEffects(LivingEntity slime, Entity target) {
    super.doEnchantDamageEffects(slime, target);
    if (target instanceof LivingEntity) {
      TeleportHelper.randomNearbyTeleport((LivingEntity) target, teleportPredicate);
    }
  }

  @Override
  protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
    float oldHealth = getHealth();
    super.actuallyHurt(damageSrc, damageAmount);
    if (isAlive() && getHealth() < oldHealth) {
      TeleportHelper.randomNearbyTeleport(this, teleportPredicate);
    }
  }
}
