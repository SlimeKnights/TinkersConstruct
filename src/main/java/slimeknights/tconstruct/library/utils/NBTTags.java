package slimeknights.tconstruct.library.utils;

import slimeknights.tconstruct.library.tools.part.IMaterialItem;

/**
 * Holds all the NBT Tag keys used by the standard tinkers stuff.
 */
public final class NBTTags {
  /** @deprecated use {@link IMaterialItem#MATERIAL_TAG} */
  @Deprecated
  public static final String PART_MATERIAL = IMaterialItem.MATERIAL_TAG;

  /**
   * Tag containing the tank on many items
   * TODO: find better home in 1.19, likely migrate usages to a utility
   */
  public static final String TANK = "tank";

  private NBTTags() {}
}
