package slimeknights.tconstruct.tools.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.Util;

@SideOnly(Side.CLIENT)
public class CleaverSlashAttackFx extends EntityFX {

  public static final IParticleFactory FACTORY = new Factory();

  public static final ResourceLocation TEXTURE = Util.getResource("textures/particle/slash_cleaver.png");
  public static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
  private int life;
  private int lifeTime;
  private TextureManager textureManager;
  private float size;

  protected int animPhases;
  protected int animPerRow;

  public CleaverSlashAttackFx(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, TextureManager textureManager) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    this.life = 0;
    this.lifeTime = 4;
    this.textureManager = textureManager;
    this.size = 1.0F;
    this.animPerRow = 4;
    this.animPhases = 8;
  }

  @Override
  public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    float progress = ((float)life + partialTicks)/(float)lifeTime;
    //int i = (int) ((((float) this.life + partialTicks) * 3f / (float) this.lifeTime));
    int i = (int)(progress * (float)animPhases);
    int rows = MathHelper.ceiling_float_int((float) animPhases / (float) animPerRow);

    if(i < animPhases) {
      this.textureManager.bindTexture(TEXTURE);
      float f = (float) (i % animPerRow) / (float) animPerRow;
      float f1 = f + 1f/(float)animPerRow - 0.005f;
      float f2 = (float) (i / animPerRow) / (float) rows;
      float f3 = f2 + 1f/(float)rows - 0.005f;
      float f4 = 0.5F * this.size;
      float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
      float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
      float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      RenderHelper.disableStandardItemLighting();
      worldRendererIn.begin(7, VERTEX_FORMAT);
      worldRendererIn.pos((double) (f5 - rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4 * 1.5F), (double) (f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRendererIn.pos((double) (f5 - rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4 * 1.5F), (double) (f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double) f1, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRendererIn.pos((double) (f5 + rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4 * 1.5F), (double) (f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRendererIn.pos((double) (f5 + rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4 * 1.5F), (double) (f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      Tessellator.getInstance().draw();
      GlStateManager.enableLighting();
    }
  }

  @Override
  public int getBrightnessForRender(float p_189214_1_) {
    return 61680;
  }

  @Override
  public void onUpdate() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    ++this.life;

    if(this.life == this.lifeTime) {
      this.setExpired();
    }
  }

  @Override
  public int getFXLayer() {
    return 3;
  }

  @SideOnly(Side.CLIENT)
  private static class Factory implements IParticleFactory {

    @Override
    public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... data) {
      return new CleaverSlashAttackFx(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().getTextureManager());
    }
  }

}
