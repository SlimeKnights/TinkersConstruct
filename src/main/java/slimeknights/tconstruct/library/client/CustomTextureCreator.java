package slimeknights.tconstruct.library.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.material.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.IPatternOffset;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.client.texture.CastTexture;
import slimeknights.tconstruct.library.client.texture.GuiOutlineTexture;
import slimeknights.tconstruct.library.client.texture.PatternTexture;
import slimeknights.tconstruct.library.client.texture.TextureColoredTexture;
import slimeknights.tconstruct.library.client.texture.TinkerTexture;
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
      texturePartMapping.put(texture, Sets.newHashSet());
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
    // get the material info at this point, to override hardcoded material rendering with resources
    MaterialRenderInfoLoader.INSTANCE.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());

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
            usable |= toolPart.canUseMaterialForRendering(material);
          }
        }

        if(usable) {
          TextureAtlasSprite sprite = createTexture(material, baseTexture, map);
          if(sprite != null) {
            builtSprites.put(material.identifier, sprite);
          }
        }
      }

      if(belongsToToolPart(baseTexture)) {
        TextureAtlasSprite sprite = createTexture(guiMaterial, baseTexture, map);
        if(sprite != null) {
          builtSprites.put(guiMaterial.identifier, sprite);
        }
      }

      sprites.put(baseTexture.toString(), builtSprites);
    }
  }

  private TextureAtlasSprite createTexture(Material material, ResourceLocation baseTexture, TextureMap map) {
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

      // different base texture?
      if(material.renderInfo.getTextureSuffix() != null) {
        String loc2 = baseTexture.toString() + "_" + material.renderInfo.getTextureSuffix();
        TextureAtlasSprite base2 = map.getTextureExtry(loc2);
        // can we manually load it?
        if(base2 == null && exists(loc2)) {
          base2 = TinkerTexture.loadManually(new ResourceLocation(loc2));
          // save in the map so it's getting reused by the others and is available
          map.setTextureEntry(base2);
        }
        if(base2 != null) {
          baseTexture = new ResourceLocation(base2.getIconName());
        }
      }

      sprite = material.renderInfo.getTexture(baseTexture, location);
      createdTextures++;
    }

    // stitch new textures
    if(sprite != null && material.renderInfo.isStitched()) {
      map.setTextureEntry(sprite);
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
    ResourceLocation patternLocation;
    try {
      constructor = clazz.getConstructor(ResourceLocation.class, ResourceLocation.class, String.class);
      IModel patternModel = ModelLoaderRegistry.getModel(baseTextureLoc);
      patternLocation = patternModel.getTextures().iterator().next();
      baseTextureString = patternLocation.toString();
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
          map.setTextureEntry(partPatternTexture);
        }
        else {
          ResourceLocation modelLocation = item.getRegistryName();
          IModel partModel = ModelLoaderRegistry.getModel(new ResourceLocation(modelLocation.getResourceDomain(),
                                                                               "item/parts/" + modelLocation
                                                                                   .getResourcePath()
                                                                               + MaterialModelLoader.EXTENSION));
          ResourceLocation partTexture = partModel.getTextures().iterator().next();

          if(partModel != ModelLoaderRegistry.getMissingModel()) {
            partPatternTexture = constructor.newInstance(partTexture, patternLocation, partPatternLocation);
            if(partModel instanceof IPatternOffset) {
              IPatternOffset offset = (IPatternOffset) partModel;
              ((TextureColoredTexture) partPatternTexture).setOffset(offset.getXOffset(), offset.getYOffset());
            }
            map.setTextureEntry(partPatternTexture);
          }
        }
      } catch(Exception e) {
        log.error(e);
      }
    }

    return baseTextureString;
  }

  public static boolean exists(String res) {
    List<IResource> resources = null;
    try {
      ResourceLocation loc = new ResourceLocation(res);
      loc = new ResourceLocation(loc.getResourceDomain(), "textures/" + loc.getResourcePath() + ".png");
      resources = Minecraft.getMinecraft().getResourceManager().getAllResources(loc);
    } catch(IOException e) {
      return false;
    } finally {
      if (resources != null) {
        for(IResource resource : resources) {
          IOUtils.closeQuietly(resource);
        }
      }
    }
    return true;
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

  public static boolean belongsToToolPart(ResourceLocation location) {
    for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
      if(!(toolpart instanceof Item)) {
        continue; // WHY?!
      }
      try {
        Optional<ResourceLocation> storedResourceLocation = MaterialModelLoader.getToolPartModelLocation(toolpart);
        if(storedResourceLocation.isPresent()) {
          ResourceLocation stored = storedResourceLocation.get();
          ResourceLocation modelLocation = new ResourceLocation(stored.getResourceDomain(), "item/" + stored.getResourcePath());
          IModel partModel = ModelLoaderRegistry.getModel(modelLocation);

          // the actual texture of the part
          ResourceLocation baseTexture = partModel.getTextures().iterator().next();
          if(baseTexture.toString().equals(location.toString())) {
            return true;
          }
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
      public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
        return new GuiOutlineTexture(baseTexture, location);
      }
    });
  }
}
