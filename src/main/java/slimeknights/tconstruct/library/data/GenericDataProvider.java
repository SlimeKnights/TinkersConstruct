package slimeknights.tconstruct.library.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Generic logic to convert any serializable object into JSON
 */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericDataProvider implements IDataProvider {
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  protected final DataGenerator generator;
  private final ResourcePackType type;
  private final String folder;
  private final Gson gson;

  public GenericDataProvider(DataGenerator generator, ResourcePackType type, String folder) {
    this(generator, type, folder, GSON);
  }

  public GenericDataProvider(DataGenerator generator, String folder, Gson gson) {
    this(generator, ResourcePackType.SERVER_DATA, folder, gson);
  }

  public GenericDataProvider(DataGenerator generator, String folder) {
    this(generator, folder, GSON);
  }

  @SuppressWarnings("UnstableApiUsage")
  protected void saveThing(DirectoryCache cache, ResourceLocation location, Object materialJson) {
    try {
      String json = gson.toJson(materialJson);
      Path path = this.generator.getOutputFolder().resolve(Paths.get(type.getDirectoryName(), location.getNamespace(), folder, location.getPath() + ".json"));
      String hash = HASH_FUNCTION.hashUnencodedChars(json).toString();
      if (!Objects.equals(cache.getPreviousHash(path), hash) || !Files.exists(path)) {
        Files.createDirectories(path.getParent());

        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
          bufferedwriter.write(json);
        }
      }

      cache.recordHash(path, hash);
    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
