package slimeknights.tconstruct.library.client.data.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;

/**
 * Logic to read sprites from existing images and return native images which can later be modified
 */
@Log4j2
@RequiredArgsConstructor
public class DataGenSpriteReader extends AbstractSpriteReader {
  private final ExistingFileHelper existingFileHelper;
  private final String folder;

  @Override
  public boolean exists(ResourceLocation path) {
    return existingFileHelper.exists(path, ResourcePackType.CLIENT_RESOURCES, ".png", folder);
  }

  @Override
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
}
