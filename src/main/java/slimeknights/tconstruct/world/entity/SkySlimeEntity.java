package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.TinkerWorld;

public class SkySlimeEntity extends SlimeEntity {
  public SkySlimeEntity(EntityType<? extends SkySlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return (float)Math.sqrt(this.getSlimeSize()) * this.getJumpFactor() / 2;
  }

  @Override
  protected IParticleData getSquishParticle() {
    return TinkerWorld.skySlimeParticle.get();
  }
}
