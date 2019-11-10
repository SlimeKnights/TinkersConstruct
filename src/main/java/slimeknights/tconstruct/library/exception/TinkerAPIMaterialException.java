package slimeknights.tconstruct.library.exception;

import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;

public class TinkerAPIMaterialException extends TinkerAPIException {

  public static TinkerAPIMaterialException materialStatsTypeRegisteredTwice(MaterialStatType materialStatType) {
    return new TinkerAPIMaterialException("Trying to register the material stats /'" + materialStatType.getIdentifier() + "/', but it has already been registered before");
  }

  public static TinkerAPIMaterialException materialNotRegistered(MaterialStatType materialStatType) {
    return new TinkerAPIMaterialException("The material /'" + materialStatType.getIdentifier() + "/' has not been registered");
  }

  public static TinkerAPIMaterialException corruptedMaterialStats(MaterialStatType materialStatType, IMaterial material, Class<?> invalidStatClass, Class<?> wantedStatClass) {
    return new TinkerAPIMaterialException("Material Stat Registry corrupted!" +
      "The stats of type '" + materialStatType.getIdentifier() + "' registered for material '" + material.getIdentifier() + "' have an invalid class. " +
      "Is '" + invalidStatClass.getCanonicalName() + "' but should be '" + wantedStatClass.getCanonicalName() + "'");
  }

  private TinkerAPIMaterialException(String message) {
    super(message);
  }

  private TinkerAPIMaterialException(String message, Throwable cause) {
    super(message, cause);
  }
}
