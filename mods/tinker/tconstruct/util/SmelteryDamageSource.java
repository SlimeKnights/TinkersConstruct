package mods.tinker.tconstruct.util;

import mods.tinker.tconstruct.library.tools.AbilityHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class SmelteryDamageSource extends DamageSource
{
    public SmelteryDamageSource()
    {
        super("smeltery");
    }

    public String getDeathMessage (EntityLiving par1EntityLiving)
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
        EntityLiving entityliving1 = par1EntityLiving.func_94060_bK();
        String s = "death." + type + this.damageType;
        String s1 = s + ".player";
        return entityliving1 != null && StatCollector.func_94522_b(s1) ? StatCollector.translateToLocalFormatted(s1,
                new Object[] { par1EntityLiving.getTranslatedEntityName(), entityliving1.getTranslatedEntityName() }) : StatCollector.translateToLocalFormatted(s,
                new Object[] { par1EntityLiving.getTranslatedEntityName() });
    }
}
