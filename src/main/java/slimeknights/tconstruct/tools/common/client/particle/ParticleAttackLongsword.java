package slimeknights.tconstruct.tools.common.client.particle;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.client.particle.ParticleAttack;

public class ParticleAttackLongsword extends ParticleAttack {

  private static final ResourceLocation SWEEP_TEXTURE = new ResourceLocation("textures/entity/sweep.png");

  public ParticleAttackLongsword(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureManager textureManager) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, textureManager);
  }

  @Override
  protected void init() {
    super.init();
    this.height = 0.5f;
    this.size = 1.8f;
  }

  @Override
  protected ResourceLocation getTexture() {
    return SWEEP_TEXTURE;
  }
}
