package tconstruct.blocks.slime;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeLeaves extends BlockLeaves
{
    private static final String[] fastLeaves = new String[] { "slimeleaves_blue_fast" };
    private static final String[] fancyLeaves = new String[] { "slimeleaves_blue_fancy" };
    @SideOnly(Side.CLIENT)
    private Icon[] fastIcons;
    @SideOnly(Side.CLIENT)
    private Icon[] fancyIcons;

    public SlimeLeaves(int id)
    {
        super(id);
        setCreativeTab(TConstructRegistry.blockTab);
        setLightOpacity(1);
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor ()
    {
        return 0xffffff;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor (int par1)
    {
        return 0xffffff;
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 0xffffff;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.fastIcons = new Icon[fastLeaves.length];
        this.fancyIcons = new Icon[fancyLeaves.length];

        for (int i = 0; i < this.fastIcons.length; i++)
        {
            this.fastIcons[i] = iconRegister.registerIcon("tinker:" + fastLeaves[i] );
            this.fancyIcons[i] = iconRegister.registerIcon("tinker:" + fancyLeaves[i]);
        }
    }

    public Icon getIcon (int side, int meta)
    {
        int tex = meta % 4;

        if (this.graphicsLevel)
            return fancyIcons[tex];
        else
            return fastIcons[tex];
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < fastIcons.length; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
    
    /* Drops */


    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return TContent.slimeSapling.blockID;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune)
    {
        if (!world.isRemote)
        {
            int dropChance = 35;

            /*if ((meta & 3) == 3)
            {
                j1 = 40;
            }*/

            if (fortune > 0)
            {
                dropChance -= 2 << fortune;

                if (dropChance < 15)
                {
                    dropChance = 15;
                }
            }

            if (world.rand.nextInt(dropChance) == 0)
            {
                int k1 = this.idDropped(meta, world.rand, fortune);
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(k1, 1, this.damageDropped(meta)));
            }

            dropChance = 80;

            if (fortune > 0)
            {
                dropChance -= 10 << fortune;

                if (dropChance < 20)
                {
                    dropChance = 20;
                }
            }

            if ((meta & 3) == 0 && world.rand.nextInt(dropChance) == 0)
            {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(TContent.strangeFood, 1, 0));
            }
        }
    }
}
