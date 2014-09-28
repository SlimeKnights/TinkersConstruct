package tconstruct.world.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.blocks.TConstructBlock;

public class SoilBlock extends TConstructBlock
{
    static String[] soilTypes = new String[] { "slimesand", "grout", "slimesandblue", "graveyardsoil", "consecratedsoil", "slimedirt_blue", "nether_grout" };

    public SoilBlock()
    {
        super(Material.ground, 3.0F, soilTypes);
    }

    @Override
    public void onEntityWalking (World world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            if (((EntityLivingBase) entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
            {
                int metadata = world.getBlockMetadata(x, y, z);
                if (metadata == 3)
                {
                    ((EntityLivingBase) entity).heal(1);
                }
                else if (metadata == 4)
                {
                    ((EntityLivingBase) entity).attackEntityFrom(DamageSource.magic, 1);
                    ((EntityLivingBase) entity).setFire(1);
                }
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 3)
        {
            entity.motionX *= 0.4;
            entity.motionZ *= 0.4;
            if (meta != 1 && entity instanceof EntityLivingBase)
            {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.id, 1));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 1, 1));
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /*
     * public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
     * int z) { this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F); if
     * (world.getBlockMetadata(x, y, z) >= 3) { this.setBlockBounds(0.0F, 0.0F,
     * 0.0F, 1.0F, 0.875F, 1.0F); } }
     */
}
