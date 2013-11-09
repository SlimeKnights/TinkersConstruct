package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
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
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = ((EntityLivingBase) entity);
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

    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 5)
            return Block.dirt.blockHardness;
        return this.blockHardness;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return Block.slowSand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /*public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        if (world.getBlockMetadata(x, y, z) >= 3)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.875F, 1.0F);
        }
    }*/
}
