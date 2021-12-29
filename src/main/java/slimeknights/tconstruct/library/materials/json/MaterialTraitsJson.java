package slimeknights.tconstruct.library.materials.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Json for serializing and deserializing material traits */
@RequiredArgsConstructor
public class MaterialTraitsJson {
  @SerializedName("default")
  @Getter
  @Nullable
  private final List<ModifierEntry> defaultTraits;
  @Nullable
  private final Map<ResourceLocation, List<ModifierEntry>> perStat;

  public Map<MaterialStatsId,List<ModifierEntry>> getPerStat() {
    if (perStat == null) {
      return Collections.emptyMap();
    }
    Map<MaterialStatsId,List<ModifierEntry>> newMap = new HashMap<>(perStat.size());
    perStat.forEach((key, value) -> newMap.put(new MaterialStatsId(key), value));
    return newMap;
  }
}
