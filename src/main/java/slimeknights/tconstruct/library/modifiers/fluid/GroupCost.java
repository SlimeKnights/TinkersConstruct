package slimeknights.tconstruct.library.modifiers.fluid;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;

/** Enum for changing how a JSON effect combines its cost for multiple targets */
public enum GroupCost {
  /** The cost of all targets will be summed together */
  SUM,
  /** The largest cost will be used */
  MAX;

  public static final EnumLoadable<GroupCost> LOADABLE = new EnumLoadable<>(GroupCost.class);
}
