package tconstruct.library.tinkering.traits;

public interface ITrait {

  String getIdentifier();

  String getLocalizedName();

  /* Updating */

  /**
   * Called each tick
   */
  void onUpdate();

  /* Harvesting */

  /**
   * Called when a block is mined.
   *
   * @param speed        The original, unmodified speed from the tool
   * @param currentSpeed How fast the block will be harvested currently, possibly modified by other traits
   * @param isEffective  If the tool is effective for the block to harvest
   * @return How fast the block should be harvested. Standard return value is currentSpeed
   */
  float miningSpeed(float speed, float currentSpeed, boolean isEffective);

  /**
   * Called just before a block breaks, analog to Item.onBlockStartBreak
   *
   * @return Return true to prevent harvesting of the block.
   */
  boolean beforeBlockBreak();

  /**
   * Called after a block has been broken.
   */
  void afterBlockBreak();
  /* Attacking */

  /**
   * Called when an entity is hit, before the damage is dealt.
   *
   * @param damage        The original, unmodified damage from the tool
   * @param currentDamage The damage that will be dealt currently, possibly modified by other traits
   * @return The damage to deal. Standard return value is currentDamage
   */
  float onHit(float damage, float currentDamage);


  /**
   * Called AFTER damage calculations, allows to let the weapon crit.
   *
   * @return true if it should be a crit. false will NOT prevent a crit from other sources.
   */
  boolean doesCriticalHit();

  /* Damage tool */

  /**
   * Called when the tools durability is getting damaged
   *
   * @param damage        The original, unmodified damage that would be dealt
   * @param currentDamage The current damage that will be dealt, possibly modified by other traits
   * @return The damage to deal, Standard return value is currentDamage
   */
  int onDamage(int damage, int currentDamage);
}
