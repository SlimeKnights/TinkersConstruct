package slimeknights.tconstruct.library.client.data;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Data generator to create png image files */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericTextureGenerator implements DataProvider {
  private final DataGenerator generator;
  private final String folder;

  /** Saves the given image to the given location */
  @SuppressWarnings("UnstableApiUsage")
  protected void saveImage(CachedOutput cache, ResourceLocation location, NativeImage image) {
    try {
      Path path = this.generator.getOutputFolder().resolve(Paths.get(PackType.CLIENT_RESOURCES.getDirectory(), location.getNamespace(), folder, location.getPath() + ".png"));
      byte[] bytes = image.asByteArray();
      cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
    } catch (IOException e) {
      log.error("Couldn't write image for {}", location, e);
    }
  }
}
