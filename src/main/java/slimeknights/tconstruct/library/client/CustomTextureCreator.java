package slimeknights.tconstruct.library.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.IPatternOffset;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.client.texture.AbstractColoredTexture;
import slimeknights.tconstruct.library.client.texture.CastTexture;
import slimeknights.tconstruct.library.client.texture.GuiOutlineTexture;
import slimeknights.tconstruct.library.client.texture.PatternTexture;
import slimeknights.tconstruct.library.client.texture.TextureColoredTexture;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialGUI;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;

/**
 * Textures registered with this creator will get a texture created/loaded for each material.
 */
public class CustomTextureCreator implements IResourceManagerReloadListener {

  public static final CustomTextureCreator INSTANCE = new CustomTextureCreator();

  private static Logger log = Util.getLogger("TextureGen");

  /**
   * Holds all sprites built from the base-texture used as the key.
   */
  public static Map<String, Map<String, TextureAtlasSprite>> sprites = Maps.newHashMap();

  private static Set<ResourceLocation> baseTextures = Sets.newHashSet();

  private static Map<ResourceLocation, Set<IToolPart>> texturePartMapping = Maps.newHashMap();

  public static void registerTextures(Collection<ResourceLocation> textures) {
    baseTextures.addAll(textures);
  }

  public static void registerTexture(ResourceLocation texture) {
    baseTextures.add(texture);
  }

  public static void registerTextureForPart(ResourceLocation texture, IToolPart toolPart) {
    if(!texturePartMapping.containsKey(texture)) {
      texturePartMapping.put(texture, Sets.<IToolPart>newHashSet());
    }
    texturePartMapping.get(texture).add(toolPart);
    registerTexture(texture);
  }

  // set these to the pattern/cast model to generate part-textures for them
  public static ResourceLocation patternModelLocation;
  public static ResourceLocation castModelLocation;
  public static String patternLocString;
  public static String castLocString;

  public static final Material guiMaterial;

  private int createdTextures;

  // low since other event-handlers might want to register textures beforehand
  @SubscribeEvent(priority = EventPriority.LOW)
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    // only do the processing once: at the end of the loading when the resource manager gets reloaded
    // this is equivalent to a resourcepack change midgame

    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return;
    }


    createdTextures = 0;
    // create textures for each material where needed
    createMaterialTextures(event.getMap());

    // add stencil and cast textures for all used toolparts
    createPatterntextures(event.getMap());

    log.debug("Generated " + createdTextures + " Textures for Materials");
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
      Set<IToolPart> parts = texturePartMapping.get(baseTexture);

      Map<String, TextureAtlasSprite> builtSprites = Maps.newHashMap();
      for(Material material : TinkerRegistry.getAllMaterials()) {
        boolean usable;
        if(parts == null || material instanceof MaterialGUI) {
          usable = true;
        }
        else {
          usable = false;
          for(IToolPart toolPart : parts) {
            usable |= toolPart.canUseMaterial(material);
          }
        }

        if(usable) {
          TextureAtlasSprite sprite = createTexture(material, baseTexture, base, map);
          if(sprite != null) {
            builtSprites.put(material.identifier, sprite);
          }
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
      createdTextures++;
    }

    // stitch new textures
    if(sprite != null && material.renderInfo.isStitched()) {
      map.setTextureEntry(location, sprite);
    }
    return sprite;
  }

  private void createPatterntextures(TextureMap map) {
    // create Pattern textures
    if(patternModelLocation != null) {
      patternLocString = createPatternTexturesFor(map, patternModelLocation, TinkerRegistry.getPatternItems(), PatternTexture.class);
    }
    // create cast textures
    if(castModelLocation != null) {
      castLocString = createPatternTexturesFor(map, castModelLocation, TinkerRegistry.getCastItems(), CastTexture.class);
    }
  }

  public String createPatternTexturesFor(TextureMap map, ResourceLocation baseTextureLoc, Iterable<Item> items, Class<? extends TextureColoredTexture> clazz) {
    Constructor<? extends TextureColoredTexture> constructor;
    String baseTextureString;
    TextureAtlasSprite baseTexture;
    try {
      constructor = clazz.getConstructor(String.class, TextureAtlasSprite.class, String.class);
      IModel patternModel = ModelLoaderRegistry.getModel(baseTextureLoc);
      ResourceLocation patternLocation = patternModel.getTextures().iterator().next();
      baseTexture = map.getTextureExtry(patternLocation.toString());
      baseTextureString = patternLocation.toString();
      if(baseTexture == null) {
        log.error("No basetexture found for pattern texture generation: " + patternLocation);
        return null;
      }
    } catch(Exception e) {
      log.error(e);
      return null;
    }


    for(Item item : items) {
      try {
        // get id
        String identifier = Pattern.getTextureIdentifier(item);
        String partPatternLocation = baseTextureString + identifier;
        TextureAtlasSprite partPatternTexture;
        if(exists(partPatternLocation)) {
          partPatternTexture = map.registerSprite(new ResourceLocation(partPatternLocation));
          map.setTextureEntry(partPatternLocation, partPatternTexture);
        }
        else {
          /*
          ResourceLocation modelLocation = getModelLocationForItem(item);
          IModel partModel = ModelLoaderRegistry.getModel(modelLocation);
          */
          ResourceLocation modelLocation = Util.getItemLocation(item);
          IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                               "item/parts/" + modelLocation
                                                                                   .getResourcePath()
                                                                               + MaterialModelLoader.EXTENSION));
          ResourceLocation partTexture = partModel.getTextures().iterator().next();

          if(partModel != ModelLoaderRegistry.getMissingModel()) {
            partPatternTexture = constructor.newInstance(partTexture.toString(), baseTexture, partPatternLocation);
            if(partModel instanceof IPatternOffset) {
              IPatternOffset offset = (IPatternOffset) partModel;
              ((TextureColoredTexture) partPatternTexture).setOffset(offset.getXOffset(), offset.getYOffset());
            }
            map.setTextureEntry(partPatternLocation, partPatternTexture);
          }
        }
      } catch(Exception e) {
        log.error(e);
      }
    }

    return baseTextureString;
  }

  private ResourceLocation getModelLocationForItem(Item item) {
    String loc = null;
    try {
      Field field = ModelBakery.class.getDeclaredField("customVariantNames");
      field.setAccessible(true);
      Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Item>, Set<String>> map = (Map<RegistryDelegate<Item>, Set<String>>) field.get(null);
      Set<String> variants = map.get(item.delegate);
      if(variants != null) {
        loc = variants.iterator().next();
      }
    } catch(NoSuchFieldException e) {
      e.printStackTrace();
    } catch(IllegalAccessException e) {
      e.printStackTrace();
    }

    if(loc == null) {
      loc = Util.getItemLocation(item).toString();
    }
    ResourceLocation rl = new ResourceLocation(loc.replaceAll("#.*", ""));
    rl = new ResourceLocation(rl.getResourceDomain(), "item/" + rl.getResourcePath());
    return rl;
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

      } catch(Exception e) {
        log.error(e);
      }
    }
  }

  public static String getItemLoc(String res) {
    ResourceLocation loc = new ResourceLocation(res);
    return String.format("%s:items/%s", loc.getResourceDomain(), loc.getResourcePath());
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
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    // clear cache
    baseTextures.clear();
    for(Map map : sprites.values()) {
      // safety in case there are some references lying around
      map.clear();
    }
    sprites.clear();
  }

  public static ResourceLocation getTextureLocationFromToolPart(IToolPart toolpart) throws Exception {
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
      } catch(Exception e) {
        return false;
      }
    }
    return false;
  }

  static {
    guiMaterial = new MaterialGUI("_internal_gui");
    guiMaterial.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new GuiOutlineTexture(baseTexture, location);
      }
    });
  }
}
