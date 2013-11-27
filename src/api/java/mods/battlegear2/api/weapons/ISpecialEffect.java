package mods.battlegear2.api.weapons;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface ISpecialEffect {
	/**
	 * 
	 * @param entityHit The entity the effect will be applied to.
	 * @param entityHitting
	 * @return true if it adds an hitting action
	 */
    public boolean performEffects(EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
