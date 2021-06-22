package slimeknights.tconstruct.library.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/**
 * Interface for extra hooks needed on modifyable weapons
 */
public interface IModifiableWeapon {
  /**
   * Actually deal damage to the entity we hit. Can be overridden for special behaviour
   *
   * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
   */
  default boolean dealDamage(IModifierToolStack stack, LivingEntity attacker, Hand hand, Entity target, float damage, boolean isCritical, boolean fullyCharged) {
    return ToolAttackUtil.dealDefaultDamage(attacker, target, damage);
  }
}
