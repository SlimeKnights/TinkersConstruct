package slimeknights.tconstruct.library.exception;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

// TODO 1.19: reevaluate whether this is actually needed
public class TinkerAPIMaterialException extends TinkerAPIException {

  public static TinkerAPIMaterialException materialStatsTypeRegisteredTwice(ResourceLocation materialStatType) {
    return new TinkerAPIMaterialException("Trying to register the material stats '" + materialStatType + "', but it has already been registered before");
  }

  public static TinkerAPIMaterialException materialStatTypeFallbackRegisteredTwice(ResourceLocation materialStatType) {
    return new TinkerAPIMaterialException("Trying to register fallback for material stats '" + materialStatType + "', but it has already been registered before");
  }

  public static TinkerAPIMaterialException materialNotRegistered(ResourceLocation materialStatType) {
    return new TinkerAPIMaterialException("The material '" + materialStatType + "' has not been registered");
  }

  public static TinkerAPIMaterialException corruptedMaterialStats(ResourceLocation materialStatType, IMaterial material, Class<?> invalidStatClass, Class<?> wantedStatClass) {
    return new TinkerAPIMaterialException("Material Stat Registry corrupted!" +
      "The stats of type '" + materialStatType + "' registered for material '" + material.getIdentifier() + "' have an invalid class. " +
      "Is '" + invalidStatClass.getCanonicalName() + "' but should be '" + wantedStatClass.getCanonicalName() + "'");
  }

  public static TinkerAPIMaterialException statBuilderWithInvalidMaterialCount() {
    return new TinkerAPIMaterialException(
      "Somehow something is trying to build a tool with a non-matching amount of materials. " +
        "Number of materials and required parts differ. " +
        "This should never happen and is a bug in whatever called this in the stacktrace.");
  }

  private TinkerAPIMaterialException(String message) {
    super(message);
  }

  private TinkerAPIMaterialException(String message, Throwable cause) {
    super(message, cause);
  }
}
