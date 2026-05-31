package slimeknights.tconstruct.library;

import net.minecraft.world.item.ItemDisplayContext;

/** Custom transform types used for tinkers item rendering */
public class TinkerItemDisplays {
  private TinkerItemDisplays() {}

  public static void init() {}

  /** Used by the melter and smeltery for display of items its melting */
  public static ItemDisplayContext MELTER = ItemDisplayContext.NONE;
  /** Used by the part builder, crafting station, tinkers station, and tinker anvil */
  public static ItemDisplayContext TABLE = ItemDisplayContext.NONE;
  /** Used by the casting table for item rendering */
  public static ItemDisplayContext CASTING_TABLE = ItemDisplayContext.FIXED;
  /** Used by the casting basin for item rendering */
  public static ItemDisplayContext CASTING_BASIN = ItemDisplayContext.NONE;
  /** Used by the fluid cannon for display of the item in front */
  public static ItemDisplayContext FLUID_CANNON = ItemDisplayContext.FIXED;
  /** Used by throwing to allow adjusting the tool position */
  public static ItemDisplayContext THROWN = ItemDisplayContext.FIXED;
}
