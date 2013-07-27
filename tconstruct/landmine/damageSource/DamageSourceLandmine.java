package tconstruct.landmine.damageSource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class DamageSourceLandmine extends DamageSource{

	public DamageSourceLandmine(String s) {
		super(s);
		this.setDamageBypassesArmor();
	}
	
    public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase){
       return super.getDeathMessage(par1EntityLivingBase);
    }

}
