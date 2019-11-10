package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * This json is mostly used for automatic consistency checks and for easier deserialization.
 * The actual stats deserialization is done in {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager}
 */
public class MaterialStatJson {

  private final ResourceLocation materialId;
  private final List<MaterialStatsWrapper> stats;

  public MaterialStatJson(ResourceLocation materialId, List<MaterialStatsWrapper> stats) {
    this.materialId = materialId;
    this.stats = stats;
  }

  public ResourceLocation getMaterialId() {
    return materialId;
  }

  public List<MaterialStatsWrapper> getStats() {
    return stats;
  }

  public static class MaterialStatsWrapper {
    private final ResourceLocation id;

    private MaterialStatsWrapper(ResourceLocation id) {
      this.id = id;
    }

    public ResourceLocation getId() {
      return id;
    }
  }
}
