package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Interface for extra hooks needed on modifyable weapons
 */
public interface IModifiableWeapon {
  /**
   * Actually deal damage to the entity we hit. Can be overridden for special behaviour
   *
   * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
   */
  default boolean dealDamage(ToolStack stack, LivingEntity attacker, Entity target, float damage, boolean isCritical, boolean fullyCharged) {
    if (attacker instanceof PlayerEntity) {
      return target.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), damage);
    }
    return target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
  }

  /**
   * Gets the damage cutoff for this weapon, that is the point where damage starts to cap
   *
   * @return  Damage cutoff
   */
  float getDamageCutoff();

  /**
   * Deals damage to the weapon
   *
   * @param tool    Tool instance
   * @param stack   Item stack instance for the tool
   * @param living  Attacker
   * @param amount  Amount of damage to deal
   */
  default void damageWeapon(ToolStack tool, ItemStack stack, LivingEntity living, int amount) {
    boolean isCreative = living instanceof PlayerEntity && ((PlayerEntity)living).isCreative();
    if (!isCreative && tool.damage(amount, living, stack)) {
      living.sendBreakAnimation(Hand.MAIN_HAND);
    }
  }
}
