package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpeedSlab extends SlabBase
{
    public SpeedSlab()
    {
        super(Material.field_151576_e);
        this.func_149647_a(TConstructRegistry.blockTab);
        func_149711_c(3F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        meta = meta % 8;
        return TRepo.speedBlock.func_149691_a(side, meta);
    }

    @Override
    public void func_149666_a (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 7; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    @Override
    public void func_149724_b (World world, int x, int y, int z, Entity entity)
    {
        double boost = 2.2D;
        int metadata = world.getBlockMetadata(x, y, z) % 8;
        if (metadata == 1 || metadata == 4)
            boost = 2.7D;

        double mX = Math.abs(entity.motionX);
        double mZ = Math.abs(entity.motionZ);
        if (mX < 0.5D)
        {
            entity.motionX *= boost;
        }
        if (mZ < 0.5D)
        {
            entity.motionZ *= boost;
        }
    }
}
