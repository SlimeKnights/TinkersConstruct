package tconstruct.util;

import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.EntityLivingBase;

public class DamageSourceFireworkExplode extends DamageSource
{

    public DamageSourceFireworkExplode(String s)
    {
        super(s);
        this.setDamageBypassesArmor();
    }

    public IChatComponent getDeathMessage (EntityLivingBase par1EntityLivingBase)
    {
        return super.getDeathMessage(par1EntityLivingBase);
    }

}
