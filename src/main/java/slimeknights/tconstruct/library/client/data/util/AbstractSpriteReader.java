package slimeknights.tconstruct.library.client.data.util;

import lombok.RequiredArgsConstructor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Base sprite reader implementation for both datagen and the command */
@RequiredArgsConstructor
public abstract class AbstractSpriteReader {
  protected final List<NativeImage> openedImages = new ArrayList<>();

  /** Checks if an image exists in the given location */
  public abstract boolean exists(ResourceLocation path);

  /**
   * Reads an image at the given path, relative to the folder
   * @param path  Path containing the file
   * @return  Loaded image
   * @throws IOException  If the image failed to load
   */
  public abstract NativeImage read(ResourceLocation path) throws IOException;

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

  /** Tracks the given image so when this reader is closed, that image is closed */
  public void track(NativeImage transformed) {
    openedImages.add(transformed);
  }

  /** Closes all opened images */
  public void closeAll() {
    for (NativeImage image : openedImages) {
      image.close();
    }
    openedImages.clear();
  }
}
