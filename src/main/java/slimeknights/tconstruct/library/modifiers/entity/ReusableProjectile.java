package slimeknights.tconstruct.library.modifiers.entity;

import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.common.TinkerTags;

/** Interface handling making projectiles reusable, to prevent accidental removal. */
public interface ReusableProjectile {
  /** Checks if the projectile is currently reusable. */
  boolean isReusable();

  /** Checks if the given projectile is reusable, either by tag or interface. */
  static boolean isSingleUse(Projectile projectile) {
    // if in the tag, its reusable
    if (projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)) {
      return false;
    }
    // if implementing the interface, conditionally
    if (projectile instanceof ReusableProjectile reusable) {
      return !reusable.isReusable();
    }
    // anything else is single use
    return true;
  }

  /** Discards the given projectile */
  static void discard(Projectile projectile) {
    if (isSingleUse(projectile)) {
      projectile.discard();
    }
  }
}
