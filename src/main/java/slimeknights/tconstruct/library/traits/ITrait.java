package slimeknights.tconstruct.library.traits;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;

// todo: move defaults to abstractTrait or all abstract to defaults

/**
 * Traits are specific properties on tools with special effects.
 * The trait object contains basic information about the trait.
 * The corresponding trait object gets events forwarded to it when a tool with that trait executes them
 */
public interface ITrait {
  /* Updating */

  /**
   * Called each tick by the tool. See {@link net.minecraft.item.Item#inventoryTick(ItemStack, World, Entity, int, boolean)}
   */
  void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected);

  /**
   * Called by stuff that's in the Armor slot? Unused so far.
   */
  void onArmorTick(ItemStack tool, World world, PlayerEntity player);

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
   * Called after the block has been destroyed. See {@link net.minecraft.item.Item#onBlockDestroyed(ItemStack, World, BlockState, BlockPos, LivingEntity)}
   * Called before the tools durability is reduced.
   */
  void afterBlockBreak(ItemStack tool, World world, BlockState state, BlockPos pos, LivingEntity player, boolean wasEffective);

  /**
   * Called after a block has been broken. See {@link net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent}
   * Note that, as opposed to the original event, this only gets called with a player.
   */
  // TODO: Event is gone?
  // void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event);


  /* Attacking */

  /**
   * Called BEFORE onHit, allows to let the weapon crit.
   *
   * @param tool   The tool dealing the damage.
   * @param player The player (or entity) that is hitting the target.
   * @param target The entity to hit.
   * @return true if it should be a crit. false will NOT prevent a crit from other sources.
   */
  boolean isCriticalHit(ItemStack tool, LivingEntity player, LivingEntity target);

  /**
   * Called when an entity is hit, before the damage is dealt and before critical hit calculation.
   * Allows to modify the damage dealt.
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
  float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical);

  /**
   * Called when an entity is hit, just before the damage is dealt. Damage is the final damage dealt, including critical damage.
   * Damage has been fully calculated. You can deal damage to the entity in this callback.
   * The hurtResistantTime will be set correctly before the call, and it will be reset after the call for the original damage call.
   *
   * @param tool       The tool dealing the damage.
   * @param player     The player (or entity) that is hitting the target.
   * @param target     The entity to hit.
   * @param damage     The original, unmodified damage from the tool. Does not includes critical damage, that will be calculated afterwards.
   * @param isCritical If the hit will be a critical hit.
   */
  void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical);

  /**
   * Modify the knockback applied. Called after onHit and with the actual damage value. Damage value INCLUDES crit damage here.
   *
   * @param tool         The tool dealing the damage.
   * @param player       The player (or entity) that is hitting the target.
   * @param target       The entity to hit.
   * @param damage       The damage that will be dealt, including critical hit damage.
   * @param knockback    Unmodified base knockback
   * @param newKnockback Current knockback, possibly modified by other traits.
   * @param isCritical   If the hit will be a critical hit.
   * @return The knockback, Standard return value is newKnockback.
   */
  float knockBack(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float knockback, float newKnockback, boolean isCritical);

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
  void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit);

  /**
   * Called when the player holding the tool blocks an attack.
   */
  void onBlock(ItemStack tool, PlayerEntity player, LivingHurtEvent event);

  /**
   * Called when the player holding the tool is damaged. Only called if the player is NOT blocking! onBlock is called in that case.
   */
  default void onPlayerHurt(ItemStack tool, PlayerEntity player, LivingEntity attacker, LivingHurtEvent event) {}

  /* Damage tool */

  /**
   * Called before the tools durability is getting reduced.
   *
   * @param tool      The tool to be damaged.
   * @param damage    The original, unmodified damage that would be dealt
   * @param newDamage The current damage that will be dealt, possibly modified by other traits
   * @return The damage to deal, Standard return value is newDamage
   */
  int onToolDamage(ItemStack tool, int damage, int newDamage, LivingEntity entity);

  /**
   * Called before the tools durability is getting increased.
   *
   * @param tool      The tool to be healed.
   * @param amount    The original, unmodified amount that would be healed
   * @param newAmount The current damage that will be healed, possibly modified by other traits
   * @return The damage to deal. Standard return value is newAmount
   */
  int onToolHeal(ItemStack tool, int amount, int newAmount, LivingEntity entity);

  /**
   * Called before the tool is getting repaired with its repair material.
   * Do not confuse this with onToolHeal, which will be called afterwards.
   * This callback as well as onToolHeal will be called multiple times when a tool is getting repaired with multiple items.
   *
   * @param tool   The tool to repair.
   * @param amount How much durability will be repaired. Can be bigger than the damage the tool has.
   */
  void onRepair(ItemStack tool, int amount);

  /**
   * When the tool is equipped, this is called to set the players attributes.
   * See {@link net.minecraft.item.Item#getAttributeModifiers(EquipmentSlotType, ItemStack)}
   *
   * @param slot         Analogous to Item.getAttributeModifiers
   * @param stack        Item.getAttributeModifiers
   * @param attributeMap The map you usually return. Fill in your stuff, if needed
   */
  default void getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> attributeMap) {}

  /**
   * Determines the order in which traits/modifiers are processed. Higher priority gets processed first.
   * This is needed when the modifier interacts with the world, like modifying drops or preventing/dealing damage.
   * <p>
   * Example: One modifier adds extra drops, while another consumes a specific drop.
   * The one removing drops needs to run after the one adding drops, to work properly.
   * <p>
   * Note that this has no impact if you use events rather than the trait callbacks! You should still respect it.
   * Does NOT affect the order in which applyEffect on IModifier is called.
   *
   * @return Priority, default is 100, higher priority (>100) runs before lower.
   */
  default int getPriority() {
    return 100;
  }
}
