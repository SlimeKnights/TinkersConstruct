package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.natura.common.NContent;
import mods.tinker.tconstruct.client.block.CrystalBlockRender;
import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class LightCrystalBase extends Block
{
    public LightCrystalBase(int id)
    {
        super(id, Material.glass);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta < 5)
            return Block.glowStone.getIcon(side, meta);
        return Block.blockNetherQuartz.getIcon(side, 1);
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public int getRenderType ()
    {
        return CrystalBlockRender.model;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 10; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
