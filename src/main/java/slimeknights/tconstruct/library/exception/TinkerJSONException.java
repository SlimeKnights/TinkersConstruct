package slimeknights.tconstruct.library.exception;

import net.minecraft.resources.ResourceLocation;

// TODO 1.19: reevaluate whether this is actually needed
public class TinkerJSONException extends TinkerAPIException {

  public static TinkerJSONException materialJsonWithoutCraftingInformation(ResourceLocation materialId) {
    return new TinkerJSONException("Malformed JSON for the material '" + materialId + "'. Missing craftable information.");
  }

  public static TinkerJSONException materialStatsJsonWithoutMaterial() {
    return new TinkerJSONException("Malformed JSON for the stats. Missing material id which the stats belong to.");
  }

  public static TinkerJSONException materialTraitsJsonWithoutMaterial(ResourceLocation file) {
    return new TinkerJSONException("Malformed JSON for traits in file " + file + ". Missing material id which the traits belong to.");
  }

  public static TinkerJSONException materialStatsJsonWithoutId() {
    return new TinkerJSONException("Malformed JSON for stats. Missing stat id information.");
  }

  private TinkerJSONException(String message) {
    super(message);
  }

  private TinkerJSONException(String message, Throwable cause) {
    super(message, cause);
  }
}
