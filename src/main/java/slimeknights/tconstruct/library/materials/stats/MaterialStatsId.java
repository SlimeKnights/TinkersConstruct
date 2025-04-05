package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialStatsId extends ResourceId {
  public static final IdParser<MaterialStatsId> PARSER = new IdParser<>(MaterialStatsId::new, "Material Stat Type");

  public MaterialStatsId(String text) {
    super(text);
  }

  public MaterialStatsId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialStatsId(ResourceLocation location) {
    super(location);
  }

  private MaterialStatsId(String namespace, String path, @Nullable Dummy pDummy) {
    super(namespace, path, pDummy);
  }

  /** Checks if the given material can be used */
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getId(), this).isPresent();
  }


  /** {@return Material Stats ID, or null if invalid} */
  @Nullable
  public static MaterialStatsId tryParse(String string) {
    return tryParse(string, (namespace, path) -> new MaterialStatsId(namespace, path, null));
  }

  /** {@return Material Stats ID, or null if invalid} */
  @Nullable
  public static MaterialStatsId tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, (n, p) -> new MaterialStatsId(namespace, path, null));
  }
}
