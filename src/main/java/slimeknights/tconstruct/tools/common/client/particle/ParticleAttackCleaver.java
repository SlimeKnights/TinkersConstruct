package slimeknights.tconstruct.tools.common.client.particle;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.particle.ParticleAttack;

@SideOnly(Side.CLIENT)
public class ParticleAttackCleaver extends ParticleAttack {

  public static final ResourceLocation TEXTURE = Util.getResource("textures/particle/slash_cleaver.png");

  public ParticleAttackCleaver(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureManager textureManager) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, textureManager);
  }

  @Override
  protected void init() {
    super.init();
    this.height = 1.3f;
  }

  @Override
  protected ResourceLocation getTexture() {
    return TEXTURE;
  }
}
