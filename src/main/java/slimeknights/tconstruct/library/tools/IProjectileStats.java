package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public interface IProjectileStats {

   boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, EntityLivingBase entity, float damage);

  Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack);
}
