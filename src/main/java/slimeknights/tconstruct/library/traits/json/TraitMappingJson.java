package slimeknights.tconstruct.library.traits.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public class TraitMappingJson {

  @SerializedName("default")
  private final List<ResourceLocation> defaultTraits;
  private final Map<ResourceLocation, List<ResourceLocation>> perStat;

  public TraitMappingJson(List<ResourceLocation> defaultTraits, Map<ResourceLocation, List<ResourceLocation>> perStat) {
    this.defaultTraits = defaultTraits;
    this.perStat = perStat;
  }

  public List<ResourceLocation> getDefaultTraits() {
    return defaultTraits;
  }

  public Map<ResourceLocation, List<ResourceLocation>> getPerStat() {
    return perStat;
  }
}
