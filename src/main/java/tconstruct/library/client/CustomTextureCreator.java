package tconstruct.library.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.client.texture.AbstractColoredTexture;
import tconstruct.library.materials.Material;

/**
 * Textures registered with this creator will get a texture created/loaded for each material.
 */
public class CustomTextureCreator implements IResourceManagerReloadListener {

  private static Logger log = Util.getLogger("TextureGen");

  private static Set<ResourceLocation> baseTextures = Sets.newHashSet();

  /**
   * Holds all sprites built from the base-texture used as the key.
   */
  public static Map<String, Map<String, TextureAtlasSprite>> sprites = Maps.newHashMap();

  public static void registerTextures(Collection<ResourceLocation> textures) {
    baseTextures.addAll(textures);
  }

  public static void registerTexture(ResourceLocation texture) {
    baseTextures.add(texture);
  }

  // low since other event-handlers might want to register textures beforehand
  @SubscribeEvent(priority = EventPriority.LOW)
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    // only do the processing once: at the end of the loading when the resource manager gets reloaded
    // this is equivalent to a resourcepack change midgame

    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return;
    }

    TextureMap map = event.map;

    for(ResourceLocation baseTexture : baseTextures) {
      // exclude missingno :I
      if(baseTexture.toString().equals("minecraft:missingno")) {
        continue;
      }

      TextureAtlasSprite base = map.getTextureExtry(baseTexture.toString());
      if(base == null) {
        log.error("Missing base texture: " + baseTexture.toString());
        continue;
      }

      Map<String, TextureAtlasSprite> builtSprites = Maps.newHashMap();
      for(Material material : TinkerRegistry.getAllMaterials()) {
        String location = baseTexture.toString() + "_" + material.identifier;
        TextureAtlasSprite sprite;

        if(exists(location)) {
          sprite = map.registerSprite(new ResourceLocation(location));
        }
        else {
          // material does not need a special generated texture
          if(material.renderInfo == null) {
            continue;
          }

          TextureAtlasSprite matBase = base;

          // different base texture?
          if(material.renderInfo.getTextureSuffix() != null) {
            String loc2 = baseTexture.toString() + "_" + material.renderInfo.getTextureSuffix();
            TextureAtlasSprite base2 = map.getTextureExtry(loc2);
            // can we manually load it?
            if(base2 == null && exists(loc2)) {
              base2 = new AbstractColoredTexture(loc2, loc2) {
                @Override
                protected int colorPixel(int pixel, int mipmap, int pxCoord) {
                  return pixel;
                }
              };

              // save in the map so it's getting reused by the others and is available
              map.setTextureEntry(loc2, base2);
            }
            if(base2 != null) {
              matBase = base2;
            }
          }

          sprite = material.renderInfo.getTexture(matBase, location);
        }

        // stitch new textures
        if(sprite != null && material.renderInfo.isStitched()) {
          map.setTextureEntry(location, sprite);
        }
        builtSprites.put(material.identifier, sprite);
      }

      sprites.put(baseTexture.toString(), builtSprites);
    }
  }

  public static boolean exists(String res) {
    try {
      ResourceLocation loc = new ResourceLocation(res);
      loc = new ResourceLocation(loc.getResourceDomain(), "textures/" + loc.getResourcePath() + ".png");
      Minecraft.getMinecraft().getResourceManager().getAllResources(loc);
      return true;
    } catch(IOException e) {
      return false;
    }
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    // clear cache
    baseTextures.clear();
    for(Map map : sprites.values()) {
      // safety in case there are some references lying around
      map.clear();
    }
    sprites.clear();
  }
}
