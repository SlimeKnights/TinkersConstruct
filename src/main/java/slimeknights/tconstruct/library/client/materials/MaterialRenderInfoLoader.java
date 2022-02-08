package slimeknights.tconstruct.library.client.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import slimeknights.mantle.data.IEarlySafeManagerReloadListener;
import slimeknights.mantle.data.ResourceLocationSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Loads the material render info from resource packs. Loaded independently of materials loaded in data packs, so a resource needs to exist in both lists to be used.
 * See {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager} for stats.
 * <p>
 * The location inside resource packs is "tinkering/materials".
 * So if your mods name is "foobar", the location for your mods materials is "assets/foobar/tinkering/materials".
 */
@Log4j2
public class MaterialRenderInfoLoader implements IEarlySafeManagerReloadListener {
  public static final MaterialRenderInfoLoader INSTANCE = new MaterialRenderInfoLoader();

  /** Folder to scan for material render info JSONS */
  public static final String FOLDER = "tinkering/materials";
  /** GSON adapter for material info deserializing */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(MaterialStatsId.class, new ResourceLocationSerializer<>(MaterialStatsId::new, TConstruct.MOD_ID))
    .registerTypeHierarchyAdapter(ISpriteTransformer.class, ISpriteTransformer.SERIALIZER)
    .registerTypeHierarchyAdapter(IColorMapping.class, IColorMapping.SERIALIZER)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /**
   * Called on mod construct to register the resource listener
   */
  public static void addResourceListener(RegisterClientReloadListenersEvent manager)  {
    manager.registerReloadListener(INSTANCE);
  }

  /** Map of all loaded materials */
  private Map<MaterialVariantId,MaterialRenderInfo> renderInfos = ImmutableMap.of();

  private MaterialRenderInfoLoader() {}

  /**
   * Gets a list of all loaded materials render infos
   * @return  All loaded material render infos
   */
  public Collection<MaterialRenderInfo> getAllRenderInfos() {
    return renderInfos.values();
  }

  /**
   * Gets the render info for the given material
   * @param variantId  Material loaded
   * @return  Material render info
   */
  public Optional<MaterialRenderInfo> getRenderInfo(MaterialVariantId variantId) {
    // if there is a variant, try fetching for the variant
    if (variantId.hasVariant()) {
      MaterialRenderInfo info = renderInfos.get(variantId);
      if (info != null) {
        return Optional.of(info);
      }
    }
    // no variant or the variant was not found? default to the material
    return Optional.ofNullable(renderInfos.get(variantId.getId()));
  }

  @Override
  public void onReloadSafe(ResourceManager manager) {
    // first, we need to fetch all relevant JSON files
    int trim = FOLDER.length() + 1;
    Map<MaterialVariantId,MaterialRenderInfo> map = new HashMap<>();
    for(ResourceLocation location : manager.listResources(FOLDER, (loc) -> loc.endsWith(".json"))) {
      // clean up ID by trimming off the extension and folder
      String path = location.getPath();
      String localPath = path.substring(trim, path.length() - 5);

      // locate variant as a subfolder, and create final ID
      String variant = "";
      int slashIndex = localPath.lastIndexOf('/');
      if (slashIndex >= 0) {
        variant = localPath.substring(slashIndex + 1);
        localPath = localPath.substring(0, slashIndex);
      }
      MaterialVariantId id = MaterialVariantId.create(location.getNamespace(), localPath, variant);

      // read in the JSON data
      try (
        Resource resource = manager.getResource(location);
        InputStream inputstream = resource.getInputStream();
        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
      ) {
        MaterialRenderInfoJson json = GSON.fromJson(reader, MaterialRenderInfoJson.class);
        if (json == null) {
          log.error("Couldn't load data file {} from {} as it's null or empty", id, location);
        } else {
          // parse it into material render info
          MaterialRenderInfo old = map.put(id, loadRenderInfo(id, json));
          if (old != null) {
            throw new IllegalStateException("Duplicate data file ignored with ID " + id);
          }
        }
      } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
        log.error("Couldn't parse data file {} from {}", id, location, jsonparseexception);
      }
    }
    // store the list immediately, otherwise it is not in place in time for models to load
    this.renderInfos = map;
    log.debug("Loaded material render infos: {}", Util.toIndentedStringList(map.keySet()));
    log.info("{} material render infos loaded", map.size());
  }

  /**
   * Gets material render info based on the given JSON
   * @param material   Material location
   * @param json  Render info JSON data
   * @return  Material render info data
   */
  private MaterialRenderInfo loadRenderInfo(MaterialVariantId material, MaterialRenderInfoJson json) {
    // parse color
    int color = 0xFFFFFFFF;
    if (json.getColor() != null) {
      color = JsonHelper.parseColor(json.getColor());
    }

    // texture fallback to ID if not told to skip
    ResourceLocation texture = null;
    if (!json.isSkipUniqueTexture()) {
      texture = json.getTexture();
      if (texture == null) {
        texture = material.getLocation('_');
      }
    }
    // list of fallback textures
    String[] fallback = json.getFallbacks();
    if (fallback == null) {
      fallback = new String[0];
    }
    return new MaterialRenderInfo(material, texture, fallback, color, json.getLuminosity());
  }
}
