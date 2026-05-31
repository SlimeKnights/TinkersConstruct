package slimeknights.tconstruct.compat.minecraft.world.item.enchantment;

import net.minecraft.world.entity.LivingEntity;

/** Compatibility shim for the old protection helper. */
public final class ProtectionEnchantment {
  private ProtectionEnchantment() {}

  public static double getExplosionKnockbackAfterDampener(LivingEntity entity, double strength) {
    return strength * (1.0D - entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
  }
}
