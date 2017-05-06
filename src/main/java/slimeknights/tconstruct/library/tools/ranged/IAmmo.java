package slimeknights.tconstruct.library.tools.ranged;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;

/**
 * Represents an item that has an ammo count
 * This is not a capability because we also need it clientside, and durability already is synced.
 */
public interface IAmmo {
  /**
   * Returns the current ammo amount of the item.
   */
  int getCurrentAmmo(@Nonnull ItemStack stack);

  /**
   * Returns the maximum amount of ammo the item can have
   */
  int getMaxAmmo(@Nonnull ItemStack stack);

  /**
   * Add one ammo. Usually used for picking up projectiles on the ground, etc.
   * @param stack The itemstack to add the ammo to. Has to have the proper NBT.
   * @param player The player picking up the ammo. Used for particles and trait interaction.
   * @return True if ammo could be added, false if ammo already is full.
   */
  boolean addAmmo(@Nonnull ItemStack stack, @Nullable EntityLivingBase player);

  /**
   * Consumes one ammo. Usually when a projectile is shot.
   * @param stack The itemstack to use the ammo from. Has to have the proper NBT.
   * @param player The player using the ammo. Used for particles and trait interaction.
   * @return True if ammo was used, false if no ammo is left or no ammo was used (due to traits,...).
   */
  boolean useAmmo(@Nonnull ItemStack stack, @Nullable EntityLivingBase player);

  /**
   * Sets the Ammo amount to that absolute value.
   * Behaviour for values below 0 or above the max ammo is undefined.
   * @param count Set count
   * @param stack The itemstack to set the ammo for. Has to have proper NBT.
   */
  void setAmmo(int count, @Nonnull ItemStack stack);

  /**
   * Gets the projectile to fire, matching the itemstacks data.
   */
  EntityProjectileBase getProjectile(@Nonnull ItemStack stack, @Nonnull ItemStack launcher, World world, EntityPlayer player, float speed, float inaccuracy, float power, boolean usedAmmo);
}
