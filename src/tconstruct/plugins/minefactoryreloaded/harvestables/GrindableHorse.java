package tconstruct.plugins.minefactoryreloaded.harvestables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.MobDrop;
import tconstruct.common.TContent;

public class GrindableHorse implements IFactoryGrindable
{
    @Override
    public Class<?> getGrindableEntity ()
    {
        return EntityHorse.class;
    }

    @Override
    public List<MobDrop> grind (World world, EntityLivingBase entity, Random random)
    {
        List<MobDrop> drops = new ArrayList<MobDrop>();

        drops.add(new MobDrop(10, new ItemStack(TContent.materials, random.nextInt(6) + 3, 36)));

        return drops;
    }

    @Override
    public boolean processEntity (EntityLivingBase entity)
    {
        return false;
    }
}
