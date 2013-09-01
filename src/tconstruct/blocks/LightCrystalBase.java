package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.client.block.CrystalBlockRender;
import tconstruct.common.TContent;
import tconstruct.crystal.TheftValueTracker;
import tconstruct.library.TConstructRegistry;

public class LightCrystalBase extends Block
{
    String[] textureNames = { "amber_crystal" };
    Icon[] icons;

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
        if (meta < 10)
            return Block.blockNetherQuartz.getIcon(side, 1);
        return icons[0];
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
        for (int iter = 0; iter < 15; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }
}
