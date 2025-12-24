package slimeknights.tconstruct.library.modifiers.modules.util;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;

/** Predicate to condition a melee projectile on being a projectile. */
public enum ProjectilePredicate {
  ALWAYS, PROJECTILE, MELEE;

  public static final EnumLoadable<ProjectilePredicate> LOADABLE = new EnumLoadable<>(ProjectilePredicate.class);

  /** Evaluates the predicate. */
  public boolean test(boolean projectile) {
    return this == ALWAYS || (projectile == (this == PROJECTILE));
  }
}
