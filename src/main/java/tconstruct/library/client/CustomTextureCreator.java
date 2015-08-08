package tconstruct.library.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.library.client.texture.AbstractColoredTexture;
import tconstruct.library.client.texture.CastTexture;
import tconstruct.library.client.texture.GuiOutlineTexture;
import tconstruct.library.client.texture.PatternTexture;
import tconstruct.library.materials.Material;
import tconstruct.library.tools.IToolPart;

/**
 * Textures registered with this creator will get a texture created/loaded for each material.
 */
public class CustomTextureCreator implements IResourceManagerReloadListener {

  private static Logger log = Util.getLogger("TextureGen");

  /**
   * Holds all sprites built from the base-texture used as the key.
   */
  public static Map<String, Map<String, TextureAtlasSprite>> sprites = Maps.newHashMap();

  private static Set<ResourceLocation> baseTextures = Sets.newHashSet();

  public static void registerTextures(Collection<ResourceLocation> textures) {
    baseTextures.addAll(textures);
  }

  public static void registerTexture(ResourceLocation texture) {
    baseTextures.add(texture);
  }

  // set these to the pattern/cast model to generate part-textures for them
  public static ResourceLocation patternModelLocation;
  public static ResourceLocation castModelLocation;

  public static final Material guiMaterial;

  // low since other event-handlers might want to register textures beforehand
  @SubscribeEvent(priority = EventPriority.LOW)
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    // only do the processing once: at the end of the loading when the resource manager gets reloaded
    // this is equivalent to a resourcepack change midgame

    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return;
    }

    // create textures for each material where needed
    createMaterialTextures(event.map);

    // add stencil and cast textures for all used toolparts
    createPatterntextures(event.map);
  }

  private void createMaterialTextures(TextureMap map) {
    // Create textures for toolparts and tools - Textures that need 1 per material
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
        TextureAtlasSprite sprite = createTexture(material, baseTexture, base, map);
        if(sprite != null) {
          builtSprites.put(material.identifier, sprite);
        }
      }

      if(belongsToToolPart(baseTexture)) {
        TextureAtlasSprite sprite = createTexture(guiMaterial, baseTexture, base, map);
        if(sprite != null) {
          builtSprites.put(guiMaterial.identifier, sprite);
        }
      }

      sprites.put(baseTexture.toString(), builtSprites);
    }
  }

  private TextureAtlasSprite createTexture(Material material, ResourceLocation baseTexture, TextureAtlasSprite base, TextureMap map) {
    String location = baseTexture.toString() + "_" + material.identifier;
    TextureAtlasSprite sprite;

    if(exists(location)) {
      sprite = map.registerSprite(new ResourceLocation(location));
    }
    else {
      // material does not need a special generated texture
      if(material.renderInfo == null) {
        return null;
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
    return sprite;
  }

  private void createPatterntextures(TextureMap map) {
    // nothing to do
    if(patternModelLocation == null && castModelLocation == null) {
      return;
    }

    try {
      TextureAtlasSprite pattern = null;
      TextureAtlasSprite cast = null;
      {
        IModel patternModel = ModelLoaderRegistry.getModel(patternModelLocation);
        ResourceLocation patternLocation = patternModel.getTextures().iterator().next();
        pattern = map.getTextureExtry(patternLocation.toString());
      }
      {
        IModel patternModel = ModelLoaderRegistry.getModel(castModelLocation);
        ResourceLocation patternLocation = patternModel.getTextures().iterator().next();
        cast = map.getTextureExtry(patternLocation.toString());
      }

      String patternLocString = Util.getResource("Pattern").toString() + "_";
      String castLocString = Util.getResource("Cast").toString() + "_";

      for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
        if(!(toolpart instanceof Item)) {
          continue; // WHY?!
        }

        ResourceLocation modelLocation = Util.getItemLocation((Item) toolpart);
        IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                             "item/parts/" + modelLocation
                                                                                 .getResourcePath()
                                                                             + MaterialModelLoader.EXTENSION));
        ResourceLocation partTexture = partModel.getTextures().iterator().next();

        // Pattern
        if(pattern != null) {
          String partPatternLocation = patternLocString + toolpart.getIdentifier();
          TextureAtlasSprite partPatternTexture =
              new PatternTexture(partTexture.toString(), pattern, partPatternLocation);

          map.setTextureEntry(partPatternLocation, partPatternTexture);
        }
        if(cast != null) {
          String partCastLocation = castLocString + toolpart.getIdentifier();
          TextureAtlasSprite partCastTexture =
              new CastTexture(partTexture.toString(), cast, partCastLocation);

          map.setTextureEntry(partCastLocation, partCastTexture);
        }
      }
    } catch(IOException e) {
      // should never happen
      log.error(e);
    }
  }

  // the same as materialtextures but only creates the ones for toolparts for the gui
  private void createGUITextures(TextureMap map) {
    for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
      if(!(toolpart instanceof Item)) {
        continue; // WHY?!
      }

      try {
        // name and model location
        ResourceLocation modelLocation = Util.getItemLocation((Item) toolpart);
        IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                             "item/parts/" + modelLocation
                                                                                 .getResourcePath()
                                                                             + MaterialModelLoader.EXTENSION));
        // the actual texture of the part
        ResourceLocation baseTexture = partModel.getTextures().iterator().next();

        TextureAtlasSprite base = map.getTextureExtry(baseTexture.toString());
        if(base == null) {
          log.error("Missing base texture: " + baseTexture.toString());
          continue;
        }

        // does it have textures?
        Map<String, TextureAtlasSprite> partTextures = sprites.get(baseTexture.toString());
        if(partTextures == null) {
          continue;
        }

        String location = baseTexture.toString() + "_internal_gui";
        // the texture created
        TextureAtlasSprite outlineTexture = new GuiOutlineTexture(base, location);

        // add it to the loading list
        map.setTextureEntry(location, outlineTexture);
        partTextures.put("_internal_gui", outlineTexture);

      } catch(IOException e) {
        log.error(e);
      }
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

  public static ResourceLocation getTextureLocationFromToolPart(IToolPart toolpart) throws IOException {
    if(!(toolpart instanceof Item)) {
      return null; // WHY?!
    }

    ResourceLocation modelLocation = Util.getItemLocation((Item) toolpart);
    IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                         "item/parts/" + modelLocation
                                                                             .getResourcePath()
                                                                         + MaterialModelLoader.EXTENSION));
    ResourceLocation partTexture = partModel.getTextures().iterator().next();

    return partTexture;
  }

  public static boolean belongsToToolPart(ResourceLocation location) {
    for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
      if(!(toolpart instanceof Item)) {
        continue; // WHY?!
      }

      try {
        // name and model location
        ResourceLocation modelLocation = Util.getItemLocation((Item) toolpart);
        IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                             "item/parts/" + modelLocation
                                                                                 .getResourcePath()
                                                                             + MaterialModelLoader.EXTENSION));
        // the actual texture of the part
        ResourceLocation baseTexture = partModel.getTextures().iterator().next();
        if(baseTexture.toString().equals(location.toString())) {
          return true;
        }
      } catch(IOException e) {
        return false;
      }
    }
    return false;
  }

  static {
    guiMaterial = new Material("_internal_gui", EnumChatFormatting.WHITE);
    guiMaterial.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new GuiOutlineTexture(baseTexture, location);
      }
    });
  }
}
