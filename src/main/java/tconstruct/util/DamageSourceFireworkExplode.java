package tconstruct.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class DamageSourceFireworkExplode extends DamageSource
{

    public DamageSourceFireworkExplode(String s)
    {
        super(s);
        this.setDamageBypassesArmor();
    }

    @Override
    public IChatComponent func_151519_b (EntityLivingBase par1EntityLivingBase)
    {
    	return new ChatComponentTranslation("death.attack.FireworkExplode", par1EntityLivingBase.func_145748_c_());
    }

}
