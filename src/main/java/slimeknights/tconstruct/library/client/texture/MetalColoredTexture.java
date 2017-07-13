package slimeknights.tconstruct.library.client.texture;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import slimeknights.tconstruct.library.client.RenderUtil;

public class MetalColoredTexture extends AbstractColoredTexture {

  protected int baseColor;
  protected float shinyness;
  protected float brightness;
  protected float hueshift;

  public MetalColoredTexture(ResourceLocation baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(baseTexture, spriteName);
    this.baseColor = baseColor;
    this.shinyness = shinyness;
    this.brightness = brightness;
    this.hueshift = hueshift;
  }

  @Override
  protected int colorPixel(int pixel, int pxCoord) {
    int a = RenderUtil.alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    float l = getPerceptualBrightness(pixel) / 255f;

    int c = baseColor;

    // multiply in the color
    int r = RenderUtil.red(c);
    int b = RenderUtil.blue(c);
    int g = RenderUtil.green(c);

    r = mult(r, RenderUtil.red(pixel)) & 0xff;
    g = mult(g, RenderUtil.blue(pixel)) & 0xff;
    b = mult(b, RenderUtil.green(pixel)) & 0xff;


    float[] hsl = Color.RGBtoHSB(r, g, b, null);
    hsl[0] -= (0.5f - l * l) * hueshift;
    //float l = (brightness/255f)*(brightness/255f);
    // brightness of the template affects saturation. the brighter the less saturation to simulate shiny metal
    if(l > 0.9f) {
      hsl[1] = MathHelper.clamp(hsl[1] - (l * l * shinyness), 0, 1);
    }

    // lightness too!
    //hsl[2] = MathHelper.clamp_float(hsl[2] - 0.3f + l*l*0.8f, 0, 1);
    //hsl[2] = MathHelper.clamp_float(hsl[2], 0, 1);
    if(l > 0.8f) {
      hsl[2] = MathHelper.clamp(hsl[2] + l * l * brightness, 0, 1);
    }
    //else if(l > 0.4f)
    //hsl[2] = MathHelper.clamp_float(hsl[2] + l * l * 0.1f, 0, 1);

    c = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
    r = RenderUtil.red(c);
    b = RenderUtil.blue(c);
    g = RenderUtil.green(c);

    // put it back together
    return RenderUtil.compose(r, g, b, a);
  }
}
