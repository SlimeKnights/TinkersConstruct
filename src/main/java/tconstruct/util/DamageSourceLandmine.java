package tconstruct.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
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
        return new ChatComponentTranslation("death.attack.Landmine", par1EntityLivingBase.func_145748_c_());
    }
}
