package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AttackParticle extends SpriteTexturedParticle {

  private final IAnimatedSprite spriteList;

  public AttackParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
    IAnimatedSprite spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed);
    this.spriteList = spriteList;

    this.lifetime = 4;
    this.quadSize = 1.0F;

    this.setSpriteFromAge(spriteList);
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.PARTICLE_SHEET_LIT;
  }

  @Override
  public int getLightColor(float partialTicks) {
    return 61680;
  }

  public void tick() {
    this.xo = this.x;
    this.yo = this.y;
    this.zo = this.z;

    if (this.age++ >= this.lifetime) {
      this.remove();
    }
    else {
      this.setSpriteFromAge(this.spriteList);
    }
  }
}
