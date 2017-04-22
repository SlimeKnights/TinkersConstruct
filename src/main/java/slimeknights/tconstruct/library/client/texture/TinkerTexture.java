package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

import slimeknights.tconstruct.library.TinkerRegistry;

public class TinkerTexture extends TextureAtlasSprite {

  public static TextureAtlasSprite loadManually(String sprite) {
    return new TinkerTexture(sprite);
  }

  protected TinkerTexture(String spriteName) {
    super(spriteName);
  }

  // loads the base texture manually, same procedure as TextureMap.
  // Be careful, this changes the width and height of the current texture. Be sure to preserve it if needed!
  protected TextureAtlasSprite backupLoadTexture(ResourceLocation resourceLocation, IResourceManager resourceManager) {
    if(resourceLocation.equals(TextureMap.LOCATION_MISSING_TEXTURE)) {
      return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    String id = resourceLocation.toString();
    TextureAtlasSprite sprite = AbstractColoredTexture.cache.get(id);
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

      AbstractColoredTexture.cache.put(id, sprite);
    } catch(IOException e) {
      TinkerRegistry.log.error("Unable to generate " + this.getIconName() + ": unable to load " + resourceLocation + "!", e);
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
}
