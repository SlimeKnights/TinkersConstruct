package slimeknights.tconstruct.library.materials;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * These values define how much an item is "worth".
 * The values are used for liquids as well as part crafting.
 * e.g. we use them to determine how much fluid shall be generated, or how much the item counts as for part building.
 * <p>
 * In general, use these values when registering items/item-interactions unless you have a very good reason.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialValues {
  /** Value of a single metal ingot, is divisible by 9 */
  public static final int INGOT = 144;
  /** Value of a single metal nugget */
  public static final int NUGGET = INGOT / 9;
  /** Value of a single metal block, is divisible by 81 */
  public static final int METAL_BLOCK = INGOT * 9;
  /** Value of a single metal brick block, is divisible by 36 */
  public static final int METAL_BRICK = INGOT * 4;

  /** Value of a gem such as emerald or an ender pearl, divides into buckets well */
  public static final int GEM = 250;
  /** Value of a block of 9 gems, such as emerald or an ender pearl */
  public static final int GEM_BLOCK = GEM * 9;

  /** Value of a single glass block, also used for obsidian */
  public static final int GLASS_BLOCK = 1000;
  /** Value of a glass pane, slightly cheaper than vanilla */
  public static final int GLASS_PANE = GLASS_BLOCK / 4;

  /** Value of a single slimeball, also used for clay and slime substitutes */
  public static final int SLIMEBALL = 250;
  /** Value of a block worth 4 slime, see also congealed */
  public static final int SLIME_CONGEALED = SLIMEBALL * 4;
  /** Value of a block worth 9 slime */
  public static final int SLIMEBLOCK = SLIMEBALL * 9;
}
