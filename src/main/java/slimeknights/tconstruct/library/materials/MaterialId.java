package slimeknights.tconstruct.library.materials;

import net.minecraft.util.ResourceLocation;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialId extends ResourceLocation {

  public MaterialId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
