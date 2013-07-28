package mods.tinker.tconstruct.landmine.damageSource;

import net.minecraft.util.DamageSource;

public class DamageSourceLandmine extends DamageSource{

	public DamageSourceLandmine(String s) {
		super(s);
		this.setDamageBypassesArmor();
	}
	
    /*public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase){
       return super.getDeathMessage(par1EntityLivingBase);
    }*/

}
