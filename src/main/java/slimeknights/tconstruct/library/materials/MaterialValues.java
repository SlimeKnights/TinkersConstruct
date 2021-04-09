package slimeknights.tconstruct.library.materials;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

/**
 * These values define how much an item is "worth".
 * The values are used for liquids as well as part crafting.
 * e.g. we use them to determine how much fluid shall be generated, or how much the item counts as for part building.
 * <p>
 * In general, use these values when registering items/item-interactions unless you have a very good reason.
 */
public final class MaterialValues {
  /** Value of a single metal ingot, is divisible by 9 */
  public static final FluidAmount INGOT = FluidAmount.of(1, 9);
  /** Value of a single metal nugget */
  public static final FluidAmount NUGGET = INGOT.div(9);
  /** Value of a single metal block, is divisible by 81 */
  public static final FluidAmount METAL_BLOCK = INGOT.mul(9);
  /** Value of a single metal brick block, is divisible by 36 */
  public static final FluidAmount METAL_BRICK = INGOT.mul(4);

  /** Value of a gem such as emerald or an ender pearl, divides into buckets well */
  public static final FluidAmount GEM = FluidAmount.of(1,4);
  /** Value of a block of 9 gems, such as emerald or an ender pearl */
  public static final FluidAmount GEM_BLOCK = GEM.mul(9);

  /** Value of a single glass block, also used for obsidian */
  public static final FluidAmount GLASS_BLOCK = FluidAmount.ONE;
  /** Value of a glass pane, slightly cheaper than vanilla */
  public static final FluidAmount GLASS_PANE = GLASS_BLOCK.div(4);

  /** Value of a single slimeball, also used for clay and slime substitutes */
  public static final FluidAmount SLIMEBALL = FluidAmount.of(1, 4);
  /** Value of a block worth 4 slime, see also congealed */
  public static final FluidAmount SLIME_CONGEALED = SLIMEBALL.mul(4);
  /** Value of a block worth 9 slime */
  public static final FluidAmount SLIMEBLOCK = SLIMEBALL.mul(9);
}
