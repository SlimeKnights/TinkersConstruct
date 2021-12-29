package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AttackParticle extends TextureSheetParticle {

  private final SpriteSet spriteList;

  public AttackParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
    SpriteSet spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed);
    this.spriteList = spriteList;

    this.lifetime = 4;
    this.quadSize = 1.0F;

    this.setSpriteFromAge(spriteList);
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_LIT;
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
