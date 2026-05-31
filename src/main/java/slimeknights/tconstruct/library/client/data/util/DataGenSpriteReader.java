package slimeknights.tconstruct.library.client.data.util;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

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
  public boolean metadataExists(ResourceLocation path) {
    return existingFileHelper.exists(path, PackType.CLIENT_RESOURCES, ".png.mcmeta", folder);
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    try {
      Resource resource = existingFileHelper.getResource(path, PackType.CLIENT_RESOURCES, ".png", folder);
      NativeImage image = NativeImage.read(resource.open());
      openedImages.add(image);
      return image;
    } catch (IOException|NoSuchElementException e) {
      log.error("Failed to read image at {}", path);
      throw e;
    }
  }

  @Override
  public JsonObject readMetadata(ResourceLocation path) throws IOException {
    try (BufferedReader reader = existingFileHelper.getResource(path, PackType.CLIENT_RESOURCES, ".png.mcmeta", folder).openAsReader()) {
      return GsonHelper.parse(reader);
    }
  }
}
