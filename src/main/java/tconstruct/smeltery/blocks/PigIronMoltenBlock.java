package tconstruct.smeltery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class PigIronMoltenBlock extends TConstructFluid
{

    public PigIronMoltenBlock(Fluid fluid, Material material, String texture)
    {
        super(fluid, material, texture);
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.stillIcon = iconRegister.registerIcon("tinker:" + texture);
        this.flowIcon = iconRegister.registerIcon("tinker:" + texture);

        if (this.overwriteFluidIcons)
            this.getFluid().setIcons(stillIcon, flowIcon);
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (!world.isRemote && entity != null && !entity.isWet())
            entity.setFire(40);
        if (!entity.isWet() || world.rand.nextInt(100) > 73)
            world.playSoundAtEntity(entity, "random.fizz", 0.7F, 1.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
    }

}
