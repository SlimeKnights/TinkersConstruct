package slimeknights.tconstruct.library.client.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Logic to read sprites from existing images and return native images which can later be modified
 */
@Log4j2
@RequiredArgsConstructor
public class SpriteReader {
  private final ExistingFileHelper existingFileHelper;
  private final String folder;
  private final List<NativeImage> openedImages = new ArrayList<>();

  /** Checks if an image exists in the given location */
  public boolean exists(ResourceLocation path) {
    return existingFileHelper.exists(path, ResourcePackType.CLIENT_RESOURCES, ".png", folder);
  }

  /**
   * Reads an image at the given path, relative to the folder
   * @param path  Path containing the file
   * @return  Loaded image
   * @throws IOException  If the image failed to load
   */
  public NativeImage read(ResourceLocation path) throws IOException {
    try {
      IResource resource = existingFileHelper.getResource(path, ResourcePackType.CLIENT_RESOURCES, ".png", folder);
      NativeImage image = NativeImage.read(resource.getInputStream());
      openedImages.add(image);
      return image;
    } catch (IOException e) {
      log.error("Failed to read image at {}", path);
      throw e;
    }
  }

  /** Reads the file if it exists */
  @Nullable
  public NativeImage readIfExists(ResourceLocation path) {
    if (exists(path)) {
      try {
        return read(path);
      } catch (IOException e) {
        // no-op should never happen
      }
    }
    return null;
  }

  /** Closes all opened images */
  public void closeAll() {
    for (NativeImage image : openedImages) {
      image.close();
    }
    openedImages.clear();
  }
}
