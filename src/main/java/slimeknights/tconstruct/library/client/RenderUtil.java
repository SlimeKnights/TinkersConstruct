package slimeknights.tconstruct.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

public final class RenderUtil {
  private RenderUtil() {}

  public static float FLUID_OFFSET = 0.001f; //

  protected static Minecraft mc = Minecraft.getMinecraft();

  /** Renders the given texture tiled into a GUI */
  public static void renderTiledTextureAtlas(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

    putTiledTextureQuads(worldrenderer, x, y, width, height, depth, sprite);

    tessellator.draw();
  }

  /** Adds a quad to the rendering pipeline. Call startDrawingQuads beforehand. You need to call draw() yourself. */
  public static void putTiledTextureQuads(WorldRenderer renderer, int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    float u1 = sprite.getMinU();
    float v1 = sprite.getMinV();

    // tile vertically
    do {
      int renderHeight = Math.min(sprite.getIconHeight(), height);
      height -= renderHeight;

      float v2 = sprite.getInterpolatedV((16f * renderHeight)/(float)sprite.getIconHeight());

      // we need to draw the quads per width too
      int x2 = x;
      int width2 = width;
      // tile horizontally
      do {
        int renderWidth = Math.min(sprite.getIconWidth(), width2);
        width2 -= renderWidth;

        float u2 = sprite.getInterpolatedU((16f * renderWidth)/(float)sprite.getIconWidth());

        renderer.pos(x2,               y,                depth).tex(u1, v1).endVertex();
        renderer.pos(x2,               y + renderHeight, depth).tex(u1, v2).endVertex();
        renderer.pos(x2 + renderWidth, y + renderHeight, depth).tex(u2, v2).endVertex();
        renderer.pos(x2 + renderWidth, y,                depth).tex(u2, v1).endVertex();

        x2 += renderWidth;
      } while(width2 > 0);

      y += renderHeight;
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
    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    int color = fluid.getFluid().getColor(fluid);
    //RenderUtil.setColorRGBA(color);
    int brightness = mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity());

    pre(x, y, z);

    TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

    putTexturedQuad(renderer, still,   x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST, color, brightness);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST, color, brightness);
    putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP, color, brightness);

    tessellator.draw();

    post();
  }

  public static void putTexturedCuboid(WorldRenderer renderer, ResourceLocation location, double x1, double y1, double z1, double x2, double y2, double z2,
                                       int color, int brightness) {
    TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(location.toString());
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST, color, brightness);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST, color, brightness);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP, color, brightness);
  }

  public static void putTexturedQuad(WorldRenderer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                     int color, int brightness) {
    int l1 = brightness >> 0x10 & 0xFFFF;
    int l2 = brightness & 0xFFFF;

    int a = color >> 24 & 0xFF;
    int r = color >> 16 & 0xFF;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;

    putTexturedQuad(renderer, sprite, x,y,z, w,h, d, face, r,g,b,a, l1, l2);
  }

  public static void putTexturedQuad(WorldRenderer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                     int r, int g, int b, int a, int light1, int light2) {
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
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        break;
      case UP:
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case NORTH:
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        break;
      case SOUTH:
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case WEST:
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case EAST:
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
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

  public static void setBrightness(WorldRenderer renderer, int brightness) {
    renderer.putBrightness4(brightness, brightness, brightness, brightness);
  }
}
