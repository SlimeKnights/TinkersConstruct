package tconstruct.blocks;

import java.util.List;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.PartCrafterLogic;
import tconstruct.blocks.logic.PatternChestLogic;
import tconstruct.blocks.logic.StencilTableLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.client.block.TableRender;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryBlock;
import tconstruct.util.PHConstruct;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ToolStationBlock extends InventoryBlock
{
    public ToolStationBlock(int id, Material material)
    {
        super(id, material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.setStepSound(Block.soundWoodFootstep);
    }

    //Block.hasComparatorInputOverride and Block.getComparatorInputOverride

    /* Rendering */
    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "toolstation_top", "toolstation_side", "toolstation_bottom", "partbuilder_oak_top", "partbuilder_oak_side", "partbuilder_oak_bottom", "partbuilder_spruce_top",
                "partbuilder_spruce_side", "partbuilder_spruce_bottom", "partbuilder_birch_top", "partbuilder_birch_side", "partbuilder_birch_bottom", "partbuilder_jungle_top",
                "partbuilder_jungle_side", "partbuilder_jungle_bottom", "patternchest_top", "patternchest_side", "patternchest_bottom", "stenciltable_oak_top", "stenciltable_oak_side",
                "stenciltable_oak_bottom", "stenciltable_spruce_top", "stenciltable_spruce_side", "stenciltable_spruce_bottom", "stenciltable_birch_top", "stenciltable_birch_side",
                "stenciltable_birch_bottom", "stenciltable_jungle_top", "stenciltable_jungle_side", "stenciltable_jungle_bottom" };

        return textureNames;
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta <= 4)
        {
            return icons[meta * 3 + getTextureIndex(side)];
        }
        else if (meta <= 9)
        {
            return icons[15 + getTextureIndex(side)];
        }
        else
        {
            return icons[meta * 3 + getTextureIndex(side) - 12];
        }
    }

    public int getTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
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
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side){
    	return side == ForgeDirection.UP;
    }
    
    @Override
    public int getRenderType ()
    {
        return TableRender.tabelModelID;
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 5)
            return AxisAlignedBB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY - 0.125,
                    (double) z + this.maxZ);
        return AxisAlignedBB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new ToolStationLogic();
        case 1:
            return new PartCrafterLogic();
        case 2:
            return new PartCrafterLogic();
        case 3:
            return new PartCrafterLogic();
        case 4:
            return new PartCrafterLogic();
        case 5:
            return new PatternChestLogic();
        case 6:
            return new PatternChestLogic();
        case 7:
            return new PatternChestLogic();
        case 8:
            return new PatternChestLogic();
        case 9:
            return new PatternChestLogic();
        case 10:
            return new StencilTableLogic();
        case 11:
            return new StencilTableLogic();
        case 12:
            return new StencilTableLogic();
        case 13:
            return new StencilTableLogic();
            /*case 14:
            	return new CastingTableLogic();*/
        default:
            return null;
        }
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int md = world.getBlockMetadata(x, y, z);
        if (md == 0)
            return 0;
        else if (md < 5)
            return 1;
        else if (md < 10)
            return 2;
        else
            return 3;

        //return -1;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 6; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }

        for (int iter = 10; iter < 14; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack)
    {
        if (PHConstruct.freePatterns)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 5)
            {
                PatternChestLogic logic = (PatternChestLogic) world.getBlockTileEntity(x, y, z);
                for (int i = 1; i <= 13; i++)
                {
                    logic.setInventorySlotContents(i - 1, new ItemStack(TContent.woodPattern, 1, i));
                }
                logic.setInventorySlotContents(13, new ItemStack(TContent.woodPattern, 1, 22));
            }
        }
    }
}
