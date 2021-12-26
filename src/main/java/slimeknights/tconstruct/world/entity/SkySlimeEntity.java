package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.world.TinkerWorld;

public class SkySlimeEntity extends SlimeEntity {
  private double bounceAmount = 0f;
  public SkySlimeEntity(EntityType<? extends SkySlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  @Override
  protected float getJumpPower() {
    return (float)Math.sqrt(this.getSize()) * this.getBlockJumpFactor() / 2;
  }

  @Override
  protected IParticleData getParticleType() {
    return TinkerWorld.skySlimeParticle.get();
  }

  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier) {
    float[] ret = ForgeHooks.onLivingFall(this, distance, damageMultiplier);
    if (ret == null) {
      return false;
    }
    distance = ret[0];
    damageMultiplier = ret[1];

    if (distance > 2) {
      if (isSuppressingBounce()) {
        return super.causeFallDamage(distance, damageMultiplier * 0.2f);
      } else {
        // invert Y motion, boost X and Z slightly
        Vector3d motion = getDeltaMovement();
        setDeltaMovement(motion.x / 0.95f, motion.y * -0.9, motion.z / 0.95f);
        bounceAmount = getDeltaMovement().y;
        fallDistance = 0f;
        hasImpulse = true;
        setOnGround(false);
        playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
      }
    }
    return false;
  }

  @Override
  public void move(MoverType typeIn, Vector3d pos) {
    super.move(typeIn, pos);
    if (bounceAmount > 0) {
      Vector3d motion = getDeltaMovement();
      setDeltaMovement(motion.x, bounceAmount, motion.z);
      bounceAmount = 0;
    }
  }
}
