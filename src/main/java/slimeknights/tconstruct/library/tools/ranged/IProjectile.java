package slimeknights.tconstruct.library.tools.ranged;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * The item is a tinker projectile. Used for internal handling.
 */
public interface IProjectile {

  boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, Entity entity, float damage);

  Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack);
}
