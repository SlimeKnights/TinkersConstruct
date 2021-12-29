package slimeknights.tconstruct.library.client.data.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;
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
    return existingFileHelper.exists(path, PackType.CLIENT_RESOURCES, ".png", folder);
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    try {
      Resource resource = existingFileHelper.getResource(path, PackType.CLIENT_RESOURCES, ".png", folder);
      NativeImage image = NativeImage.read(resource.getInputStream());
      openedImages.add(image);
      return image;
    } catch (IOException e) {
      log.error("Failed to read image at {}", path);
      throw e;
    }
  }
}
