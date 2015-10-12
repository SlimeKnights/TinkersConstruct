package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class MetalTextureTexture extends TextureColoredTexture {

  protected MetalColoredTexture texture2;

  public MetalTextureTexture(String addTextureLocation, TextureAtlasSprite baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(addTextureLocation, baseTexture, spriteName);
    texture2 = new MetalColoredTexture(baseTexture, spriteName, baseColor, shinyness, brightness, hueshift);
  }

  public MetalTextureTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(addTexture, baseTexture, spriteName);
    texture2 = new MetalColoredTexture(baseTexture, spriteName, baseColor, shinyness, brightness, hueshift);
  }

  @Override
  public boolean load(IResourceManager manager, ResourceLocation location) {
    // at frist do the metal texture
    texture2.load(manager, location);
    return super.load(manager, location);
  }

  @Override
  protected void processData(int[][] data) {
    // go over the base texture and color it
    for(int mipmap = 0; mipmap < data.length; mipmap++) {
      if(data[mipmap] == null) {
        continue;
      }
      for(int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++) {
        // get input from metal
        data[mipmap][pxCoord] = colorPixel(texture2.getFrameTextureData(0)[mipmap][pxCoord], mipmap, pxCoord);
      }
    }
  }
}
