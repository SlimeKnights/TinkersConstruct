package slimeknights.tconstruct.library.modifiers.fluid;

/**
 * Represents the level of a fluid effect
 * @param value  Effective level
 * @param max    Max level, used when dealing with conservative effects
 */
public record EffectLevel(float value, float max) {
  /**
   * Gets the effective amount given the existing amount. Can possibly be up to {@link #max()} by summing with {@link #value()}.
   * Used level will be {@code effective - existing}. If {@code effective <= existing}, no action need be performed.
   * @param existing  Existing level on the target, note you will likely want to rescale this from result to level units.
   * @return Effective effect level
   */
  public float effective(float existing) {
    return Math.min(this.value + existing, max);
  }

  /**
   * Gets the level that was used, given the cap exists
   * @param max  Maximum level that could have been set
   * @return  Level that was used.
   */
  public float computeUsed(float max) {
    return Math.min(this.value, max);
  }

  /** Check if the effect level is at least 1, used to limit effects that don't scale */
  public boolean isFull() {
    return value >= 1;
  }

  /** Divides the effect level by the given divisor */
  public EffectLevel divide(float divisor) {
    if (divisor == 1) {
      return this;
    }
    if (divisor == 0) {
      throw new IllegalArgumentException("Divisor cannot be zero");
    }
    return new EffectLevel(value / divisor, max / divisor);
  }

  /** Subtracts the given amount from the effect level */
  public EffectLevel subtract(float amount) {
    if (amount == 0) {
      return this;
    }
    return new EffectLevel(value - amount, max);
  }
}
