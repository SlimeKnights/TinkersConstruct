package slimeknights.tconstruct.library.modifiers.modules.util;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;

/** Predicate matching a boolean parameter on a modifier module */
public enum BooleanPredicate implements it.unimi.dsi.fastutil.booleans.BooleanPredicate {
  /** Matches regardless of the boolean value */
  ALWAYS,
  /** Matches if the boolean value is true */
  TRUE,
  /** Matches if the boolean value is false */
  FALSE;

  /** Loadable instance for using in modules. */
  public static final EnumLoadable<BooleanPredicate> LOADABLE = new EnumLoadable<>(BooleanPredicate.class);

  @Override
  public boolean test(boolean value) {
    return this == ALWAYS || (value == (this == TRUE));
  }
}
