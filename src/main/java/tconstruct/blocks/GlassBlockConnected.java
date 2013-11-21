package tconstruct.blocks;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.*;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.util.config.PHConstruct;

/** @author fuj1n
 * 
 */

public class GlassBlockConnected extends Block
{
    protected Icon[] icons = new Icon[16];
    private boolean shouldRenderSelectionBox = true;
    protected String folder;
    private int renderPass;

    public GlassBlockConnected(int par1, String location, boolean hasAlpha)
    {
        super(par1, Material.glass);
        this.setStepSound(soundGlassFootstep);
        folder = location;
        renderPass = hasAlpha ? 1 : 0;
        setHardness(0.3F);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    //For FMP support
    public Icon[] getIcons ()
    {
        return icons;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return renderPass;
    }

    /**
     * This is checked to see if the texture should connect to this block
     * @param par2 x
     * @param par3 y
     * @param par4 z
     * @param par5 ID this block is asking to connect to (may be 0 if there is no block)
     * @param par6 Metadata of the block this block is trying to connect to
     * @return true if should connect
     */
    public boolean shouldConnectToBlock (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, int par6)
    {
        return par5 == this.blockID;
    }

    @Override
    public Icon getBlockTexture (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 15 ? icons[0] : getConnectedBlockTexture(par1IBlockAccess, par2, par3, par4, par5, icons);
    }

    public Icon getConnectedBlockTexture (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, Icon[] icons)
    {
        if (PHConstruct.connectedTexturesMode == 0)
        {
            return icons[0];
        }

        boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

        switch (par5)
        {
        case 0:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[11];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[5];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[6];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenDown)
            {
                return icons[3];
            }
            else if (isOpenUp)
            {
                return icons[4];
            }
            else if (isOpenLeft)
            {
                return icons[2];
            }
            else if (isOpenRight)
            {
                return icons[1];
            }
            break;
        case 1:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[11];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[5];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[6];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenDown)
            {
                return icons[3];
            }
            else if (isOpenUp)
            {
                return icons[4];
            }
            else if (isOpenLeft)
            {
                return icons[2];
            }
            else if (isOpenRight)
            {
                return icons[1];
            }
            break;
        case 2:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[9];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[8];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[4];
            }
            else if (isOpenRight)
            {
                return icons[3];
            }
            break;
        case 3:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[10];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[7];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[3];
            }
            else if (isOpenRight)
            {
                return icons[4];
            }
            break;
        case 4:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[10];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[7];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[3];
            }
            else if (isOpenRight)
            {
                return icons[4];
            }
            break;
        case 5:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[9];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[8];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[4];
            }
            else if (isOpenRight)
            {
                return icons[3];
            }
            break;
        }

        return icons[0];
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    @Override
    public Icon getIcon (int par1, int par2)
    {
        return icons[0];
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool (World par1World, int par2, int par3, int par4)
    {
        if (shouldRenderSelectionBox)
        {
            return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
        }
        else
        {
            return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 0D, 0D, 0D);
        }
    }

    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {
        icons[0] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass");
        icons[1] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_1_d");
        icons[2] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_1_u");
        icons[3] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_1_l");
        icons[4] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_1_r");
        icons[5] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_h");
        icons[6] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_v");
        icons[7] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_dl");
        icons[8] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_dr");
        icons[9] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_ul");
        icons[10] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_2_ur");
        icons[11] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_3_d");
        icons[12] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_3_u");
        icons[13] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_3_l");
        icons[14] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_3_r");
        icons[15] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_4");
    }

    @Override
    public boolean canPlaceTorchOnTop (World world, int x, int y, int z)
    {
        return true;
    }
}
