package tconstruct.blocks;

import java.util.List;

import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.client.block.TankRender;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IServantLogic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class LavaTankBlock extends BlockContainer
{
    public Icon[] icons;

    public LavaTankBlock(int id)
    {
        super(id, Material.rock);
        setHardness(12);
        setCreativeTab(TConstructRegistry.blockTab);
        setUnlocalizedName("TConstruct.LavaTank");
        setStepSound(Block.soundGlassFootstep);
    }

    public String[] getTextureNames ()
    {
        String[] textureNames = { "lavatank_side", "lavatank_top", "searedgague_top", "searedgague_side", "searedgague_bottom", "searedwindow_top", "searedwindow_side", "searedwindow_bottom" };

        return textureNames;
    }

    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
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
    public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        //if (side == 0 && world.getBlockMetadata(x, y, z) == 0)
        //return super.shouldSideBeRendered(world, x, y, z, side);
        int bID = world.getBlockId(x, y, z);
        return bID == this.blockID ? false : super.shouldSideBeRendered(world, x, y, z, side);
        //return true;
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic != null && logic instanceof LavaTankLogic)
            return ((LavaTankLogic) logic).getBrightness();
        return 0;
    }

    /*@Override
    public int getRenderBlockPass()
    {
    	return 1;
    }*/

    @Override
    public int getRenderType ()
    {
        return TankRender.tankModelID;
    }

    public Icon getIcon (int side, int meta)
    {
        if (meta == 0)
        {
            if (side == 0 || side == 1)
            {
                return icons[1];
            }
            else
            {
                return icons[0];
            }
        }
        else
        {
            return icons[meta * 3 + getTextureIndex(side) - 1];
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
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new LavaTankLogic();
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (heldItem != null)
        {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
            LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
            if (liquid != null)
            {
                int amount = logic.fill(ForgeDirection.UNKNOWN, liquid, false);
                if (amount == liquid.amount)
                {
                    logic.fill(ForgeDirection.UNKNOWN, liquid, true);
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));
                    return true;
                }
                else
                    return true;
            }
            else if (FluidContainerRegistry.isBucket(heldItem))
            {
                FluidTankInfo[] tanks = logic.getTankInfo(ForgeDirection.UNKNOWN);
                FluidStack fillFluid = tanks[0].fluid;//getFluid();
                ItemStack fillStack = FluidContainerRegistry.fillFluidContainer(fillFluid, heldItem);
                if (fillStack != null)
                {
                    logic.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, true);
                    if (!player.capabilities.isCreativeMode)
                    {
                        if (heldItem.stackSize == 1)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, fillStack);
                        }
                        else
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));

                            if (!player.inventory.addItemStackToInventory(fillStack))
                            {
                                player.dropPlayerItem(fillStack);
                            }
                        }
                    }
                    return true;
                }
                else
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static ItemStack consumeItem (ItemStack stack)
    {
        if (stack.stackSize == 1)
        {
            if (stack.getItem().hasContainerItem())
                return stack.getItem().getContainerItemStack(stack);
            else
                return null;
        }
        else
        {
            stack.splitStack(1);

            return stack;
        }
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return createTileEntity(world, 0);
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Data */
    public int damageDropped (int meta)
    {
        return meta;
    }

    /* Updates */
    public void onNeighborBlockChange (World world, int x, int y, int z, int nBlockID)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
    }
}
