package slimeknights.tconstruct.library.data;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Generic data generator dumping raw text to a file.
 * @see slimeknights.mantle.data.GenericDataProvider
 */
@RequiredArgsConstructor
public abstract class GenericStringProvider implements DataProvider {
  protected final PackOutput.PathProvider pathProvider;
  private final String folder;
  private final String extension;

  public GenericStringProvider(PackOutput output, Target type, String folder, String extension) {
    this(output.createPathProvider(type, folder), folder, extension);
  }

  public GenericStringProvider(DataGenerator generator, Target type, String folder, String extension) {
    this(generator.getPackOutput(), type, folder, extension);
  }

  /** Localizes the given resource to the folder */
  public ResourceLocation localize(ResourceLocation name) {
    return JsonHelper.localize(name, folder, '.' + extension);
  }

  /** Saves the given string to the given location */
  protected CompletableFuture<?> saveString(CachedOutput cache, ResourceLocation location, String data) {
    return CompletableFuture.runAsync(() -> {
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(data.getBytes());
        byte[] bytes = outputStream.toByteArray();
        Path outputPath = this.pathProvider.file(location, extension);
        cache.writeIfNeeded(outputPath, bytes, Hashing.sha1().hashBytes(bytes));
      } catch (IOException e) {
        TConstruct.LOG.error("Couldn't write string for {}", location, e);
        throw new CompletionException(e);
      }
    }, Util.backgroundExecutor());
  }
}
