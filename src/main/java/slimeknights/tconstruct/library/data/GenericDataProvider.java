package slimeknights.tconstruct.library.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
public abstract class GenericDataProvider implements DataProvider {

  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  protected final DataGenerator generator;
  private final String folder;
  private final Gson gson;

  public GenericDataProvider(DataGenerator generator, String folder) {
    this(generator, folder, GSON);
  }

  protected void saveThing(DataCache cache, Identifier location, Object materialJson) {
    try {
      String json = gson.toJson(materialJson);
      Path path = this.generator.getOutput().resolve(Paths.get("data", location.getNamespace(), folder, location.getPath() + ".json"));
      String hash = SHA1.hashUnencodedChars(json).toString();
      if (!Objects.equals(cache.getOldSha1(path), hash) || !Files.exists(path)) {
        Files.createDirectories(path.getParent());

        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
          bufferedwriter.write(json);
        }
      }

      cache.updateSha1(path, hash);
    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
