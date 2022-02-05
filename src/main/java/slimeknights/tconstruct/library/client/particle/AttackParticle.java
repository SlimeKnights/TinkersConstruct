package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public abstract class AttackParticle extends TextureSheetParticle {

  private final SpriteSet spriteList;

  public AttackParticle(ClientLevel world, double x, double y, double z, double pQuadSizeMultiplier, SpriteSet spriteList) {
    super(world, x, y, z, 0, 0, 0);
    this.spriteList = spriteList;
    float f = this.random.nextFloat() * 0.6F + 0.4F;
    this.rCol = f;
    this.gCol = f;
    this.bCol = f;
    this.lifetime = 4;
    this.quadSize = 1.0F - (float)pQuadSizeMultiplier * 0.5F;
    this.setSpriteFromAge(spriteList);
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_LIT;
  }

  @Override
  public int getLightColor(float partialTicks) {
    return 0xF000F0;
  }

  @Override
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
