package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.RenderUtil;

public abstract class AbstractColoredTexture extends TextureAtlasSprite {

  protected static Map<String, TextureAtlasSprite> cache = Maps.newHashMap();

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

    if(baseTexture == null || baseTexture.getFrameCount() <= 0) {
      // ensure it's null so stuff gets loaded
      baseTexture = null;
      if(extra != null && !extra.isEmpty()) {
        baseTexture = backupLoadTexture(new ResourceLocation(backupTextureLocation + "_" + extra), manager);
      }
      if(baseTexture == null) {
        baseTexture = backupLoadTexture(new ResourceLocation(backupTextureLocation), manager);
      }
    }

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
    else {
      this.width = 1; // needed so we don't crash
      this.height = 1;
      // failure
      return false;
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


  // loads the base texture manually, same procedure as TextureMap.
  // Be careful, this changes the width and height of the current texture. Be sure to preserve it if needed!
  protected TextureAtlasSprite backupLoadTexture(ResourceLocation resourceLocation, IResourceManager resourceManager) {
    if(resourceLocation.equals(TextureMap.LOCATION_MISSING_TEXTURE)) {
      return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    String id = resourceLocation.toString();
    TextureAtlasSprite sprite = cache.get(id);
    if(sprite != null) {
      // got it cached
      return sprite;
    }

    sprite = TextureAtlasSprite.makeAtlasSprite(resourceLocation);
    IResource iresource = null;
    resourceLocation = this.getResourceLocation(resourceLocation);

    try {
      // load the general info
      PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourceLocation));
      iresource = resourceManager.getResource(resourceLocation);
      boolean flag = iresource.getMetadata("animation") != null;
      sprite.loadSprite(pngsizeinfo, flag);

      // load the actual texture data
      sprite.loadSpriteFrames(iresource, Minecraft.getMinecraft().gameSettings.mipmapLevels + 1);

      cache.put(id, sprite);
    } catch(IOException e) {
      TinkerRegistry.log.error("Unable to generate " + this.getIconName() + ": unable to load " + resourceLocation + "!\nBase texture: " + backupTextureLocation, e);
      net.minecraftforge.fml.client.FMLClientHandler.instance().trackMissingTexture(resourceLocation);
      sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    } finally {
      IOUtils.closeQuietly(iresource);
    }

    return sprite;
  }

  // completely emulates the behaviour of the TextureMap texture loading process
  protected TextureAtlasSprite backupLoadtextureAtlasSprite(ResourceLocation resourceLocation, IResourceManager resourceManager) {
    ResourceLocation resourcelocation1 = null;//this.completeResourceLocation(resourceLocation, 0);
    TextureAtlasSprite textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(resourceLocation);

    try {
      IResource iresource = resourceManager.getResource(resourcelocation1);
      BufferedImage[] abufferedimage = new BufferedImage[1 + 4]; // iirc TextureMap.mipmapLevels is always 4? :I
      abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());
      TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

      // 1.9
      /*
      // metadata
      if(texturemetadatasection != null) {
        List<Integer> list = texturemetadatasection.getListMipmaps();
        int i1;

        if(!list.isEmpty()) {
          int l = abufferedimage[0].getWidth();
          i1 = abufferedimage[0].getHeight();

          if(MathHelper.roundUpToPowerOfTwo(l) != l || MathHelper.roundUpToPowerOfTwo(i1) != i1) {
            throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
          }
        }

        for(Integer aList : list) {
          i1 = aList;

          if(i1 > 0 && i1 < abufferedimage.length - 1 && abufferedimage[i1] == null) {
            ResourceLocation resourcelocation2 = this.completeResourceLocation(resourceLocation, i1);

            try {
              abufferedimage[i1] = TextureUtil
                  .readBufferedImage(resourceManager.getResource(resourcelocation2).getInputStream());
            } catch(IOException ioexception) {
              TinkerRegistry.log
                  .error("Unable to load miplevel {} from: {}", i1, resourcelocation2,
                         ioexception);
            }
          }
        }
      }*/

      PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(iresource);
      AnimationMetadataSection animationmetadatasection = iresource.getMetadata("animation");
      textureAtlasSprite.loadSprite(pngsizeinfo, animationmetadatasection != null);

      return textureAtlasSprite;

    } catch(RuntimeException runtimeexception) {
      TinkerRegistry.log.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
    } catch(IOException ioexception1) {
      TinkerRegistry.log.error("Unable to load " + resourcelocation1, ioexception1);
    }

    return null;
  }

  protected ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
    return new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s/%s%s", "textures", resourceLocation.getResourcePath(), ".png"));
  }

  // borrowed from Shadows of Physis
  // Thanks TTFTCUTS! :)
  public static int getPerceptualBrightness(int col) {
    double r = RenderUtil.red(col) / 255.0;
    double g = RenderUtil.green(col) / 255.0;
    double b = RenderUtil.blue(col) / 255.0;

    return getPerceptualBrightness(r, g, b);
  }

  public static int getPerceptualBrightness(double r, double g, double b) {

    double brightness = Math.sqrt(0.241 * r * r + 0.691 * g * g + 0.068 * b * b);

    return (int) (brightness * 255);
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


  public static class CacheClearer implements IResourceManagerReloadListener {

    public static CacheClearer INSTANCE = new CacheClearer();

    private CacheClearer() {
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
      AbstractColoredTexture.cache.clear();
    }
  }
}
