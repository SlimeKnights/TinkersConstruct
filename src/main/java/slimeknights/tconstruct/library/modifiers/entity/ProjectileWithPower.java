package slimeknights.tconstruct.library.modifiers.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;

/** Interface for a projectile with a power getter and setter, used by {@link slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule} */
public interface ProjectileWithPower {
  /** Gets the current power */
  float getPower();

  /** Gets the amount of damage this projectile will deal. Used by {@link #getDamage(Projectile)} */
  default float getDamage() {
    return getPower();
  }

  /** Sets the power to the new value */
  void setPower(float power);

  
  /** Gets the power for the given projectile */
  static float getDamage(Projectile projectile) {
    if (projectile instanceof ProjectileWithPower withPower) {
      return withPower.getDamage();
    }
    if (projectile instanceof AbstractArrow arrow) {
      return (float) Mth.ceil(Mth.clamp(arrow.getBaseDamage() * projectile.getDeltaMovement().length(), 0, Integer.MAX_VALUE));
    }
    return 0;
  }
}
