package tconstruct.test;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Arrays;

public class BakedQuadUV {
  private final BakedQuad parent;
  private final float[] uv; // normalized uv coordinates

  public BakedQuadUV(BakedQuad originalQuad, TextureAtlasSprite sprite) {
    parent = originalQuad;
    uv = new float[originalQuad.getVertexData().length/7 * 2];

    calcNormalizedUVs(sprite);
  }

  private void calcNormalizedUVs(TextureAtlasSprite sprite) {
    int[] vertexData = parent.getVertexData();

    // take the UV coordinates and normalize them to (0-1) and save them
    for(int i = 0; i < vertexData.length/7; i++) {
      float u = Float.intBitsToFloat(vertexData[i*7 + 4]);
      float v = Float.intBitsToFloat(vertexData[i*7 + 5]);

      assert(u >= sprite.getMinU() && u <= sprite.getMaxU());
      assert(v >= sprite.getMinV() && v <= sprite.getMaxV());

      u = 16f * (u - sprite.getMinU()) / (sprite.getMaxU() - sprite.getMinU());
      v = 16f * (v - sprite.getMinV()) / (sprite.getMaxV() - sprite.getMinV());

      uv[i*2 + 0] = u;
      uv[i*2 + 1] = v;
    }
  }

  public BakedQuad applyTexture(TextureAtlasSprite texture) {
    int[] vertexData = Arrays.copyOf(parent.getVertexData(), parent.getVertexData().length);

    // take the saved normalized UV data and apply it to the new texture
    for(int i = 0; i < vertexData.length/7; i++) {
      float u = uv[i*2 + 0];
      float v = uv[i*2 + 1];

      u = texture.getInterpolatedU(u);
      v = texture.getInterpolatedV(v);

      vertexData[i*7 + 4] = Float.floatToRawIntBits(u);
      vertexData[i*7 + 5] = Float.floatToRawIntBits(v);
    }

    return new BakedQuad(vertexData, parent.getTintIndex(), parent.getFace());
  }
}
