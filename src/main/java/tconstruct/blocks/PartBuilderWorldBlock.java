package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.PartBuilderWorldLogic;
import tconstruct.client.block.PartBuilderWorldRender;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryBlock;
import tconstruct.library.tools.AbilityHelper;

public class PartBuilderWorldBlock extends InventoryBlock
{
    public PartBuilderWorldBlock(int id, Material material)
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
    public boolean isBlockSolidOnSide (World world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public int getRenderType ()
    {
        return PartBuilderWorldRender.model;
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
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 1 && meta <= 4)
        {
            return activatePartBuilder(world, x, y, z, player);
        }
        else
            return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

    boolean activatePartBuilder (World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            PartBuilderWorldLogic logic = (PartBuilderWorldLogic) world.getBlockTileEntity(x, y, z);
            if (logic.isStackInSlot(2))
            {
                ItemStack stack = logic.decrStackSize(2, 1);
                if (stack != null)
                    AbilityHelper.spawnItemAtPlayer(player, stack);

                if (logic.isStackInSlot(3))
                {
                    stack = logic.decrStackSize(3, 1);
                    if (stack != null)
                        AbilityHelper.spawnItemAtPlayer(player, stack);
                }
            }
            else if (!logic.isStackInSlot(0))
            {
                ItemStack stack = player.getCurrentEquippedItem();
                stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                logic.setInventorySlotContents(0, stack);
            }
            else
            {
                ItemStack equip = player.getCurrentEquippedItem();
                if (equip == null && logic.isStackInSlot(0))
                {
                    ItemStack stack = logic.decrStackSize(0, 1);
                    if (stack != null)
                        AbilityHelper.spawnItemAtPlayer(player, stack);
                }
                else if (equip != null)
                {
                    if (logic.buildItemPart(equip))
                    {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    }
                }
            }

            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 1:
            return new PartBuilderWorldLogic();
        case 2:
            return new PartBuilderWorldLogic();
        case 3:
            return new PartBuilderWorldLogic();
        case 4:
            return new PartBuilderWorldLogic();
        default:
            return null;
        }
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return -1;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 1; iter <= 4; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
