package slimeknights.tconstruct.library.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
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
}
