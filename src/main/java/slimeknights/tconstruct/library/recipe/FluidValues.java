package slimeknights.tconstruct.library.recipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * These values define common amounts of fluids used in recipes.
 * <p>
 * Use these values when registering casting/melting interactions to ensure if the fluid API changes the values are still correct
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FluidValues {
  /** Value of a single metal ingot, is divisible by 9 */
  public static final int INGOT = 144;
  /** Value of a single metal nugget */
  public static final int NUGGET = INGOT / 9;
  /** Value of a single metal block, is divisible by 81 */
  public static final int METAL_BLOCK = INGOT * 9;
  /** Value of a single metal brick block, is divisible by 36 */
  public static final int METAL_BRICK = INGOT * 4;

  /** Value of a gem such as diamond or emerald, needs to be divible by 3 for ore multipliers */
  public static final int GEM = 144;
  /** Value of a block of 9 gems, such as emerald or an ender pearl */
  public static final int GEM_BLOCK = GEM * 9;

  /** Value of a single glass block, also used for obsidian */
  public static final int GLASS_BLOCK = 1000;
  /** Value of a glass pane, slightly cheaper than vanilla */
  public static final int GLASS_PANE = GLASS_BLOCK / 4;

  /** Value of a single slimeball, also used for clay, slime substitutes, and ender pearls */
  public static final int SLIMEBALL = 250;
  /** Value of a block worth 4 slime, see also congealed */
  public static final int SLIME_CONGEALED = SLIMEBALL * 4;
  /** Value of a block worth 9 slime */
  public static final int SLIMEBLOCK = SLIMEBALL * 9;
}
