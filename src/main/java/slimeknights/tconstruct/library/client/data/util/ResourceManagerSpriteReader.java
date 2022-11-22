package slimeknights.tconstruct.library.client.data.util;

import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;

import java.io.IOException;

/** Sprite reader pulling from a datapack resource manager */
@RequiredArgsConstructor
public class ResourceManagerSpriteReader extends AbstractSpriteReader {
  private final ResourceManager manager;
  private final String folder;

  private ResourceLocation getLocation(ResourceLocation base) {
    return new ResourceLocation(base.getNamespace(), folder + "/" + base.getPath() + ".png");
  }

  @Override
  public boolean exists(ResourceLocation path) {
    return DynamicTextureLoader.textureExists(manager, path);
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    Resource resource = manager.getResource(getLocation(path));
    NativeImage image = NativeImage.read(resource.getInputStream());
    openedImages.add(image);
    return image;
  }
}
