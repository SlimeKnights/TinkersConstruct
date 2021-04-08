package slimeknights.tconstruct.library.traits;

import net.minecraft.util.Identifier;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class TraitId extends Identifier {

  public TraitId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public TraitId(Identifier resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
