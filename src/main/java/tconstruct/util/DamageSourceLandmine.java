package tconstruct.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

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
        return new ChatComponentTranslation("death.attack.Landmine", par1EntityLivingBase.func_145748_c_());
    }
}
