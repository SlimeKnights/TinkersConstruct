package slimeknights.tconstruct.library.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Alternative to {@link net.minecraft.client.resources.JsonReloadListener} that merges all json into a single builder rather than taking the top most JSON
 * @param <B>  Builder class
 */
@RequiredArgsConstructor
@Log4j2
public abstract class MergingJsonDataLoader<B> implements IResourceManagerReloadListener {
  private static final int JSON_LENGTH = ".json".length();

  @VisibleForTesting
  protected final Gson gson;
  @VisibleForTesting
  protected final String folder;
  @VisibleForTesting
  protected final Function<ResourceLocation,B> builderConstructor;

  /**
   * Parses a particular JSON into the builder
   * @param builder   Builder object
   * @param id        ID of json being parsed
   * @param element   JSON data
   * @throws JsonSyntaxException  If the json failed to parse
   */
  protected abstract void parse(B builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException;

  /**
   * Called when the JSON finished parsing to handle the final map
   * @param map      Map of data
   * @param manager  Resource manager
   */
  protected abstract void finishLoad(Map<ResourceLocation,B> map, IResourceManager manager);

  @Override
  public void onResourceManagerReload(IResourceManager manager) {
    Map<ResourceLocation,B> map = new HashMap<>();
    for (ResourceLocation filePath : manager.getAllResourceLocations(folder, fileName -> fileName.endsWith(".json"))) {
      String path = filePath.getPath();
      ResourceLocation id = new ResourceLocation(filePath.getNamespace(), path.substring(folder.length() + 1, path.length() - JSON_LENGTH));

      try {
        for (IResource resource : manager.getAllResources(filePath)) {
          try (
            InputStream inputstream = resource.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
          ) {
            JsonElement json = JSONUtils.fromJson(gson, reader, JsonElement.class);
            if (json == null) {
              log.error("Couldn't load data file {} from {} in data pack {} as its null or empty", id, filePath, resource.getPackName());
            } else {
              B builder = map.computeIfAbsent(id, builderConstructor);
              parse(builder, id, json);
            }
          } catch (RuntimeException | IOException ex) {
            log.error("Couldn't parse data file {} from {} in data pack {}", id, filePath, resource.getPackName(), ex);
          } finally {
            IOUtils.closeQuietly(resource);
          }
        }
      } catch (IOException ex) {
        log.error("Couldn't read material trait mapping {} from {}", id, filePath, ex);
      }
    }
    finishLoad(map, manager);
  }
}
