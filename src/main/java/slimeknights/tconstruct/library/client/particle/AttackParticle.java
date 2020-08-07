package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AttackParticle extends SpriteTexturedParticle {

  private final IAnimatedSprite spriteList;

  public AttackParticle(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed);
    this.spriteList = spriteList;

    this.maxAge = 4;
    this.particleScale = 1.0F;

    this.selectSpriteWithAge(spriteList);
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.PARTICLE_SHEET_LIT;
  }

  @Override
  public int getBrightnessForRender(float partialTicks) {
    return 61680;
  }

  public void tick() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    if (this.age++ >= this.maxAge) {
      this.setExpired();
    }
    else {
      this.selectSpriteWithAge(this.spriteList);
    }
  }
}
