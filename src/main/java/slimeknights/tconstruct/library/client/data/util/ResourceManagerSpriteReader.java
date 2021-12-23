package slimeknights.tconstruct.library.client.data.util;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/** Sprite reader pulling from a datapack resource manager */
@RequiredArgsConstructor
public class ResourceManagerSpriteReader extends AbstractSpriteReader {
  private final IResourceManager manager;
  private final String folder;

  private ResourceLocation getLocation(ResourceLocation base) {
    return new ResourceLocation(base.getNamespace(), folder + "/" + base.getPath() + ".png");
  }

  @Override
  public boolean exists(ResourceLocation path) {
    return manager.hasResource(getLocation(path));
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    IResource resource = manager.getResource(getLocation(path));
    NativeImage image = NativeImage.read(resource.getInputStream());
    openedImages.add(image);
    return image;
  }
}
