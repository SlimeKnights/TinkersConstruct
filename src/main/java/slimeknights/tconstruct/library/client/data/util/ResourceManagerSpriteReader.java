package slimeknights.tconstruct.library.client.data.util;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

/** Sprite reader pulling from a datapack resource manager */
@RequiredArgsConstructor
public class ResourceManagerSpriteReader extends AbstractSpriteReader {
  private final ResourceManager manager;
  private final String folder;

  /** Gets a location with the given extension */
  private ResourceLocation getLocation(ResourceLocation base, String extension) {
    return base.withPath(folder + '/' + base.getPath() + extension);
  }

  /** Gets a location for .png */
  private ResourceLocation getLocation(ResourceLocation base) {
    return getLocation(base, ".png");
  }

  @Override
  public boolean exists(ResourceLocation path) {
    return manager.getResource(getLocation(path)).isPresent();
  }

  @Override
  public boolean metadataExists(ResourceLocation path) {
    return manager.getResource(getLocation(path, ".png.mcmeta")).isPresent();
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    Resource resource = manager.getResource(getLocation(path)).orElseThrow(FileNotFoundException::new);
    NativeImage image = NativeImage.read(resource.open());
    openedImages.add(image);
    return image;
  }

  @Nullable
  @Override
  public NativeImage readIfExists(ResourceLocation path) {
    Optional<Resource> resource = manager.getResource(getLocation(path));
    if (resource.isPresent()) {
      try {
        NativeImage image = NativeImage.read(resource.get().open());
        openedImages.add(image);
        return image;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  public JsonObject readMetadata(ResourceLocation path) throws IOException {
    try (BufferedReader reader = manager.getResource(getLocation(path, ".png.mcmeta")).orElseThrow(FileNotFoundException::new).openAsReader()) {
      return GsonHelper.parse(reader);
    }
  }
}
