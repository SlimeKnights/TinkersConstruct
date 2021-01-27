package slimeknights.tconstruct.library.materials;

/**
 * These values define how much an item is "worth".
 * The values are used for liquids as well as part crafting.
 * e.g. we use them to determine how much fluid shall be generated, or how much the item counts as for part building.
 * <p>
 * In general, use these values when registering items/item-interactions unless you have a very good reason.
 */
public final class MaterialValues {
  // How much the different items are "worth"
  // the values are used for liquid conversion

  /** Value of a single metal ingot, is divisible by 9 */
  public static final int VALUE_Ingot = 144;
  /** Value of a single metal nugget */
  public static final int VALUE_Nugget = VALUE_Ingot / 9;
  /** Value of a single metal block, is divisible by 81 */
  public static final int VALUE_Block = VALUE_Ingot * 9;
  /** Value of a single metal brick block, is divisible by 36 */
  public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

  /** Value of a gem such as emerald or an ender pearl, divides into buckets well */
  public static final int VALUE_Gem = 250;
  public static final int VALUE_GemBlock = VALUE_Gem * 9;

  /** Value of a single glass block */
  public static final int VALUE_Glass = VALUE_Ingot * 4;
  /** Value of a glass pane, slightly cheaper than vanilla */
  public static final int VALUE_Pane = VALUE_Glass / 4;

  /** Value of a single slimeball */
  public static final int VALUE_SlimeBall = 250;


  private MaterialValues() {
  }
}
