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

    public IChatComponent func_151519_b (EntityLivingBase par1EntityLivingBase)
    {
        return super.func_151519_b(par1EntityLivingBase);
    }

}
