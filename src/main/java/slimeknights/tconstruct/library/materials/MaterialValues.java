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
  // the values are used for both liquid conversion as well as part crafting
  public static final int VALUE_Ingot = 144;
  public static final int VALUE_Nugget = VALUE_Ingot / 9;
  public static final int VALUE_Fragment = VALUE_Ingot / 4;
  public static final int VALUE_Shard = VALUE_Ingot / 2;

  public static final int VALUE_Gem = 666; // divisible by 3!
  public static final int VALUE_Block = VALUE_Ingot * 9;

  public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
  public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
  public static final int VALUE_Glass = 1000;
  public static final int VALUE_Pane = VALUE_Glass * 6 / 16;

  public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

  public static final int VALUE_SlimeBall = 250;


  private MaterialValues() {
  }
}
