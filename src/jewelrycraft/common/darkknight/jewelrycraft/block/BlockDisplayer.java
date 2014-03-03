package common.darkknight.jewelrycraft.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import common.darkknight.jewelrycraft.tileentity.TileEntityDisplayer;

public class BlockDisplayer extends BlockContainer
{
    Random rand = new Random();

    protected BlockDisplayer(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityDisplayer();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntityDisplayer te = (TileEntityDisplayer) world.getBlockTileEntity(i, j, k);
        ItemStack item = entityPlayer.inventory.getCurrentItem();
        if (te != null && item != null && item != new ItemStack(0, 0, 0) && !world.isRemote)
        {
            if(!te.hasObject)
            {
                te.object = item.copy();
                te.object.stackSize = 1;
                te.quantity += item.stackSize;
                te.hasObject = true;
                if (!entityPlayer.capabilities.isCreativeMode) item.stackSize = 0;
                te.isDirty = true;
            }
            else if(te.object.itemID == item.itemID && te.object != null && te.object != new ItemStack(0, 0, 0) && te.object.getItemDamage() == item.getItemDamage())
            {
                if(te.object.hasTagCompound() && item.hasTagCompound() && te.object.getTagCompound().equals(item.getTagCompound()))
                {
                    te.quantity += item.stackSize;
                    te.object.stackSize = 1;
                    if (!entityPlayer.capabilities.isCreativeMode) item.stackSize = 0;
                    te.isDirty = true;
                }
                else if(!te.object.hasTagCompound() && !item.hasTagCompound())
                {
                    te.quantity += item.stackSize;
                    te.object.stackSize = 1;
                    if (!entityPlayer.capabilities.isCreativeMode) item.stackSize = 0;
                    te.isDirty = true;                    
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer player)
    {
        TileEntityDisplayer te = (TileEntityDisplayer) world.getBlockTileEntity(i, j, k);
        if (te != null && !world.isRemote)
        {
            if (te.hasObject && te.object != null && te.object != new ItemStack(0, 0, 0) && player.inventory.addItemStackToInventory(te.object))
            {
                if(player.isSneaking())
                {
                    if(te.quantity > te.object.getMaxStackSize())
                    {
                        te.object.stackSize = te.object.getMaxStackSize() - 1;
                        player.inventory.addItemStackToInventory(te.object);
                        te.object.stackSize = 1;
                        te.quantity -= te.object.getMaxStackSize();
                    }
                    else
                    {
                        te.object.stackSize = te.quantity - 1;
                        player.inventory.addItemStackToInventory(te.object);
                        te.hasObject = false;
                        te.object = new ItemStack(0, 0, 0);
                        te.quantity = 0;
                    }
                    te.isDirty = true;
                }
                else
                {
                    if(te.quantity >= 2)
                    {
                        player.inventory.addItemStackToInventory(te.object);
                        te.object.stackSize = 1;
                        --te.quantity;
                    }
                    else
                    {
                        player.inventory.addItemStackToInventory(te.object);
                        te.object.stackSize = 1;
                        te.hasObject = false;
                        te.object = new ItemStack(0, 0, 0);
                        te.quantity = 0;
                    }
                    te.isDirty = true;
                }  
            }
        }
    }

    public void dropItem(World world, double x, double y, double z, ItemStack stack)
    {
        EntityItem entityitem = new EntityItem(world, x + 0.5D, y + 1.5D, z + 0.5D, stack);
        entityitem.motionX = 0;
        entityitem.motionZ = 0;
        entityitem.motionY = 0.11000000298023224D;
        world.spawnEntityInWorld(entityitem);
    }

    public void breakBlock(World world, int i, int j, int k, int par5, int par6)
    {
        TileEntityDisplayer te = (TileEntityDisplayer) world.getBlockTileEntity(i, j, k);

        if (te != null && te.hasObject)
        {
            te.object.stackSize = te.quantity;
            dropItem(te.worldObj, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.object);
            world.markTileEntityForDespawn(te);
        }

        super.breakBlock(world, i, j, k, par5, par6);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack par6ItemStack)
    {
        int rotation = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, rotation, 2);
    }

    @Override
    public void registerIcons(IconRegister icon)
    {
        this.blockIcon = icon.registerIcon("jewelrycraft:displayer");
    }
}
