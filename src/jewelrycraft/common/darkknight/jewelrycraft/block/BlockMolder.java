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
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import common.darkknight.jewelrycraft.config.ConfigHandler;
import common.darkknight.jewelrycraft.item.ItemList;
import common.darkknight.jewelrycraft.tileentity.TileEntityMolder;

public class BlockMolder extends BlockContainer
{
    Random rand = new Random();

    protected BlockMolder(int par1, Material par2Material)
    {
        super(par1, par2Material);
        this.setBlockBounds(0.1F, 0F, 0.1F, 0.9F, 0.2F, 0.9F);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityMolder();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntityMolder te = (TileEntityMolder) world.getBlockTileEntity(i, j, k);
        ItemStack item = entityPlayer.inventory.getCurrentItem();
        if (te != null && !world.isRemote)
        {
            if(item != null && !te.hasMold && item.itemID == ItemList.molds.itemID)
            {
                te.mold = item.copy();
                te.hasMold = true;
                if (!entityPlayer.capabilities.isCreativeMode) --item.stackSize;
                entityPlayer.addChatMessage(StatCollector.translateToLocalFormatted("chatmessage.Jewelrycraft.molder.addedmold", te.mold.getDisplayName()));
                te.isDirty = true;
            }
            if (te.hasMold && entityPlayer.isSneaking() && !te.hasMoltenMetal)
            {
                dropItem(world, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.mold.copy());
                te.mold = new ItemStack(0, 0, 0);
                te.hasMold = false;
                te.isDirty = true;
            }
            else if(te.hasMoltenMetal) entityPlayer.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.molder.hasmoltenmetal"));
        }
        return true;
    }

    public void dropItem(World world, double x, double y, double z, ItemStack stack)
    {
        EntityItem entityitem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
        entityitem.motionX = 0;
        entityitem.motionZ = 0;
        entityitem.motionY = 0.11000000298023224D;
        world.spawnEntityInWorld(entityitem);
    }

    public void breakBlock(World world, int i, int j, int k, int par5, int par6)
    {
        TileEntityMolder te = (TileEntityMolder) world.getBlockTileEntity(i, j, k);

        if (te != null)
        {
            if(te.hasJewelBase) dropItem(te.worldObj, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.jewelBase.copy());
            if(te.hasMold) dropItem(world, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.mold.copy());
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
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer player)
    {
        TileEntityMolder me = (TileEntityMolder) world.getBlockTileEntity(i, j, k);
        if (me != null && !world.isRemote)
        {
            if (me.hasJewelBase)
            {
                dropItem(me.worldObj, (double)me.xCoord, (double)me.yCoord, (double)me.zCoord, me.jewelBase.copy());
                me.jewelBase = new ItemStack(0, 0, 0);
                me.hasJewelBase = false;
            }
            else if (me.hasMoltenMetal && me.cooling > 0)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.molder.metaliscooling") + " (" + ((ConfigHandler.ingotCoolingTime - me.cooling)*100/ConfigHandler.ingotCoolingTime) + "%)");
            else if (me.mold.itemID == ItemList.molds.itemID && !me.hasMoltenMetal)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.molder.moldisempty"));
            else if (me.mold.itemID != ItemList.molds.itemID)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.molder.moldismissing"));
            me.isDirty = true;
        }
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
    public void registerIcons(IconRegister icon)
    {
        this.blockIcon = icon.registerIcon("jewelrycraft:molder");
    }
}
