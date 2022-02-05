package slimeknights.tconstruct.library.materials.json;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

/**
 * This json is mostly used for automatic consistency checks and for easier deserialization.
 * The actual stats deserialization is done in {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager}
 */
@RequiredArgsConstructor
public class MaterialStatJson {
  @Nullable
  private final Map<ResourceLocation, JsonObject> stats;

  public Map<ResourceLocation, JsonObject> getStats() {
    if (stats == null) {
      return Collections.emptyMap();
    }
    return stats;
  }
}
