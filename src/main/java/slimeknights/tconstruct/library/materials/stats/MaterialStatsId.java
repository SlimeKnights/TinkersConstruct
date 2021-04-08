package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.util.Identifier;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialStatsId extends Identifier {

  public MaterialStatsId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialStatsId(Identifier resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
