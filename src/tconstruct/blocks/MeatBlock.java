package tconstruct.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MeatBlock extends Block
{
    public Icon[] icons;
    public String[] textureNames = new String[] { "ham_skin", "ham_bone" };

    public MeatBlock(int id)
    {
        super(id, Material.wood);
        this.setHardness(1.0F);
        this.setStepSound(Block.soundWoodFootstep);
        //setBurnProperties(this.blockID, 5, 20);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int metadata)
    {
        int tex = (metadata % 4) * 2;
        int orientation = metadata / 4;

        switch (orientation)
        //Ends of logs
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
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    public int idDropped (int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
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
                        int j2 = par1World.getBlockId(par2 + k1, par3 + l1, par4 + i2);

                        if (Block.blocksList[j2] != null)
                        {
                            Block.blocksList[j2].beginLeavesDecay(par1World, par2 + k1, par3 + l1, par4 + i2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
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
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
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

    protected ItemStack createStackedBlock (int par1)
    {
        return new ItemStack(this.blockID, 1, limitToValidMetadata(par1));
    }

    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    /*public void onBlockHarvested (World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (meta % 4 == 1)
        {
            if (world.difficultySetting > 2)
                world.createExplosion(null, x, y, z, 1.75f, false);
            else
                world.createExplosion(null, x, y, z, 2f, false);
        }
    }*/

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < icons.length / 2; i++)
            par3List.add(new ItemStack(par1, 1, i));
    }

    @Override
    public boolean canSustainLeaves (World world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean isWood (World world, int x, int y, int z)
    {
        return true;
    }

    public boolean isBeaconBase (World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
