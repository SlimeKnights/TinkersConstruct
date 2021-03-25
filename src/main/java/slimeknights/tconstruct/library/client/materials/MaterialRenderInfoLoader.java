package slimeknights.tconstruct.library.client.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.IEarlySafeManagerReloadListener;
import slimeknights.tconstruct.library.materials.MaterialId;

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
 * The location inside resource packs is "toolmaterials".
 * So if your mods name is "foobar", the location for your mods materials is "assets/foobar/toolmaterials".
 */
@Log4j2
public class MaterialRenderInfoLoader implements IEarlySafeManagerReloadListener {
  public static final MaterialRenderInfoLoader INSTANCE = new MaterialRenderInfoLoader();

  /** Folder to scan for material render info JSONS */
  private static final String FOLDER = "models/tool_materials";
  /** GSON adapter for material info deserializing */
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /**
   * Called on mod construct to register the resource listener
   */
  public static void addResourceListener(IReloadableResourceManager manager)  {
    manager.addReloadListener(INSTANCE);
  }

  /** Map of all loaded materials */
  private Map<MaterialId,MaterialRenderInfo> renderInfos = ImmutableMap.of();

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
   * @param materialId  Material loaded
   * @return  Material render info
   */
  public Optional<MaterialRenderInfo> getRenderInfo(MaterialId materialId) {
    return Optional.ofNullable(renderInfos.get(materialId));
  }

  @Override
  public void onReloadSafe(IResourceManager manager) {
    // first, we need to fetch all relevant JSON files
    int trim = FOLDER.length() + 1;
    Map<MaterialId,MaterialRenderInfo> map = new HashMap<>();
    for(ResourceLocation location : manager.getAllResourceLocations(FOLDER, (loc) -> loc.endsWith(".json"))) {
      // clean up ID by trimming off the extension
      String path = location.getPath();
      MaterialId id = new MaterialId(location.getNamespace(), path.substring(trim, path.length() - 5));

      // read in the JSON data
      try (
        IResource iresource = manager.getResource(location);
        InputStream inputstream = iresource.getInputStream();
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
   * @param loc   Material location
   * @param json  Render info JSON data
   * @return  Material render info data
   */
  private MaterialRenderInfo loadRenderInfo(ResourceLocation loc, MaterialRenderInfoJson json) {
    // parse color
    int color = 0xFFFFFFFF;
    if (json.getColor() != null) {
      color = Integer.parseInt(json.getColor(), 16);
      if((color & 0xFF000000) == 0) {
        color |= 0xFF000000;
      }
    }

    MaterialId id = new MaterialId(loc);
    ResourceLocation texture = json.getTexture();
    if (texture == null) {
      texture = id;
    }
    String[] fallback = json.getFallbacks();
    if (fallback == null) {
      fallback = new String[0];
    }
    return new MaterialRenderInfo(id, texture, fallback, color);
  }
}
