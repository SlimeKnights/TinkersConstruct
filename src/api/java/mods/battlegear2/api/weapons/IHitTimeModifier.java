package mods.battlegear2.api.weapons;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IHitTimeModifier {
	/**
	 * 
	 * @param entityHit 
	 * @return The amount to modify the hit shield
	 */
	public int getHitTime(ItemStack stack, EntityLivingBase entityHit);

}
