package mods.tinker.tconstruct;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class PiercingEntityDamage extends EntityDamageSource
{
    public PiercingEntityDamage(String str, Entity entity)
    {
        super(str, entity);
        this.setDamageBypassesArmor();
    }
}
