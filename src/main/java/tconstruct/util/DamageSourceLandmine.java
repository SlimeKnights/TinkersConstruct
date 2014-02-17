package tconstruct.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class DamageSourceLandmine extends DamageSource
{

    public DamageSourceLandmine(String s)
    {
        super(s);
        this.setDamageBypassesArmor();
    }

    @Override
    public IChatComponent func_151519_b (EntityLivingBase par1EntityLivingBase)
    {
        // TODO getDeathMessage????
        return super.func_151519_b(par1EntityLivingBase);
    }

}
