package tconstruct.library.utils;

/**
 * Holds all the NBT Tag keys used by the standard tinkers stuff.
 */
public final class Tags {

  /** The base data of the tinker item. What it is built from. */
  public static final String BASE_DATA = "TinkerData";
  /** Extra-data that is specific to this Itemstack and is used to build the item.
   * An example would be if a special pickaxe had 100 more durability, it'd be stored in here.
   */
  public static final String TINKER_EXTRA = "Special";

  //public static final String TOOL_RENDER = "Render";
  /** The tag that contains all the actual calculated runtime-information of the tools */
  public static final String TOOL_DATA = "Tinker";
  /** Contains all the applied modifiers */
  public static final String TOOL_MODIFIERS = "Modifiers";

  /** The tag that saves the material information on toolparts */
  public static final String PART_MATERIAL = "Material";

  public static final String DURABILITY = "Durability";
  public static final String ATTACK = "Attack";
  public static final String MININGSPEED = "MiningSpeed";
  public static final String HARVESTLEVEL = "HarvestLevel";

  public static final String MODIFIERS = "Modifiers";

  public static final String BROKEN = "Broken";

  private Tags() {
  }
}
