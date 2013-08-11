package mods.tinker.tconstruct.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SoilBlock extends TConstructBlock
{
    static String[] soilTypes = new String[] { "slimesand", "grout", "slimesandblue", "graveyardsoil", "consecratedsoil", "slimedirt_blue" };

    public SoilBlock(int id)
    {
        super(id, Material.ground, 3.0F, soilTypes);
    }

    @Override
    public void onEntityWalking (World world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityLiving)
        {
            EntityLiving living = ((EntityLiving) entity);
            if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
            {
                int metadata = world.getBlockMetadata(x, y, z);
                if (metadata == 3)
                {
                    living.heal(1);
                }
                else if (metadata == 4)
                {
                    living.attackEntityFrom(DamageSource.magic, 1);
                    living.setFire(1);
                }
            }
        }
    }
}
