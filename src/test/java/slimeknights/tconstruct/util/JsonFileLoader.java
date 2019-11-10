package slimeknights.tconstruct.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;

public class JsonFileLoader {

  private final String basePath = "data/tconstruct";

  private final Gson gson;
  private final String folder;

  public JsonFileLoader(Gson gson, String folder) {
    this.gson = gson;
    this.folder = folder;
  }

  public JsonObject loadJson(ResourceLocation file) {
    return loadJson(file.getNamespace(), file.getPath());
  }

  public JsonObject loadJson(String namespace, String filename) {
    String path = Paths.get("data", namespace, folder, filename + ".json").toString();
    URL resource = gson.getClass().getClassLoader().getResource(path);
    if(resource == null) {
      throw new IllegalArgumentException("Resource with path " + path + " doesn't exist");
    }
    try (
      InputStream inputstream = resource.openStream();
      Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
    ) {
      return Objects.requireNonNull(JSONUtils.fromJson(gson, reader, JsonObject.class));
    } catch (IOException e) {
      // wrap in runtime exception since it's test only, so we don't have to declare throws on all tests
      throw new RuntimeException(e);
    }
  }
}
