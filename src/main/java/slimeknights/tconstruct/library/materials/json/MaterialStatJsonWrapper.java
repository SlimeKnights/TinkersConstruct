package slimeknights.tconstruct.library.materials.json;

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This json is mostly used for automatic consistency checks and for easier deserialization.
 * The actual stats deserialization is done in {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager}
 */
public class MaterialStatJsonWrapper {

  private final ResourceLocation materialId;
  private final List<BaseMaterialStatsJson> stats;

  public MaterialStatJsonWrapper(ResourceLocation materialId, List<BaseMaterialStatsJson> stats) {
    this.materialId = materialId;
    this.stats = stats;
  }

  @Nullable
  public List<BaseMaterialStatsJson> getStats() {
    return stats;
  }

  @Nullable
  public MaterialId getMaterialId() {
    return materialId != null ? new MaterialId(materialId) : null;
  }

  public static class BaseMaterialStatsJson {
    private final ResourceLocation id;

    protected BaseMaterialStatsJson(ResourceLocation id) {
      this.id = id;
    }

    @Nullable
    public ResourceLocation getId() {
      return id;
    }
  }
}
