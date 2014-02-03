package tconstruct.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceLandmine extends DamageSource
{

    public DamageSourceLandmine(String s)
    {
        super(s);
        this.setDamageBypassesArmor();
    }

    public IChatComponent func_151519_b (EntityLivingBase par1EntityLivingBase)
    {
        //TODO getDeathMessage????
        return super.func_151519_b(par1EntityLivingBase);
    }

}
