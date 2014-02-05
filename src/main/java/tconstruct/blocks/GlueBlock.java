package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class GlueBlock extends TConstructBlock
{

    public GlueBlock()
    {
        super(Material.ground, 4.0f, new String[] { "glue" });
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        entity.motionX *= 0.1;
        entity.motionZ *= 0.1;

        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase lvb = (EntityLivingBase) entity;
            // Well you'd feel ill too standing on glue...
            if (lvb.isPotionActive(Potion.hunger))
            {
                lvb.getActivePotionEffect(Potion.hunger).duration = 20;
            }
            else
            {
                lvb.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 20, 4));
            }

            // Glue is sticky stuff
            if (lvb.isPotionActive(Potion.moveSlowdown))
            {
                lvb.getActivePotionEffect(Potion.moveSlowdown).duration = 30;
            }
            else
            {
                lvb.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 30, 4));
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

}
