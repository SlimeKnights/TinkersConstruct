package slimeknights.tconstruct.tools.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import slimeknights.tconstruct.library.client.particle.AttackParticle;

public class AxeAttackParticle extends AttackParticle {

  public AxeAttackParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteList);

    this.particleScale = 0.4F;
  }

  public static class Factory implements IParticleFactory<BasicParticleType> {

    private final IAnimatedSprite spriteSet;

    public Factory(IAnimatedSprite spriteSet) {
      this.spriteSet = spriteSet;
    }

    public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new AxeAttackParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
  }
}
