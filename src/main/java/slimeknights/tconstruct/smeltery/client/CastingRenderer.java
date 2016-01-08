package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.texture.AbstractColoredTexture;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class CastingRenderer<T extends TileCasting> extends TileEntitySpecialRenderer<T> {

  protected final float yMin;
  protected final float yMax;
  protected final float xzMin;
  protected final float xzMax;

  protected float scale;
  protected float yOffset;
  protected float xzOffset;


  public CastingRenderer(float yMin, float yMax, float xzMin, float xzMax) {
    this.yMin = yMin;
    this.yMax = yMax;
    this.xzMin = xzMin;
    this.xzMax = xzMax;


    this.yOffset = yMin + (yMax-yMin)/2f;
    this.xzOffset = xzMin + (xzMax - xzMin)/2f;

    this.scale = (xzMax - xzMin);
  }

  @Override
  public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(te.tank.getFluidAmount() == 0) {
      return;
    }

    float height = ((float)te.tank.getFluidAmount() - te.renderOffset) / (float)te.tank.getCapacity();

    if(te.renderOffset > 1.2f || te.renderOffset < -1.2f) {
      te.renderOffset -= (te.renderOffset / 12f + 0.1f) * partialTicks;
    }
    else {
      te.renderOffset = 0;
    }

    float yh = yMin + (yMax - yMin) * height;

    //GlStateManager.color(0.1f, 0.1f, 0.1f);
    //RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yh, xzMax);
    Minecraft mc = Minecraft.getMinecraft();
    FluidStack fluid = te.tank.getFluid();

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer renderer = tessellator.getWorldRenderer();
    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    int color = fluid.getFluid().getColor(fluid);
    //RenderUtil.setColorRGBA(color);
    int brightness = mc.theWorld.getCombinedLight(te.getPos(), fluid.getFluid().getLuminosity());

    RenderUtil.pre(x, y, z);

    float x1 = xzMin;
    float z1 = x1;
    float x2 = xzMax;
    float z2 = x2;
    float y1 = yMin;
    float y2 = yh;

    float progress = 0.01f;
    if(te.renderOffset == 0) {
      progress = te.getCooldownProgress();
    }

    int r,g,b,a;
    a = AbstractColoredTexture.alpha(color);
    r = AbstractColoredTexture.red(color);
    g = AbstractColoredTexture.green(color);
    b = AbstractColoredTexture.blue(color);

    //a = (int)(((a/255f) * (1f - progress/2f)) * a);
    r = (int)((float)r * (1f - 0.8*progress));
    g = (int)((float)g * (1f - 0.8*progress));
    b = (int)((float)b * (1f - 0.8*progress));

    color = AbstractColoredTexture.compose(r,g,b,a);

    // make it a tad smaller
    GlStateManager.scale(0.999, 0.999, 0.999);
    TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    // x/y/z2 - x/y/z1 is because we need the width/height/depth
    RenderUtil.putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness, false);
    RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
    RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
    RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, true);
    RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
    RenderUtil.putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP,    color, brightness, false);

    tessellator.draw();

    // render item
    ItemStack stack = te.getCurrentResult();
    if(progress > 0 && stack != null) {
      GlStateManager.pushMatrix();
      brightness = te.getWorld().getCombinedLight(te.getPos(), 0);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness % 0x10000) / 1f,
                                            (float)(brightness / 0x10000) / 1f);


      //GlStateManager.translate(0.5f, 0.5f, 0.5f);
      GlStateManager.translate(xzOffset, yOffset, xzOffset);
      GlStateManager.scale(scale, scale, scale);
      GlStateManager.scale(1.01f, 1.01f, 1.01f); // make it a tad bigger so it renders over the liquid (will be blended)
      GlStateManager.scale(2f, 2f, 2f); // renderItem scales by 0.5

      // align item orientation with casting tile orientation
      GlStateManager.rotate(-90 * te.getFacing().getHorizontalIndex(), 0, 1, 0);
      GlStateManager.rotate(-90, 1, 0, 0);

      GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_SRC_ALPHA);
      GL14.glBlendColor(1f, 1f, 1f, progress/2f);
      //GL14.glBlendColor(1f, 1f, 1f, 1f); // debug

      IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);

      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.popMatrix();
    }

    RenderUtil.post();
  }

  public static class Table extends CastingRenderer<TileCastingTable> {

    public Table() {
      super(15/16f, 1f, 1/16f, 15/16f);
      this.scale = 1f;
    }
  }

  public static class Basin extends CastingRenderer<TileCastingBasin> {

    public Basin() {
      super(4/16f, 1f, 2/16f, 14/16f);
    }
  }
}
