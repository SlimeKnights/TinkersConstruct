package slimeknights.tconstruct.library.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
public abstract class AttackParticle extends SpriteBillboardParticle {

  private final SpriteProvider spriteList;

  public AttackParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
    SpriteProvider spriteList) {
    super(world, x, y, z, xSpeed, ySpeed, zSpeed);
    this.spriteList = spriteList;

    this.maxAge = 4;
    this.scale = 1.0F;

    this.setSpriteForAge(spriteList);
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_LIT;
  }

  @Override
  public int getColorMultiplier(float partialTicks) {
    return 61680;
  }

  public void tick() {
    this.prevPosX = this.x;
    this.prevPosY = this.y;
    this.prevPosZ = this.z;

    if (this.age++ >= this.maxAge) {
      this.markDead();
    }
    else {
      this.setSpriteForAge(this.spriteList);
    }
  }
}
