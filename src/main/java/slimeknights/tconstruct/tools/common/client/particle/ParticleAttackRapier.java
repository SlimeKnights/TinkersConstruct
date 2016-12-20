package slimeknights.tconstruct.tools.common.client.particle;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.particle.ParticleAttack;

public class ParticleAttackRapier extends ParticleAttack {

  public static final ResourceLocation TEXTURE = Util.getResource("textures/particle/slash_rapier.png");

  public ParticleAttackRapier(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureManager textureManager) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, textureManager);
  }

  @Override
  protected void init() {
    super.init();

    this.animPhases = 8;
    this.height = 1f;
    this.size = 0.2f;
    this.lifeTime = 2;
  }

  @Override
  protected ResourceLocation getTexture() {
    return TEXTURE;
  }
}
