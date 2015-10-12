package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class MetalColoredTexture extends AbstractColoredTexture {

  protected int baseColor;
  protected float shinyness;
  protected float brightness;
  protected float hueshift;

  public MetalColoredTexture(TextureAtlasSprite baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(baseTexture, spriteName);
    this.baseColor = baseColor;
    this.shinyness = shinyness;
    this.brightness = brightness;
    this.hueshift = hueshift;
  }

  public MetalColoredTexture(String baseTextureLocation, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(baseTextureLocation, spriteName);
    this.baseColor = baseColor;
    this.shinyness = shinyness;
    this.brightness = brightness;
    this.hueshift = hueshift;
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    int brightness = getPerceptualBrightness(pixel);
    float l = brightness/255f;

    int c = baseColor;

    // multiply in the color
    int r = red(c);
    int b = blue(c);
    int g = green(c);

    r = mult(r, red(pixel)) & 0xff;
    g = mult(g, blue(pixel)) & 0xff;
    b = mult(b, green(pixel)) & 0xff;


    float[] hsl = Color.RGBtoHSB(r,g,b, null);
    hsl[0] -= (0.5f-l*l) * hueshift;
    //float l = (brightness/255f)*(brightness/255f);
    // brightness of the template affects saturation. the brighter the less saturation to simulate shiny metal
    if(l > 0.9f) {
      hsl[1] = MathHelper.clamp_float(hsl[1] - (l * l * shinyness), 0, 1);
    }

    // lightness too!
    //hsl[2] = MathHelper.clamp_float(hsl[2] - 0.3f + l*l*0.8f, 0, 1);
    //hsl[2] = MathHelper.clamp_float(hsl[2], 0, 1);
    if(l > 0.8f)
      hsl[2] = MathHelper.clamp_float(hsl[2] + l*l*brightness, 0, 1);
    //else if(l > 0.4f)
      //hsl[2] = MathHelper.clamp_float(hsl[2] + l * l * 0.1f, 0, 1);

    c = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
    r = red(c);
    b = blue(c);
    g = green(c);

    // put it back together
    return compose(r, g, b, a);
  }
}
