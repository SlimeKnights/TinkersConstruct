package tconstruct.library.tools.traits;

public interface IMaterialTrait {
  public String getIdentifier();
  public String getLocalizedName();

  /* Updating */
  /** Called each tick */
  public void onUpdate();

  /* Harvesting */

  /**
   * Called just before a block breaks, analog to Item.onBlockStartBreak
   * @return Return true to prevent harvesting of the block.
   */
  public boolean beforeBlockBreak();

  /**
   * Called after a block has been broken.
   */
  public void afterBlockBreak();

  /* Attacking */

  /**
   * Called when an entity is hit, before the damage is dealt.
   * @param damage The original, unmodified damage from the tool
   * @param currentDamage The damage that will be dealt currently, possibly modified by other traits
   * @return The damage to deal. Standard return value is currentDamage
   */
  public float onHit(float damage, float currentDamage);

  // returns true if the attack should be a critical hit

  /**
   * Called AFTER damage calculations, allows to let the weapon crit.
   * @return true if it should be a crit. false will NOT prevent a crit from other sources.
   */
  public boolean doesCriticalHit();

  /* Damage tool */
  /**
   * Called when the tools durability is getting damaged
   * @param damage The original, unmodified damage that would be dealt
   * @param currentDamage The current damage that will be dealt, possibly modified by other traits
   * @return The damage to deal, Standard return value is currentDamage
   */
  public int onDamage(int damage, int currentDamage);
}
