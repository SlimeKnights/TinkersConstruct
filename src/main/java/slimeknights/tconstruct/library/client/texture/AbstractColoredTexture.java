package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.RenderUtil;

public abstract class AbstractColoredTexture extends TinkerTexture {

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
