package tconstruct.blocks;

import java.util.List;

import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpeedSlab extends SlabBase
{
    public SpeedSlab(int id)
    {
        super(id, Material.rock);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(3F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        meta = meta % 8;
        return TContent.speedBlock.getIcon(side, meta);
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 7; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void onEntityWalking (World world, int x, int y, int z, Entity entity)
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
