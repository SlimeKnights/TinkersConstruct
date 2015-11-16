package slimeknights.tconstruct.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;

public final class RenderUtil {
  private RenderUtil() {}

  public static void renderTiledTextureAtlas(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.startDrawingQuads();
    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

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
}
