package slimeknights.tconstruct.world.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;

public class SkySlimeEntity extends TravelersPlateSlimeEntity {
  private double bounceAmount = 0f;
  public SkySlimeEntity(EntityType<? extends SkySlimeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  protected float getJumpPower() {
    return (float)Math.sqrt(this.getSize()) * this.getBlockJumpFactor() / 2;
  }

  @Override
  protected ParticleOptions getParticleType() {
    return TinkerWorld.skySlimeParticle.get();
  }

  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
    if (isSuppressingBounce()) {
      return super.causeFallDamage(distance, damageMultiplier * 0.2f, source);
    }
    float[] ret = ForgeHooks.onLivingFall(this, distance, damageMultiplier);
    if (ret == null) {
      return false;
    }
    distance = ret[0];
    if (distance > 2) {
      // invert Y motion, boost X and Z slightly
      Vec3 motion = getDeltaMovement();
      setDeltaMovement(motion.x / 0.95f, motion.y * -0.9, motion.z / 0.95f);
      bounceAmount = getDeltaMovement().y;
      fallDistance = 0f;
      hasImpulse = true;
      setOnGround(false);
      playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
    }
    return false;
  }

  @Override
  public void move(MoverType typeIn, Vec3 pos) {
    super.move(typeIn, pos);
    if (bounceAmount > 0) {
      Vec3 motion = getDeltaMovement();
      setDeltaMovement(motion.x, bounceAmount, motion.z);
      bounceAmount = 0;
    }
  }

  @Override
  protected MaterialId getPlating() {
    return MaterialIds.steel;
  }
}
