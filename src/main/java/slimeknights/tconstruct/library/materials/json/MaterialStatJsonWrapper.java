package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * This json is mostly used for automatic consistency checks and for easier deserialization.
 * The actual stats deserialization is done in {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager}
 */
public class MaterialStatJsonWrapper {

  private final List<BaseMaterialStatsJson> stats;

  public MaterialStatJsonWrapper(List<BaseMaterialStatsJson> stats) {
    this.stats = stats;
  }

  public List<BaseMaterialStatsJson> getStats() {
    return stats;
  }

  public static class BaseMaterialStatsJson {
    private final ResourceLocation id;

    private BaseMaterialStatsJson(ResourceLocation id) {
      this.id = id;
    }

    public ResourceLocation getId() {
      return id;
    }
  }
}
