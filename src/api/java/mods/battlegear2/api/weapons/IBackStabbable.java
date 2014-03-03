package mods.battlegear2.api.weapons;

import net.minecraft.entity.EntityLivingBase;

public interface IBackStabbable {

	/**
	 * Action to perform on back stabbing
	 * @param entityHit
	 * @param entityHitting
     * @return true if it adds an hitting action
	 */
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
