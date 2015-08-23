package slimeknights.tconstruct.library.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Traits are specific properties on tools with special effects.
 * The trait object contains basic information about the trait.
 * The corresponding trait object gets events forwarded to it when a tool with that trait executes them
 */
public interface ITrait {

  String getIdentifier();

  String getLocalizedName();

  /** A short description to tell the user what the trait does */
  String getLocalizedDesc();

  /** Returns how often the trait can be stacked on one item. A value of 1 or less means not stackable. */
  int getMaxCount();

  /* Updating */

  /**
   * Called each tick by the tool. See {@link net.minecraft.item.Item#onUpdate(ItemStack, World, Entity, int, boolean)}
   */
  void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected);

  /**
   * Called by stuff that's in the Armor slot? Unused so far.
   */
  void onArmorTick(ItemStack tool, World world, EntityPlayer player);

  /* Mining/Harvesting */

  /**
   * Called when a block is mined. See {@link net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed}.
   */
  void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event);

  /**
   * Called just before a block breaks. See {@link net.minecraftforge.event.world.BlockEvent.BreakEvent}.
   */
  void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event);

  /**
   * Called after the block has been destroyed. See {@link net.minecraft.item.Item#onBlockDestroyed(ItemStack, World, Block, BlockPos, EntityLivingBase)}
   * Called before the tools durability is reduced.
   */
  void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player);

  /**
   * Called after a block has been broken. See {@link net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent}
   * Note that, as opposed to the original event, this only gets called with a player.
   */
  void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event);


  /* Attacking */

  /**
   * Called BEFORE onHit, allows to let the weapon crit.
   *
   * @param tool   The tool dealing the damage.
   * @param player The player (or entity) that is hitting the target.
   * @param target The entity to hit.
   * @return true if it should be a crit. false will NOT prevent a crit from other sources.
   */
  boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target);

  /**
   * Called when an entity is hit, before the damage is dealt and before critical hit calculation.
   * Critical hit damage will be calculated off the result of this!
   *
   * @param tool       The tool dealing the damage.
   * @param player     The player (or entity) that is hitting the target.
   * @param target     The entity to hit.
   * @param damage     The original, unmodified damage from the tool. Does not includes critical damage, that will be calculated afterwards.
   * @param newDamage  The damage that will be dealt currently, possibly modified by other traits.
   * @param isCritical If the hit will be a critical hit.
   * @return The damage to deal. Standard return value is newDamage.
   */
  float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical);

  /**
   * Modify the knockback applied. Called after onHit and with the actual damage value. Damage value INCLUDES crit damage here.
   * @param tool         The tool dealing the damage.
   * @param player       The player (or entity) that is hitting the target.
   * @param target       The entity to hit.
   * @param damage       The damage that will be dealt, including critical hit damage.
   * @param knockback    Unmodified base knockback
   * @param newKnockback Current knockback, possibly modified by other traits.
   * @param isCritical   If the hit will be a critical hit.
   * @return The knockback, Standard return value is newKnockback.
   */
  float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical);

  /**
   * Called after an entity has been hit, after the damage is dealt.
   *
   * @param tool        The tool that dealt the damage.
   * @param player      The player (or entity) that hit the target.
   * @param target      The entity hit.
   * @param damageDealt How much damage has been dealt to the entity. This is the ACTUAL damage dealt - the difference in Health of the entity.
   * @param wasCritical If the hit was a critical hit.
   * @param wasHit      If the target was actually hit. False when the entity was still invulnerable, or prevented the damage because of some other reason.
   */
  void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit);

  /* Damage tool */

  /**
   * Called before the tools durability is getting reduced.
   *
   * @param tool      The tool to be damaged.
   * @param damage    The original, unmodified damage that would be dealt
   * @param newDamage The current damage that will be dealt, possibly modified by other traits
   * @return The damage to deal, Standard return value is newDamage
   */
  int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity);

  /**
   * Called before the tools durability is getting increased.
   *
   * @param tool      The tool to be healed.
   * @param amount    The original, unmodified amount that would be healed
   * @param newAmount The current damage that will be healed, possibly modified by other traits
   * @return The damage to deal. Standard return value is newAmount
   */
  int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity);

  /**
   * Called before the tool is getting repaired with its repair material.
   * Do not confuse this with onToolHeal, which will be called afterwards.
   * This callback as well as onToolHeal will be called multiple times when a tool is getting repaired with multiple items.
   *
   * @param tool       The tool to repair.
   * @param amount     How much durability will be repaired. Can be bigger than the damage the tool has.
   * @param repairItem The item the tool will be repaired with.
   * @return True to allow the repair, false to prevent the repairing. Standard value is true.
   */
  boolean onRepair(ItemStack tool, int amount, ItemStack repairItem);
}
