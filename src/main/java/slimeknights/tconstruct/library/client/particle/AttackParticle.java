package slimeknights.tconstruct.library.client.particle;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AttackParticle extends Particle {

  public static final VertexFormat VERTEX_FORMAT = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(DefaultVertexFormats.POSITION_3F).add(DefaultVertexFormats.TEX_2F).add(DefaultVertexFormats.COLOR_4UB).add(DefaultVertexFormats.TEX_2S).add(DefaultVertexFormats.NORMAL_3B).add(DefaultVertexFormats.PADDING_1B).build());

  protected TextureManager textureManager;
  protected int life;

  protected int lifeTime;
  protected float size;
  protected double height;

  protected int animPhases;
  protected int animPerRow;

  public AttackParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureManager textureManager) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    this.textureManager = textureManager;

    this.life = 0;
    this.init();
  }

  protected void init() {
    this.lifeTime = 4;
    this.size = 1f;
    this.height = 1f;

    this.animPerRow = 4;
    this.animPhases = 8;
  }

  protected abstract ResourceLocation getTexture();

  protected VertexFormat getVertexFormat() {
    return VERTEX_FORMAT;
  }

  @Override
  public void renderParticle(IVertexBuilder vertexBuilder, ActiveRenderInfo renderInfo, float partialTicks) {
    // TODO: FIX PARTICLE
    /*Vec3d projectedView = renderInfo.getProjectedView();

    float progress = (this.life + partialTicks) / this.lifeTime;
    int i = (int) (progress * this.animPhases);
    int rows = MathHelper.ceil((float) this.animPhases / (float) this.animPerRow);

    if (i < this.animPhases) {
      this.textureManager.bindTexture(this.getTexture());
      float f = (float) (i % this.animPerRow) / (float) this.animPerRow;
      float f1 = f + 1f / this.animPerRow - 0.005f;
      float f2 = (float) (i / this.animPerRow) / (float) rows;
      float f3 = f2 + 1f / rows - 0.005f;
      float f4 = 0.5F * this.size;
      float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - projectedView.getX());
      float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - projectedView.getY());
      float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - projectedView.getZ());

      // mirror the attack for left handed
      if (Minecraft.getInstance().gameSettings.mainHand == HandSide.LEFT) {
        // we just swap the x UVs to mirror it
        float t = f;
        f = f1;
        f1 = t;
      }

      Quaternion quaternion = renderInfo.func_227995_f_();


      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableLighting();
      RenderHelper.disableStandardItemLighting();
      vertexBuilder.pos(f5 - rotationX * f4 - rotationXY * f4, (f6 - rotationZ * f4 * this.height), f7 - rotationYZ * f4 - rotationXZ * f4).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).func_227887_a_(0.0F, 1.0F, 0.0F).endVertex();
      vertexBuilder.pos(f5 - rotationX * f4 + rotationXY * f4, (f6 + rotationZ * f4 * this.height), f7 - rotationYZ * f4 + rotationXZ * f4).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).func_227887_a_(0.0F, 1.0F, 0.0F).endVertex();
      vertexBuilder.pos(f5 + rotationX * f4 + rotationXY * f4, (f6 + rotationZ * f4 * this.height), f7 + rotationYZ * f4 + rotationXZ * f4).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).func_227887_a_(0.0F, 1.0F, 0.0F).endVertex();
      vertexBuilder.pos(f5 + rotationX * f4 - rotationXY * f4, (f6 - rotationZ * f4 * this.height), f7 + rotationYZ * f4 - rotationXZ * f4).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).func_227887_a_(0.0F, 1.0F, 0.0F).endVertex();
      Tessellator.getInstance().draw();
      RenderSystem.enableLighting();
    }*/
  }

  @Override
  public int getBrightnessForRender(float p_189214_1_) {
    return 61680;
  }

  @Override
  public void tick() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    ++this.life;

    if (this.life == this.lifeTime) {
      this.setExpired();
    }
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.CUSTOM;
  }
}
