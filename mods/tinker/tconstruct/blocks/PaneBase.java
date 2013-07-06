package mods.tinker.tconstruct.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.tinker.tconstruct.client.block.PaneRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PaneBase extends Block
{
    public String[] textureNames;
    public String folder;
    public Icon[] icons;
    public Icon[] sideIcons;
    public PaneBase(int id, Material material, String folder, String[] blockTextures)
    {
        super(id, material);
        textureNames = blockTextures;
        this.folder = folder;
    }

    public boolean canConnectTo (int blockID)
    {
        Block block = Block.blocksList[blockID];
        return Block.opaqueCubeLookup[blockID] || block instanceof PaneBase || block instanceof BlockPane || blockID == Block.glass.blockID;
    }

    public Icon getSideTextureIndex (int meta)
    {
        return sideIcons[meta];
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];
        this.sideIcons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:"+folder+textureNames[i]);
            this.sideIcons[i] = iconRegister.registerIcon("tinker:"+folder+textureNames[i]+"_side");
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        return icons[meta];
    }
    
    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
    
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return PaneRender.model;
    }

    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        int bID = iblockaccess.getBlockId(i, j, k);
        if (Block.blocksList[bID] instanceof PaneBase || Block.blocksList[bID] instanceof BlockPane)
        {
            return false;
        }
        else
        {
            return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity)
    {
        boolean south = canConnectTo(world.getBlockId(x, y, z - 1));
        boolean north = canConnectTo(world.getBlockId(x, y, z + 1));
        boolean east = canConnectTo(world.getBlockId(x - 1, y, z));
        boolean west = canConnectTo(world.getBlockId(x + 1, y, z));
        if (east && west || !east && !west && !south && !north)
        {
            setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (east && !west)
        {
            setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (!east && west)
        {
            setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        if (south && north || !east && !west && !south && !north)
        {
            setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (south && !north)
        {
            setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
        else if (!south && north)
        {
            setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
        }
    }

    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = canConnectTo(iblockaccess.getBlockId(i, j, k - 1));
        boolean flag1 = canConnectTo(iblockaccess.getBlockId(i, j, k + 1));
        boolean flag2 = canConnectTo(iblockaccess.getBlockId(i - 1, j, k));
        boolean flag3 = canConnectTo(iblockaccess.getBlockId(i + 1, j, k));
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
        setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

}
