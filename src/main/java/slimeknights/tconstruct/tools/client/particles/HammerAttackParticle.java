package slimeknights.tconstruct.tools.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import slimeknights.tconstruct.library.client.particle.AttackParticle;

public class HammerAttackParticle extends AttackParticle {

  public HammerAttackParticle(ClientLevel world, double x, double y, double z, double quadSize, SpriteSet spriteList) {
    super(world, x, y, z, quadSize, spriteList);
  }

  public static class Factory implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet spriteSet;

    public Factory(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new HammerAttackParticle(worldIn, x, y, z, xSpeed, this.spriteSet);
    }
  }
}
