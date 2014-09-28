package tconstruct.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.potion.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import tconstruct.blocks.TConstructBlock;

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
            EntityLivingBase living = (EntityLivingBase) entity;
            // Well you'd feel ill too standing on glue...
            living.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 20, 4));

            // Glue is sticky stuff
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 30, 4));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return Blocks.soul_sand.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

}
