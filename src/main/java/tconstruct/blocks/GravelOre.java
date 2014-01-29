package tconstruct.blocks;

import java.util.List;
import java.util.Random;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class GravelOre extends BlockSand
{
    public String[] textureNames = new String[] { "iron", "gold", "copper", "tin", "aluminum", "cobalt" };
    public IIcon[] icons;

    public GravelOre()
    {
        super();
        this.func_149647_a(TConstructRegistry.blockTab);
        this.setStepSound(soundGravelFootstep);
        this.field_149764_J = Material.field_151596_z;
    }

    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:ore_" + textureNames[i] + "_gravel");
        }
    }

    @Override
    public IIcon getIcon (int side, int meta)
    {
        return icons[meta];
    }

    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 5)
            return 10f;
        else
            return 3f;
    }

    @Override
    public int damageDropped (int meta)
    {
        /*if (meta == 1)
            return 0;*/
        return meta;
    }

    public int idDropped (int par1, Random par2Random, int par3)
    {
        /*if (par1 == 1)
            return Item.goldNugget.itemID;*/
        return this.blockID;
    }

    @Override
    public void getSubBlocks (Block b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 6; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
