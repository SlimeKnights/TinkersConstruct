package tconstruct.library.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import tconstruct.library.TinkerRegistry;

public class AnimatedColoredTexture extends TextureColoredTexture {

  private TextureAtlasSprite actualTexture;

  public AnimatedColoredTexture(TextureAtlasSprite addTexture,
                                TextureAtlasSprite baseTexture, String spriteName) {
    super(addTexture, baseTexture, spriteName);
  }

  @Override
  public boolean load(IResourceManager manager, ResourceLocation location) {
    if(addTexture.getFrameCount() > 0) {
      actualTexture = addTexture;
    }
    else {
      actualTexture = backupLoadtextureAtlasSprite(new ResourceLocation(addTextureLocation),
                                                   Minecraft.getMinecraft().getResourceManager());
    }

    return super.load(manager, location);
  }

  @Override
  protected void processData(int[][] data) {
    // get animation data again
    ResourceLocation resourcelocation1 = this.completeResourceLocation(new ResourceLocation(addTextureLocation), 0);
    IResource iresource = null;
    try {
      iresource = Minecraft.getMinecraft().getResourceManager().getResource(resourcelocation1);
    } catch(IOException e) {
      TinkerRegistry.log.error("Unable to load " + resourcelocation1, e);
      return;
    }
    AnimationMetadataSection meta = (AnimationMetadataSection)iresource.getMetadata("animation");

    // todo: access transform this
    try {
      Field f = TextureAtlasSprite.class.getDeclaredField("animationMetadata");
      f.setAccessible(true);
      f.set(this, meta);
    } catch(ReflectiveOperationException e) {
      e.printStackTrace();
    }

    // input is the data of the template
    // we now adapt this textureAtlasSprite to match the animated actualTexture
    for(int i = 0; i < meta.getFrameCount(); i++) {
      Iterator iterator = meta.getFrameIndexSet().iterator();

      while (iterator.hasNext())
      {
        int i1 = ((Integer)iterator.next()).intValue();

        // missing check if frame index is valid

        //this.allocateFrameTextureData(i1);
        if (this.framesTextureData.size() <= i1)
        {
          for (int j = this.framesTextureData.size(); j <= i1; ++j)
          {
            this.framesTextureData.add(null);
          }
        }

        int[][] data2 = new int[data.length][];
        for(int j = 0; j < data.length; j++) {
          if(data[j] != null) {
            data2[j] = data[j].clone();
          }
        }

        // set textureData for processing
        textureData = actualTexture.getFrameTextureData(i1);
        // process the copied data
        super.processData(data2);

        // add it to the textures data
        this.framesTextureData.set(i1, data2);
      }
    }
  }
}
