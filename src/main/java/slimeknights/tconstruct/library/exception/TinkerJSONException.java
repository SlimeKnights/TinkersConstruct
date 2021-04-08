package slimeknights.tconstruct.library.exception;

import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.TinkerAPIException;

public class TinkerJSONException extends TinkerAPIException {

  public static TinkerJSONException materialJsonWithoutCraftingInformation(Identifier materialId) {
    return new TinkerJSONException("Malformed JSON for the material '" + materialId + "'. Missing craftable information.");
  }

  public static TinkerJSONException materialStatsJsonWithoutMaterial() {
    return new TinkerJSONException("Malformed JSON for the stats. Missing material id which the stats belong to.");
  }

  public static TinkerJSONException materialTraitsJsonWithoutMaterial(Identifier file) {
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
