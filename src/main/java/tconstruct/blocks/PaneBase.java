package tconstruct.blocks;

import java.util.List;

import tconstruct.client.block.PaneRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PaneBase extends Block
{
    public String[] textureNames;
    public String folder;
    public IIcon[] icons;
    public IIcon[] sideIcons;

    public PaneBase(Material material, String folder, String[] blockTextures)
    {
        super(material);
        textureNames = blockTextures;
        this.folder = folder;
    }

    public final boolean func_150098_a(Block p_150098_1_)
    {
        return p_150098_1_.func_149730_j() || p_150098_1_ == this || p_150098_1_ == Blocks.glass || p_150098_1_ == Blocks.stained_glass || p_150098_1_ == Blocks.stained_glass_pane || p_150098_1_ instanceof BlockPane;
    }
    
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return func_150098_a(world.func_147439_a(x, y, z)) || world.isSideSolid(x, y, z, dir.getOpposite(), false);
    }

    public IIcon getSideTextureIndex (int meta)
    {
        return sideIcons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];
        this.sideIcons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
            this.sideIcons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i] + "_side");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        return icons[meta];
    }

    @Override
    public void func_149666_a (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    public boolean isOpaqueCube ()
    {
        return false;
    }

    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public int getRenderType ()
    {
        return PaneRender.model;
    }

    public boolean  func_149646_a (IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        Block b = iblockaccess.func_147439_a(i, j, k);
        if (b instanceof PaneBase || b instanceof BlockPane)
        {
            return false;
        }
        else
        {
            return super. func_149646_a(iblockaccess, i, j, k, l);
        }
    }

    @Override
    public void func_149743_a (World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity)
    {
        boolean south = canConnectTo(world, x, y, z - 1, ForgeDirection.NORTH);
        boolean north = canConnectTo(world, x, y, z + 1, ForgeDirection.SOUTH);
        boolean east = canConnectTo(world, x - 1, y, z, ForgeDirection.EAST);
        boolean west = canConnectTo(world, x + 1, y, z, ForgeDirection.WEST);
        if (east && west || !east && !west && !south && !north)
        {
            func_149676_a(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (east && !west)
        {
            func_149676_a(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (!east && west)
        {
            func_149676_a(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        if (south && north || !east && !west && !south && !north)
        {
            func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (south && !north)
        {
            func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (!south && north)
        {
            func_149676_a(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
        }
    }

    public void setBlockBoundsForItemRender ()
    {
        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState (IBlockAccess iblockaccess, int i, int j, int k)
    {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = canConnectTo(iblockaccess, i, j, k - 1, ForgeDirection.NORTH);
        boolean flag1 = canConnectTo(iblockaccess, i, j, k + 1, ForgeDirection.SOUTH);
        boolean flag2 = canConnectTo(iblockaccess, i - 1, j, k, ForgeDirection.EAST);
        boolean flag3 = canConnectTo(iblockaccess, i + 1, j, k, ForgeDirection.WEST);
        if (flag2 && flag3 || !flag2 && !flag3 && !flag && !flag1)
        {
            f = 0.0F;
            f1 = 1.0F;
        }
        else if (flag2 && !flag3)
        {
            f = 0.0F;
        }
        else if (!flag2 && flag3)
        {
            f1 = 1.0F;
        }
        if (flag && flag1 || !flag2 && !flag3 && !flag && !flag1)
        {
            f2 = 0.0F;
            f3 = 1.0F;
        }
        else if (flag && !flag1)
        {
            f2 = 0.0F;
        }
        else if (!flag && flag1)
        {
            f3 = 1.0F;
        }
        func_149676_a(f, 0.0F, f2, f1, 1.0F, f3);
    }

}
