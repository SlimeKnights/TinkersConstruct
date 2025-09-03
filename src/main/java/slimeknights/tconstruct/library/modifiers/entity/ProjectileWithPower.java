package slimeknights.tconstruct.library.modifiers.entity;

/** Interface for a projectile with a power getter and setter, used by {@link slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule} */
public interface ProjectileWithPower {
  /** Gets the current power */
  float getPower();

  /** Sets the power to the new value */
  void setPower(float power);
}
