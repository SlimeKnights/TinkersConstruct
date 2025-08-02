package slimeknights.tconstruct.world.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.events.teleport.EnderSlimeTeleportEvent;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;

public class EnderSlimeEntity extends TravelersPlateSlimeEntity {
  /** Predicate for this ender slime to allow teleporting */
  private final ITeleportEventFactory teleportPredicate = (entity, x, y, z) -> new EnderSlimeTeleportEvent(entity, x, y, z, this);

  public EnderSlimeEntity(EntityType<? extends EnderSlimeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  protected ParticleOptions getParticleType() {
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

  @Override
  protected MaterialId getPlating() {
    return MaterialIds.knightmetal;
  }
}
