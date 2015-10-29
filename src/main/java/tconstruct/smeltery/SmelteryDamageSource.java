package tconstruct.smeltery;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import tconstruct.library.tools.AbilityHelper;

public class SmelteryDamageSource extends DamageSource
{
    public SmelteryDamageSource()
    {
        super("smeltery");
    }

    @Override
    public IChatComponent getDeathMessage (EntityLivingBase par1EntityLiving)
    {
        String type = "";
        switch (AbilityHelper.random.nextInt(4))
        {
        case 0:
            type = "one.";
            break;
        case 1:
            type = "two.";
            break;
        case 2:
            type = "three.";
            break;
        case 3:
            type = "four.";
            break;
        }
        EntityLivingBase entityliving1 = par1EntityLiving.func_94060_bK();
        String s = "death." + type + this.damageType;
        String s1 = s + ".player";
        return entityliving1 != null && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] { par1EntityLiving.getFormattedCommandSenderName(), entityliving1.getFormattedCommandSenderName() }) : new ChatComponentTranslation(s, new Object[] { par1EntityLiving.getFormattedCommandSenderName() });
    }
}
