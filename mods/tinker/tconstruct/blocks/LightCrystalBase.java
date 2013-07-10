
package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.natura.common.NContent;
import mods.tinker.tconstruct.client.block.CrystalBlockRender;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.crystal.TheftValueTracker;
import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

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

    @Override
    public void breakBlock (World world, int x, int y, int z, int par5, int meta)
    {
        switch (meta % 5)
        {
        case 0:
        case 4:
            TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, -10);
            break;
        case 1:
            TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, -20);
            break;
        case 2:
            TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, -35);
            break;
        case 3:
            TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, -60);
            break;
        }
        
        Block block = Block.blocksList[world.getBlockId(x, y-1, z)];
        if (block == TContent.aggregator)
        {
            ((Aggregator)block).updateCrystalValue(world, x, y-1, z);
        }
    }
}
