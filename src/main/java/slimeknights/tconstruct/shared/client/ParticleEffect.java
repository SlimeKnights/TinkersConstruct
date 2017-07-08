package slimeknights.tconstruct.shared.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.Util;

@SideOnly(Side.CLIENT)
public class ParticleEffect extends ParticleCrit {

  public static final ResourceLocation TEXTURE = Util.getResource("textures/particle/particles.png");
  public static final ResourceLocation VANILLA_PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");

  protected TextureManager textureManager;
  protected final Type type;

  private int layer = 0;

  public ParticleEffect(int typeId, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 1f);

    if(typeId < 0 || typeId > Type.values().length) {
      typeId = 0;
    }

    this.type = Type.values()[typeId];

    this.particleMaxAge = 20;
    this.particleTextureIndexX = type.x / 8;
    this.particleTextureIndexY = type.y / 8;

    this.motionY += 0.1f;
    this.motionX += -0.25f + rand.nextFloat() * 0.5f;
    this.motionZ += -0.25f + rand.nextFloat() * 0.5f;

    particleRed = particleBlue = particleGreen = 1f;

    this.textureManager = Minecraft.getMinecraft().getTextureManager();

    // has to be set after constructor because parent class accesses layer-0-only functions
    this.layer = 3;
  }

  protected ResourceLocation getTexture() {
    return TEXTURE;
  }

  @Override
  public void onUpdate() {
    float r = this.particleRed;
    float g = this.particleGreen;
    float b = this.particleBlue;
    super.onUpdate();

    this.particleRed = r * 0.975f;
    this.particleGreen = g * 0.975f;
    this.particleBlue = b * 0.975f;
  }

  @Override
  public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    textureManager.bindTexture(getTexture());
    super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    Tessellator.getInstance().draw();
  }

  @Override
  public int getFXLayer() {
    // layer 3 seems to be a "binds its own texture" layer
    return layer;
  }

  public enum Type {
    HEART_FIRE(0, 0),
    HEART_CACTUS(8, 0),
    HEART_ELECTRO(16, 0),
    HEART_BLOOD(24, 0),
    HEART_ARMOR(32, 0);

    int x, y;

    Type(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
