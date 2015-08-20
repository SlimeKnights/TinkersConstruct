package slimeknights.tconstruct.library.traits;

import net.minecraft.item.ItemStack;

public interface ITrait {

  String getIdentifier();

  String getLocalizedName();

  /** A short description to tell the user what the trait does */
  String getLocalizedDesc();

  /** Returns how often the trait can be stacked on one item. A value of 1 or less means not stackable. */
  int getMaxCount();

  /* Updating */

  /**
   * Called each tick
   */
  void onUpdate(ItemStack stack);

  /* Harvesting */

  /**
   * Called when a block is mined.
   *
   * @param speed        The original, unmodified speed from the tool
   * @param currentSpeed How fast the block will be harvested currently, possibly modified by other traits
   * @param isEffective  If the tool is effective for the block to harvest
   * @return How fast the block should be harvested. Standard return value is currentSpeed
   */
  float miningSpeed(ItemStack stack, float speed, float currentSpeed, boolean isEffective);

  /**
   * Called just before a block breaks, analog to Item.onBlockStartBreak
   *
   * @return Return true to prevent harvesting of the block.
   */
  boolean beforeBlockBreak(ItemStack stack);

  /**
   * Called after a block has been broken.
   */
  void afterBlockBreak(ItemStack stack);
  /* Attacking */

  /**
   * Called when an entity is hit, before the damage is dealt.
   *
   * @param damage        The original, unmodified damage from the tool
   * @param currentDamage The damage that will be dealt currently, possibly modified by other traits
   * @return The damage to deal. Standard return value is currentDamage
   */
  float onHit(ItemStack stack, float damage, float currentDamage);


  /**
   * Called AFTER damage calculations, allows to let the weapon crit.
   *
   * @return true if it should be a crit. false will NOT prevent a crit from other sources.
   */
  boolean doesCriticalHit(ItemStack stack);

  /* Damage tool */

  /**
   * Called when the tools durability is getting damaged
   *
   * @param damage        The original, unmodified damage that would be dealt
   * @param currentDamage The current damage that will be dealt, possibly modified by other traits
   * @return The damage to deal, Standard return value is currentDamage
   */
  int onDamage(ItemStack stack, int damage, int currentDamage);
}
