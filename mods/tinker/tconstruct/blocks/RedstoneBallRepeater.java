package mods.tinker.tconstruct.blocks;

import java.util.Random;

import mods.tinker.tconstruct.client.block.BallRepeaterRender;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedstoneBallRepeater extends BlockRedstoneLogic
{
    /** The offsets for the two torches in redstone repeater blocks. */
    public static final double[] repeaterTorchOffset = new double[] { -0.0625D, 0.0625D, 0.1875D, 0.3125D};

    /** The states in which the redstone repeater blocks can be. */
    public static final int[] repeaterState = new int[] {1, 2, 3, 4};
    
    @SideOnly(Side.CLIENT)
    public static Icon field_94413_c;
    @SideOnly(Side.CLIENT)
    public static Icon field_94410_cO;
    @SideOnly(Side.CLIENT)
    public static Icon field_94411_cP;
    @SideOnly(Side.CLIENT)
    public static Icon field_94412_cQ;

    public RedstoneBallRepeater(int par1, boolean par2)
    {
        super(par1, par2);
        //this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        int i1 = par1World.getBlockMetadata(par2, par3, par4);
        int j1 = (i1 & 12) >> 2;
        j1 = j1 + 1 << 2 & 12;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 | i1 & 3, 3);
        return true;
    }

    public int func_94481_j_(int par1)
    {
        return repeaterState[(par1 & 12) >> 2] * 2;
    }

    public BlockRedstoneLogic func_94485_e()
    {
        return Block.redstoneRepeaterActive;
    }

    public BlockRedstoneLogic func_94484_i()
    {
        return Block.redstoneRepeaterIdle;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    /*public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Item.redstoneRepeater.itemID;
    }*/

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BallRepeaterRender.model;
    }

    public boolean func_94476_e(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.func_94482_f(par1IBlockAccess, par2, par3, par4, par5) > 0;
    }

    public boolean func_94477_d(int par1)
    {
        return isRedstoneRepeaterBlockID(par1);
    }
    
    public static boolean isPowerProviderOrWire(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        int i1 = par0IBlockAccess.getBlockId(par1, par2, par3);

        if (i1 == Block.redstoneWire.blockID)
        {
            return true;
        }
        else if (i1 == 0)
        {
            return false;
        }
        else if (!Block.redstoneRepeaterIdle.func_94487_f(i1))
        {
            return (Block.blocksList[i1] != null && Block.blocksList[i1].canConnectRedstone(par0IBlockAccess, par1, par2, par3, par4));
        }
        else
        {
            int j1 = par0IBlockAccess.getBlockMetadata(par1, par2, par3);
            return par4 == (j1 & 3) || par4 == Direction.rotateOpposite[j1 & 3];
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.isRepeaterPowered)
        {
            int l = par1World.getBlockMetadata(par2, par3, par4);
            int i1 = getDirection(l);
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.4F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.0D;
            double d4 = 0.0D;

            if (par5Random.nextInt(2) == 0)
            {
                switch (i1)
                {
                    case 0:
                        d4 = -0.3125D;
                        break;
                    case 1:
                        d3 = 0.3125D;
                        break;
                    case 2:
                        d4 = 0.3125D;
                        break;
                    case 3:
                        d3 = -0.3125D;
                }
            }
            else
            {
                int j1 = (l & 12) >> 2;

                switch (i1)
                {
                    case 0:
                        d4 = repeaterTorchOffset[j1];
                        break;
                    case 1:
                        d3 = -repeaterTorchOffset[j1];
                        break;
                    case 2:
                        d4 = -repeaterTorchOffset[j1];
                        break;
                    case 3:
                        d3 = repeaterTorchOffset[j1];
                }
            }

            par1World.spawnParticle("reddust", d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }
    

    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || par1World.getBlockId(par2, par3 - 1, par4) == Block.glowStone.blockID;
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        this.func_94483_i_(par1World, par2, par3, par4);
    }

    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 8388608;
    }
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.field_94413_c = par1IconRegister.registerIcon("tinker:repeaterDust_cross");
        this.field_94410_cO = par1IconRegister.registerIcon("tinker:repeaterDust_line");
        this.field_94411_cP = par1IconRegister.registerIcon("tinker:repeaterDust_cross_overlay");
        this.field_94412_cQ = par1IconRegister.registerIcon("tinker:repeaterDust_line_overlay");
        this.blockIcon = this.field_94413_c;
    }
    
    @SideOnly(Side.CLIENT)
    public static Icon func_94409_b(String par0Str)
    {
        return par0Str == "repeaterDust_cross" ? field_94413_c : (par0Str == "repeaterDust_line" ? field_94410_cO : (par0Str == "repeaterDust_cross_overlay" ? field_94411_cP : (par0Str == "repeaterDust_line_overlay" ? field_94412_cQ : null)));
    }
}
