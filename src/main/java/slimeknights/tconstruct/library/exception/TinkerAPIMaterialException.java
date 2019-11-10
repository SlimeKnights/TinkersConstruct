package slimeknights.tconstruct.library.exception;

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.materials.IMaterial;

public class TinkerAPIMaterialException extends TinkerAPIException {

  public static TinkerAPIMaterialException materialStatsTypeRegisteredTwice(ResourceLocation materialStatType) {
    return new TinkerAPIMaterialException("Trying to register the material stats '" + materialStatType + "', but it has already been registered before");
  }

  public static TinkerAPIMaterialException materialNotRegistered(ResourceLocation materialStatType) {
    return new TinkerAPIMaterialException("The material '" + materialStatType + "' has not been registered");
  }

  public static TinkerAPIMaterialException corruptedMaterialStats(ResourceLocation materialStatType, IMaterial material, Class<?> invalidStatClass, Class<?> wantedStatClass) {
    return new TinkerAPIMaterialException("Material Stat Registry corrupted!" +
      "The stats of type '" + materialStatType + "' registered for material '" + material.getIdentifier() + "' have an invalid class. " +
      "Is '" + invalidStatClass.getCanonicalName() + "' but should be '" + wantedStatClass.getCanonicalName() + "'");
  }

  private TinkerAPIMaterialException(String message) {
    super(message);
  }

  private TinkerAPIMaterialException(String message, Throwable cause) {
    super(message, cause);
  }
}
