package slimeknights.mantle.data.listener;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.tconstruct.test.JsonFileLoader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.mockito.Mockito.mock;

/**
 * Extension of {@link JsonFileLoader} with extra functionality to mock multiple data packs
 * @param <B>  Builder type
 */
public class MergingJsonFileLoader<B> extends JsonFileLoader {
  private final MergingJsonDataLoader<B> dataLoader;

  public MergingJsonFileLoader(MergingJsonDataLoader<B> dataLoader) {
    super(dataLoader.gson, dataLoader.folder);
    this.dataLoader = dataLoader;
  }

  /**
   * Loads and parses the relevant files into the data loader
   * @param mergeFolder  If nonnull, subfolder to load as a "second datapack", for testing merging behavior. If null, skips the merging
   * @param files  List of files
   */
  public void loadAndParseFiles(@Nullable String mergeFolder, ResourceLocation... files) {
    Map<ResourceLocation,B> parsedMap = new HashMap<>();
    for (Entry<ResourceLocation, JsonElement> entry : loadFilesAsSplashlist(files).entrySet()) {
      ResourceLocation id = entry.getKey();
      dataLoader.parse(parsedMap.computeIfAbsent(id, dataLoader.builderConstructor), id, entry.getValue());
    }
    if (mergeFolder != null) {
      JsonFileLoader fakeSecondDataPack = new JsonFileLoader(dataLoader.gson, dataLoader.folder + "/" + mergeFolder);
      for (Entry<ResourceLocation, JsonElement> entry : fakeSecondDataPack.loadFilesAsSplashlist(files).entrySet()) {
        ResourceLocation id = entry.getKey();
        dataLoader.parse(parsedMap.computeIfAbsent(id, dataLoader.builderConstructor), id, entry.getValue());
      }
    }
    dataLoader.finishLoad(parsedMap, mock(ResourceManager.class));
  }
}
