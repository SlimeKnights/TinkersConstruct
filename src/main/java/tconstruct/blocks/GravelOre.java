package tconstruct.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;

public class GravelOre extends BlockFalling
{
    public String[] textureNames = new String[] { "iron", "gold", "copper", "tin", "aluminum", "cobalt" };
    public IIcon[] icons;

    public GravelOre()
    {
        super();
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.stepSound = soundTypeGravel;
        //this.blockMaterial = Material.craftedSnow;
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
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

    public Block idDropped (int par1, Random par2Random, int par3)
    {
        /*if (par1 == 1)
            return Item.goldNugget.itemID;*/
        return this;
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 6; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
