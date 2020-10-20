package slimeknights.tconstruct.library.modifiers;

import net.minecraft.util.ResourceLocation;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class ModifierId extends ResourceLocation {

  public ModifierId(String resourceName) {
    super(resourceName);
  }

  public ModifierId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public ModifierId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
