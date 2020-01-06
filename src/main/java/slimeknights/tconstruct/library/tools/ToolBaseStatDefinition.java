package slimeknights.tconstruct.library.tools;

import slimeknights.tconstruct.library.TinkerAPIException;

/**
 * This class defines the innate properties of a tool.
 * Everything before materials are factored in.
 */
public final class ToolBaseStatDefinition {

  /**
   * Multiplier applied to the actual mining speed of the tool
   * Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
   */
  private final float miningSpeedModifer;

  /** Multiplier for damage from materials. Should be defined per tool. */
  private final float damageModifier;

  /**
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   * Helps keeping power creep in check.
   * The default is 15, in general this should be sufficient and only needs increasing if it's a stronger weapon.
   * A diamond sword with sharpness V has 15 damage
   */
  private final float damageCutoff;

  /**
   * Allows you set the base attack speed, can be changed by modifiers. Equivalent to the vanilla attack speed.
   * 4 is equal to any standard item. Value has to be greater than zero.
   */
  private final double attackSpeed;

  /**
   * Knockback modifier. Basically this takes the vanilla knockback on hit and modifies it by this factor.
   */
  private final float knockbackModifier;

  protected ToolBaseStatDefinition(float miningSpeedModifer, float damageModifier, float damageCutoff, double attackSpeed, float knockbackModifier) {
    this.miningSpeedModifer = miningSpeedModifer;
    this.damageModifier = damageModifier;
    this.damageCutoff = damageCutoff;
    this.attackSpeed = attackSpeed;
    this.knockbackModifier = knockbackModifier;
  }


  public float getMiningSpeedModifer() {
    return miningSpeedModifer;
  }

  public float getDamageModifier() {
    return damageModifier;
  }

  public float getDamageCutoff() {
    return damageCutoff;
  }

  public double getAttackSpeed() {
    return attackSpeed;
  }

  public float getKnockbackModifier() {
    return knockbackModifier;
  }

  public static class Builder {
    private float miningSpeedModifer = 1;
    private float damageModifier = 0;
    private float damageCutoff = 15;
    private double attackSpeed = 4;
    private float knockbackModifier = 1;

    public Builder setMiningSpeedModifer(float miningSpeedModifer) {
      this.miningSpeedModifer = miningSpeedModifer;
      return this;
    }

    public Builder setDamageModifier(float damageModifier) {
      this.damageModifier = damageModifier;
      return this;
    }

    public Builder setDamageCutoff(float damageCutoff) {
      this.damageCutoff = damageCutoff;
      return this;
    }

    public Builder setAttackSpeed(double attackSpeed) {
      this.attackSpeed = attackSpeed;
      return this;
    }

    public Builder setKnockbackModifier(float knockbackModifier) {
      this.knockbackModifier = knockbackModifier;
      return this;
    }

    public ToolBaseStatDefinition build() {
      if(damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001 and non-negative.");
      }

      return new ToolBaseStatDefinition(miningSpeedModifer, damageModifier, damageCutoff, attackSpeed, knockbackModifier);
    }
  }
}
