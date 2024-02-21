package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.utils.IdParser;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialStatsId extends ResourceLocation {
  public static final IdParser<MaterialStatsId> PARSER = new IdParser<>(MaterialStatsId::new, "Material Stat Type");

  public MaterialStatsId(String text) {
    super(text);
  }

  public MaterialStatsId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialStatsId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }
}
