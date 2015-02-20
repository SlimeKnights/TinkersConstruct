package tconstruct.test;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import sun.net.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

import tconstruct.library.tinkering.Material;
import tconstruct.library.utils.Log;

public class SimpleColoredTexture extends TextureAtlasSprite {
  private final Material material;
  private final TextureAtlasSprite baseTexture;

  protected SimpleColoredTexture(Material material, TextureAtlasSprite baseTexture,
                                 String spriteName) {
    super(spriteName);

    this.material = material;
    this.baseTexture = baseTexture;
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
    if(baseTexture.getFrameCount() > 0) {
      this.copyFrom(baseTexture);
      data = baseTexture.getFrameTextureData(0);
    }
    else {
      data = backupLoadTexture(manager);
    }

    // go over the base texture and color it
    for(int mipmap = 0; mipmap < data.length; mipmap++) {
      if(data[mipmap] == null)
        continue;
      for(int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++) {
        // we're not working per pixel
        // we take the information in the base texture to calculate the luminosity of the pixel
        // and then color it accordingly with the materials color
        data[mipmap][pxCoord] = colorPixel(data[mipmap][pxCoord]);
      }
    }

    this.framesTextureData.add(data);

    return false;
  }

  protected int colorPixel(int pixel) {
    return material.colorMid | (pixel & (255 << 24));
  }

  // loads the base texture manually, same procedure as TextureMap
  protected int[][] backupLoadTexture(IResourceManager resourceManager) {
    ResourceLocation resourcelocation = new ResourceLocation(baseTexture.getIconName());
    ResourceLocation resourcelocation1 = this.completeResourceLocation(resourcelocation, 0);

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
      Log.error("Using missing texture, unable to load " + resourcelocation1, ioexception1);
    }

    return new int[0][];
  }

  private ResourceLocation completeResourceLocation(ResourceLocation location, int p_147634_2_)
  {
    if(p_147634_2_ == 0)
      return new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", "textures", location.getResourcePath(), ".png"));

    return new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", "textures", location.getResourcePath(), p_147634_2_, ".png"));
  }
}
