package tconstruct.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.potion.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BloodBlock extends TConstructFluid
{

    public BloodBlock(Fluid fluid, Material material, String texture)
    {
        super(fluid, material, texture);
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 20 * 15, 0));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 20 * 5, 1));
        }
    }
}
