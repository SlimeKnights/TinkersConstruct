package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;

public class MeatBlock extends BlockWood
{
    public IIcon[] icons;
    public String[] textureNames = new String[] { "ham_skin", "ham_bone" };

    public MeatBlock()
    {
        this.setHardness(1.0F);
        this.setStepSound(Block.soundTypeWood);
        // setBurnProperties(this.blockID, 5, 20);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int metadata)
    {
        int tex = (metadata % 4) * 2;
        int orientation = metadata / 4;

        switch (orientation)
        // Ends of logs
        {
        case 0:
            if (side == 0 || side == 1)
                return icons[tex + 1];
            break;
        case 1:
            if (side == 4 || side == 5)
                return icons[tex + 1];
            break;
        case 2:
            if (side == 2 || side == 3)
                return icons[tex + 1];
            break;
        }

        return icons[tex];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public Item getItemDropped (int par1, Random par2Random, int par3)
    {
        return new ItemStack(this).getItem();
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an
     * update, as appropriate
     */
    public void breakBlock (World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        byte b0 = 4;
        int j1 = b0 + 1;

        if (par1World.checkChunksExist(par2 - j1, par3 - j1, par4 - j1, par2 + j1, par3 + j1, par4 + j1))
        {
            for (int k1 = -b0; k1 <= b0; ++k1)
            {
                for (int l1 = -b0; l1 <= b0; ++l1)
                {
                    for (int i2 = -b0; i2 <= b0; ++i2)
                    {
                        Block j2 = par1World.getBlock(par2 + k1, par3 + l1, par4 + i2);

                        if (j2 != null)
                        {
                            j2.beginLeavesDecay(par1World, par2 + k1, par3 + l1, par4 + i2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z,
     * side, hitX, hitY, hitZ, block metadata
     */
    @Override
    public int onBlockPlaced (World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        int j1 = par9 & 3;
        byte b0 = 0;

        switch (par5)
        {
        case 0:
        case 1:
            b0 = 0;
            break;
        case 2:
        case 3:
            b0 = 8;
            break;
        case 4:
        case 5:
            b0 = 4;
        }

        return j1 | b0;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and
     * wood.
     */
    @Override
    public int damageDropped (int par1)
    {
        return par1 & 3;
    }

    /**
     * returns a number between 0 and 3
     */
    public static int limitToValidMetadata (int par0)
    {
        return par0 & 3;
    }

    @Override
    protected ItemStack createStackedBlock (int par1)
    {
        return new ItemStack(this, 1, limitToValidMetadata(par1));
    }

    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    /*
     * public void onBlockHarvested (World world, int x, int y, int z, int meta,
     * EntityPlayer player) { if (meta % 4 == 1) { if (world.difficultySetting >
     * 2) world.createExplosion(null, x, y, z, 1.75f, false); else
     * world.createExplosion(null, x, y, z, 2f, false); } }
     */

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks (Item b, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < icons.length / 2; i++)
            par3List.add(new ItemStack(b, 1, i));
    }

    @Override
    public boolean isBeaconBase (IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
