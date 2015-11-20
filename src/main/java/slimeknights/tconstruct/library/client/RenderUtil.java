package slimeknights.tconstruct.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

public final class RenderUtil {
  private RenderUtil() {}

  protected static Minecraft mc = Minecraft.getMinecraft();

  /** Renders the given texture tiled into a GUI */
  public static void renderTiledTextureAtlas(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.startDrawingQuads();
    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

    putTiledTextureQuads(worldrenderer, x, y, width, height, depth, sprite);

    tessellator.draw();
  }

  /** Adds a quad to the rendering pipeline. Call startDrawingQuads beforehand. You need to call draw() yourself. */
  public static void putTiledTextureQuads(WorldRenderer renderer, int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    // tile vertically
    do {
      int renderHeight = Math.min(sprite.getIconHeight(), height);
      height -= renderHeight;
      y -= renderHeight;

      float v = sprite.getInterpolatedV((16f * renderHeight)/(float)sprite.getIconHeight());

      // we need to draw the quads per width too
      int x2 = x;
      // tile horizontally
      do {
        int renderWidth = Math.min(sprite.getIconWidth(), width);
        width -= renderWidth;

        float u = sprite.getInterpolatedU((16f * renderWidth)/(float)sprite.getIconWidth());

        renderer.addVertexWithUV(x2, y + renderHeight, depth, sprite.getMinU(), v);
        renderer.addVertexWithUV(x2 + renderWidth, y + renderHeight, depth, u, v);
        renderer.addVertexWithUV(x2 + renderWidth, y, depth, u, sprite.getMinV());
        renderer.addVertexWithUV(x2, y, depth, sprite.getMinU(), sprite.getMinV());

        x2 += renderWidth;
      } while(width > 0);
    } while(height > 0);
  }

  /**
   * Renders a fluid block, call from TESR. x/y/z is the rendering offset.
   * @param fluid Fluid to render
   * @param pos   BlockPos where the Block is rendered. Used for brightness.
   * @param x     Rendering offset. TESR x parameter.
   * @param y     Rendering offset. TESR x parameter.
   * @param z     Rendering offset. TESR x parameter.
   * @param w     Width. 1 = full X-Width
   * @param h     Height. 1 = full Y-Height
   * @param d     Depth. 1 = full Z-Depth
   */
  public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d) {
    double wd = (1d-w)/2d;
    double hd = (1d-h)/2d;
    double dd = (1d-d)/2d;

    renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1d-wd, 1d-hd, 1d-dd);
  }

  /** Renders block with offset x/y/z from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1 */
  public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer renderer = tessellator.getWorldRenderer();
    renderer.startDrawingQuads();
    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    int color = fluid.getFluid().getColor(fluid);
    RenderUtil.setColorRGBA(color);
    renderer.setBrightness(mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity()));

    pre(x, y, z);

    TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

    putTexturedQuad(renderer, still, 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN);
    putTexturedQuad(renderer, flowing, 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH);
    putTexturedQuad(renderer, flowing, 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.EAST);
    putTexturedQuad(renderer, flowing, 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH);
    putTexturedQuad(renderer, flowing, 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.WEST);
    putTexturedQuad(renderer, still  , 0, 0, 0, x2-x1, y2-y1, z2-z1, EnumFacing.UP);

    tessellator.draw();

    post();
  }

  public static void putTexturedCuboid(WorldRenderer renderer, ResourceLocation location, double x1, double y1, double z1, double x2, double y2, double z2) {
    TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(location.toString());
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP);
  }

  public static void putTexturedQuad(WorldRenderer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face) {
    double minU = sprite.getMinU();
    double maxU = sprite.getMaxU();
    double minV = sprite.getMinV();
    double maxV = sprite.getMaxV();

    double x1 = x;
    double x2 = x + w;
    double y1 = y;
    double y2 = y + h;
    double z1 = z;
    double z2 = z + d;

    switch(face) {
      case DOWN:
      case UP:
        minU = sprite.getInterpolatedU(x1*16d);
        maxU = sprite.getInterpolatedU(x2*16d);
        minV = sprite.getInterpolatedV(z1*16d);
        maxV = sprite.getInterpolatedV(z2*16d);
        break;
      case NORTH:
      case SOUTH:
        minU = sprite.getInterpolatedU(x1*16f);
        maxU = sprite.getInterpolatedU(x2*16f);
        minV = sprite.getInterpolatedV(y1*16f);
        maxV = sprite.getInterpolatedV(y2*16f);
        break;
      case WEST:
      case EAST:
        minU = sprite.getInterpolatedU(z1*16d);
        maxU = sprite.getInterpolatedU(z2*16d);
        minV = sprite.getInterpolatedV(y1*16d);
        maxV = sprite.getInterpolatedV(y2*16d);
        break;
    }

    switch(face) {
      case DOWN:
        renderer.addVertexWithUV(x1, y1, z1, minU, minV);
        renderer.addVertexWithUV(x2, y1, z1, maxU, minV);
        renderer.addVertexWithUV(x2, y1, z2, maxU, maxV);
        renderer.addVertexWithUV(x1, y1, z2, minU, maxV);
        break;
      case UP:
        renderer.addVertexWithUV(x1, y2, z1, minU, minV);
        renderer.addVertexWithUV(x1, y2, z2, minU, maxV);
        renderer.addVertexWithUV(x2, y2, z2, maxU, maxV);
        renderer.addVertexWithUV(x2, y2, z1, maxU, minV);
        break;
      case NORTH:
        renderer.addVertexWithUV(x1, y1, z1, minU, maxV);
        renderer.addVertexWithUV(x1, y2, z1, minU, minV);
        renderer.addVertexWithUV(x2, y2, z1, maxU, minV);
        renderer.addVertexWithUV(x2, y1, z1, maxU, maxV);
        break;
      case SOUTH:
        renderer.addVertexWithUV(x1, y1, z2, maxU, maxV);
        renderer.addVertexWithUV(x2, y1, z2, minU, maxV);
        renderer.addVertexWithUV(x2, y2, z2, minU, minV);
        renderer.addVertexWithUV(x1, y2, z2, maxU, minV);
        break;
      case WEST:
        renderer.addVertexWithUV(x1, y1, z1, maxU, maxV);
        renderer.addVertexWithUV(x1, y1, z2, minU, maxV);
        renderer.addVertexWithUV(x1, y2, z2, minU, minV);
        renderer.addVertexWithUV(x1, y2, z1, maxU, minV);
        break;
      case EAST:
        renderer.addVertexWithUV(x2, y1, z1, minU, maxV);
        renderer.addVertexWithUV(x2, y2, z1, minU, minV);
        renderer.addVertexWithUV(x2, y2, z2, maxU, minV);
        renderer.addVertexWithUV(x2, y1, z2, maxU, maxV);
        break;
    }
  }

  protected static void pre(double x, double y, double z) {
    GlStateManager.pushMatrix();

    GlStateManager.disableLighting();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    if (Minecraft.isAmbientOcclusionEnabled())
    {
      GL11.glShadeModel(GL11.GL_SMOOTH);
    }
    else
    {
      GL11.glShadeModel(GL11.GL_FLAT);
    }

    GlStateManager.translate(x, y, z);
  }

  protected static void post() {
    GlStateManager.disableBlend();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

  public static void setColorRGB(int color) {
    setColorRGBA(color | 0xff000000);
  }

  public static void setColorRGBA(int color) {
    float a = (float)(color >> 16 & 255) / 255.0F;
    float r = (float)(color >> 16 & 255) / 255.0F;
    float g = (float)(color >> 8 & 255) / 255.0F;
    float b = (float)(color & 255) / 255.0F;

    GlStateManager.color(r, g, b, a);
  }
}
