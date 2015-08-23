package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;

public abstract class AbstractColoredTexture extends TextureAtlasSprite {

  private TextureAtlasSprite baseTexture;
  private String backupTextureLocation;
  private String extra;

  protected AbstractColoredTexture(TextureAtlasSprite baseTexture, String spriteName) {
    super(spriteName);
    this.baseTexture = baseTexture;
    this.backupTextureLocation = baseTexture.getIconName();
  }

  protected AbstractColoredTexture(String baseTextureLocation, String spriteName) {
    super(spriteName);

    this.baseTexture = null;
    this.backupTextureLocation = baseTextureLocation;
  }

  public TextureAtlasSprite setSuffix(String suffix) {
    this.extra = suffix;
    this.baseTexture = null;
    return this;
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
        if(original[i] != null) {
          data[i] = Arrays.copyOf(original[i], original[i].length);
        }
      }
    }
    // load texture manually
    else {
      data = null;
      if(extra != null && !extra.isEmpty()) {
        data = backupLoadTexture(new ResourceLocation(backupTextureLocation + "_" + extra), manager);
      }
      if(data == null) {
        data = backupLoadTexture(new ResourceLocation(backupTextureLocation), manager);
      }
    }

    processData(data);

    if(this.framesTextureData.isEmpty()) {
      this.framesTextureData.add(data);
    }

    return false;
  }

  protected void processData(int[][] data) {
    // go over the base texture and color it
    for(int mipmap = 0; mipmap < data.length; mipmap++) {
      if(data[mipmap] == null) {
        continue;
      }
      for(int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++) {
        // we're not working per pixel
        // we take the information in the base texture to calculate the luminosity of the pixel
        // and then color it accordingly with the materials color
        data[mipmap][pxCoord] = colorPixel(data[mipmap][pxCoord], mipmap, pxCoord);
      }
    }
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
      for(int k = 0; k < abufferedimage.length; ++k) {
        BufferedImage bufferedimage = abufferedimage[k];

        if(bufferedimage != null) {
          aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
          bufferedimage
              .getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
        }
      }

      return aint;
    } catch(RuntimeException runtimeexception) {
      TinkerRegistry.log.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
    } catch(IOException ioexception1) {
      TinkerRegistry.log.error("Unable to generate " + this.getIconName() + ": unable to load " + resourcelocation1 + "!\nBase texture: " + baseTexture.getIconName(), ioexception1);
    }

    return null;
  }

  // completely emulates the behaviour of the TextureMap texture loading process
  protected TextureAtlasSprite backupLoadtextureAtlasSprite(ResourceLocation resourceLocation, IResourceManager resourceManager) {
    ResourceLocation resourcelocation1 = this.completeResourceLocation(resourceLocation, 0);
    TextureAtlasSprite textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(resourceLocation);

    try {
      IResource iresource = resourceManager.getResource(resourcelocation1);
      BufferedImage[] abufferedimage = new BufferedImage[1 + 4]; // iirc TextureMap.mipmapLevels is always 4? :I
      abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());
      TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");

      // metadata
      if(texturemetadatasection != null) {
        List list = texturemetadatasection.getListMipmaps();
        int i1;

        if(!list.isEmpty()) {
          int l = abufferedimage[0].getWidth();
          i1 = abufferedimage[0].getHeight();

          if(MathHelper.roundUpToPowerOfTwo(l) != l || MathHelper.roundUpToPowerOfTwo(i1) != i1) {
            throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
          }
        }

        Iterator iterator3 = list.iterator();

        while(iterator3.hasNext()) {
          i1 = ((Integer) iterator3.next()).intValue();

          if(i1 > 0 && i1 < abufferedimage.length - 1 && abufferedimage[i1] == null) {
            ResourceLocation resourcelocation2 = this.completeResourceLocation(resourceLocation, i1);

            try
            {
              abufferedimage[i1] = TextureUtil.readBufferedImage(resourceManager.getResource(resourcelocation2).getInputStream());
            }
            catch (IOException ioexception)
            {
              TinkerRegistry.log.error("Unable to load miplevel {} from: {}", new Object[] {Integer.valueOf(i1), resourcelocation2, ioexception});
            }
          }
        }
      }

      AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection) iresource.getMetadata("animation");
      textureAtlasSprite.loadSprite(abufferedimage, animationmetadatasection);

      return textureAtlasSprite;

    } catch(RuntimeException runtimeexception) {
      TinkerRegistry.log.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
    } catch(IOException ioexception1) {
      TinkerRegistry.log.error("Unable to load " + resourcelocation1, ioexception1);
    }

    return null;
  }

  protected ResourceLocation completeResourceLocation(ResourceLocation location, int p_147634_2_) {
    if(p_147634_2_ == 0) {
      return new ResourceLocation(location.getResourceDomain(),
                                  String.format("%s/%s%s", "textures", location.getResourcePath(), ".png"));
    }

    return new ResourceLocation(location.getResourceDomain(), String
        .format("%s/mipmaps/%s.%d%s", "textures", location.getResourcePath(), p_147634_2_, ".png"));
  }

  // borrowed from Shadows of Physis
  // Thanks TTFTCUTS! :)
  public static int getPerceptualBrightness(int col) {
    double r = red(col) / 255.0;
    double g = green(col) / 255.0;
    double b = blue(col) / 255.0;

    return getPerceptualBrightness(r, g, b);
  }

  public static int getPerceptualBrightness(double r, double g, double b) {

    double brightness = Math.sqrt(0.241 * r * r + 0.691 * g * g + 0.068 * b * b);

    return (int) (brightness * 255);
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
    return (int) ((float) c1 * (c2 / 255f));
  }

  // Get coordinates from index and vice versa
  protected int getX(int pxCoord) {
    return pxCoord % width;
  }

  protected int getY(int pxCoord) {
   return pxCoord / width;
  }

  protected int coord(int x, int y) {
    return y * width + x;
  }
}
