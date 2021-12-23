package slimeknights.tconstruct.library.client.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/** Data generator to create png image files */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericTextureGenerator implements IDataProvider {
  private final DataGenerator generator;
  private final String folder;

  /** Saves the given image to the given location */
  @SuppressWarnings("UnstableApiUsage")
  protected void saveImage(DirectoryCache cache, ResourceLocation location, NativeImage image) {
    try {
      Path path = this.generator.getOutputFolder().resolve(
        Paths.get(ResourcePackType.CLIENT_RESOURCES.getDirectoryName(),
                  location.getNamespace(), folder, location.getPath() + ".png"));
      String hash = HASH_FUNCTION.hashBytes(image.getBytes()).toString();
      if (!Objects.equals(cache.getPreviousHash(path), hash) || !Files.exists(path)) {
        Files.createDirectories(path.getParent());
        image.write(path);
      }
      cache.recordHash(path, hash);
    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
