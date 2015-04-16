package tconstruct.test;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import tconstruct.library.utils.Log;

public abstract class AbstractColoredTexture extends TextureAtlasSprite {
  private final TextureAtlasSprite baseTexture;
  private final String backupTextureLocation;
  private final String extra;

  protected AbstractColoredTexture(TextureAtlasSprite baseTexture, String spriteName) {
    super(spriteName);
    this.baseTexture = baseTexture;
    this.backupTextureLocation = baseTexture.getIconName();
    this.extra = "";
  }

  protected AbstractColoredTexture(String baseTextureLocation, String extra, String spriteName) {
    super(spriteName);

    this.baseTexture = null;
    this.backupTextureLocation = baseTextureLocation;
    this.extra = extra;
  }

  @Override
  public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
    return true;
  }

  @Override
  public boolean load(IResourceManager manager, ResourceLocation location) {
    this.framesTextureData = Lists.newArrayList();
    this.frameCounter = 0;
    this.tickCounter = 0;

    // get the base texture to work on
    int[][] data;
    // basetexture is present and loaded
    if(baseTexture != null && baseTexture.getFrameCount() > 0) {
      this.copyFrom(baseTexture);
      int[][] original = baseTexture.getFrameTextureData(0);
      data = new int[original.length][];
      for(int i = 0; i < original.length; i++) {
        if(original[i] != null)
          data[i] = Arrays.copyOf(original[i], original[i].length);
      }
    }
    // load texture manually
    else {
      data = null;
      if(extra != null && !extra.isEmpty())
        data = backupLoadTexture(new ResourceLocation(backupTextureLocation + "_" + extra), manager);
      if(data == null)
        data = backupLoadTexture(new ResourceLocation(backupTextureLocation), manager);
    }

    // go over the base texture and color it
    for(int mipmap = 0; mipmap < data.length; mipmap++) {
      if(data[mipmap] == null)
        continue;
      for(int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++) {
        // we're not working per pixel
        // we take the information in the base texture to calculate the luminosity of the pixel
        // and then color it accordingly with the materials color
        data[mipmap][pxCoord] = colorPixel(data[mipmap][pxCoord], mipmap, pxCoord);
      }
    }

    this.framesTextureData.add(data);

    return false;
  }

  protected abstract int colorPixel(int pixel, int mipmap, int pxCoord);

  // loads the base texture manually, same procedure as TextureMap
  protected int[][] backupLoadTexture(ResourceLocation resourceLocation, IResourceManager resourceManager) {
    ResourceLocation resourcelocation1 = this.completeResourceLocation(resourceLocation, 0);

    try {
      IResource iresource = resourceManager.getResource(resourcelocation1);
      BufferedImage[] abufferedimage = new BufferedImage[1 + 4];
      abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());

      this.width = abufferedimage[0].getWidth();
      this.height = abufferedimage[0].getHeight();

      int[][] aint = new int[abufferedimage.length][];
      for (int k = 0; k < abufferedimage.length; ++k)
      {
        BufferedImage bufferedimage = abufferedimage[k];

        if (bufferedimage != null)
        {
          aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
          bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
        }
      }

      return aint;
    }
    catch (RuntimeException runtimeexception)
    {
      Log.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
    }
    catch (IOException ioexception1)
    {
      Log.error("Unable to load " + resourcelocation1, ioexception1);
    }

    return null;
  }

  private ResourceLocation completeResourceLocation(ResourceLocation location, int p_147634_2_)
  {
    if(p_147634_2_ == 0)
      return new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", "textures", location.getResourcePath(), ".png"));

    return new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", "textures", location.getResourcePath(), p_147634_2_, ".png"));
  }

  // borrowed from Shadows of Physis
  // Thanks TTFTCUTS! :)
  public static int getPerceptualBrightness(int col) {
    double r = red(col) / 255.0;
    double g = green(col) / 255.0;
    double b = blue(col) / 255.0;

    return getPerceptualBrightness(r,g,b);
  }

  public static int getPerceptualBrightness(double r, double g, double b) {

    double brightness = Math.sqrt(0.241 * r*r + 0.691 * g*g + 0.068 * b*b);

    return (int)(brightness*255);
  }

  public static int compose(int r, int g, int b, int a) {
    int rgb = a;
    rgb = (rgb << 8) + r;
    rgb = (rgb << 8) + g;
    rgb = (rgb << 8) + b;
    return rgb;
  }

  public static int alpha(int c) {
    return (c >> 24) & 0xFF;
  }

  public static int red(int c) {
    return (c >> 16) & 0xFF;
  }

  public static int green(int c) {
    return (c >> 8) & 0xFF;
  }

  public static int blue(int c) {
    return (c) & 0xFF;
  }

  protected static int mult(int c1, int c2) {
    return (int)(c1 * (c2/255.0));
  }
}
