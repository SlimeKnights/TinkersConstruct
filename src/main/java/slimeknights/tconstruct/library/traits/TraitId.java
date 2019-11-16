package slimeknights.tconstruct.library.traits;

import net.minecraft.util.ResourceLocation;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class TraitId extends ResourceLocation {

  public TraitId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public TraitId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
