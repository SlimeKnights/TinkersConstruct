package tinker.tconstruct;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.StatCollector;

public class PiercingEntityDamage extends EntityDamageSource
{
    public PiercingEntityDamage(String str, Entity entity)
    {
        super(str, entity);
        this.setDamageBypassesArmor();
    }
}
