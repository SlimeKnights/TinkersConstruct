package slimeknights.tconstruct.library.client.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/** Data generator to create png image files */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericTextureGenerator implements DataProvider {
  private final DataGenerator generator;
  private final String folder;

  /** Saves the given image to the given location */
  @SuppressWarnings("UnstableApiUsage")
  protected void saveImage(HashCache cache, ResourceLocation location, NativeImage image) {
    try {
      Path path = this.generator.getOutputFolder().resolve(
        Paths.get(PackType.CLIENT_RESOURCES.getDirectory(),
                  location.getNamespace(), folder, location.getPath() + ".png"));
      String hash = SHA1.hashBytes(image.asByteArray()).toString();
      if (!Objects.equals(cache.getHash(path), hash) || !Files.exists(path)) {
        Files.createDirectories(path.getParent());
        image.writeToFile(path);
      }
      cache.putNew(path, hash);
    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
