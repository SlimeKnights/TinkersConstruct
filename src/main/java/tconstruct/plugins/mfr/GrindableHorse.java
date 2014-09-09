package tconstruct.plugins.mfr;

import java.util.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.*;
import tconstruct.tools.TinkerTools;

public class GrindableHorse implements IFactoryGrindable
{
    @Override
    public Class<? extends EntityLivingBase> getGrindableEntity ()
    {
        return EntityHorse.class;
    }

    @Override
    public List<MobDrop> grind (World world, EntityLivingBase entity, Random random)
    {
        List<MobDrop> drops = new ArrayList<MobDrop>();

        drops.add(new MobDrop(10, new ItemStack(TinkerTools.materials, random.nextInt(4) + 1, 36)));

        return drops;
    }

    @Override
    public boolean processEntity (EntityLivingBase entity)
    {
        return false;
    }
}
