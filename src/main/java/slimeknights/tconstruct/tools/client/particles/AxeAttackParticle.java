package slimeknights.tconstruct.tools.client.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import slimeknights.tconstruct.library.client.particle.AttackParticle;

public class AxeAttackParticle extends AttackParticle {

  public AxeAttackParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteProvider spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteList);

    this.scale = 0.4F;
  }

  public static class Factory implements ParticleFactory<DefaultParticleType> {

    private final SpriteProvider spriteSet;

    public Factory(SpriteProvider spriteSet) {
      this.spriteSet = spriteSet;
    }

    public Particle makeParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new AxeAttackParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
  }
}
