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
import common.darkknight.jewelrycraft.tileentity.TileEntityMolder;
import common.darkknight.jewelrycraft.tileentity.TileEntitySmelter;
import common.darkknight.jewelrycraft.util.JewelrycraftUtil;

public class BlockSmelter extends BlockContainer
{
    Random rand = new Random();
    
    protected BlockSmelter(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntitySmelter();
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    public void dropItem(World world, double x, double y, double z, ItemStack stack)
    {
        EntityItem entityitem = new EntityItem(world, x + 0.5D, y + 1.3D, z + 0.5D, stack);
        entityitem.motionX = 0;
        entityitem.motionZ = 0;
        entityitem.motionY = 0.11000000298023224D;
        world.spawnEntityInWorld(entityitem);
    }
    
    public void breakBlock(World world, int i, int j, int k, int par5, int par6)
    {
        TileEntitySmelter te = (TileEntitySmelter) world.getBlockTileEntity(i, j, k);
        if (te != null && te.hasMetal){
            dropItem(world, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.metal.copy());
            world.markTileEntityForDespawn(te);
        }
        super.breakBlock(world, i, j, k, par5, par6);
    }
    
    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntitySmelter te = (TileEntitySmelter) world.getBlockTileEntity(i, j, k);
        ItemStack item = entityPlayer.inventory.getCurrentItem();
        if (te != null && !world.isRemote)
        {
            int index = -1;
            for(int a = 0; a < JewelrycraftUtil.jamcraftPlayers.size(); a++) if(entityPlayer.username.equals(JewelrycraftUtil.jamcraftPlayers.get(a))) index = a;
            if (!te.hasMetal && !te.hasMoltenMetal && item != null && (item.getUnlocalizedName().toLowerCase().contains("ingot") || index != -1) && !item.getUnlocalizedName().toLowerCase().contains("mold"))
            {
                entityPlayer.addChatMessage(StatCollector.translateToLocalFormatted("chatmessage.Jewelrycraft.smelter.nowsmeltingingot", item.getDisplayName()));
                te.metal = item.copy();
                te.metal.stackSize = 1;
                te.hasMetal = true;
                te.melting = ConfigHandler.ingotMeltingTime;
                if (!entityPlayer.capabilities.isCreativeMode) --item.stackSize;
                te.isDirty = true;
            }
            else if (te.hasMetal && !te.hasMoltenMetal && item != null && item.getDisplayName().contains("Ingot") && !item.getDisplayName().contains("Mold"))
                entityPlayer.addChatMessage(StatCollector.translateToLocalFormatted("chatmessage.Jewelrycraft.smelter.alreadyhasingot", te.metal.getDisplayName()));
            else if (te.hasMoltenMetal)
                entityPlayer.addChatMessage(StatCollector.translateToLocalFormatted("chatmessage.Jewelrycraft.smelter.hasmolteningot", te.moltenMetal.getDisplayName()));
            else if (item != null && !item.getUnlocalizedName().toLowerCase().contains("ingot") && item.getDisplayName().contains("Ingot"))
                entityPlayer.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.itemrenamedtoingot"));
            else if (item != null && (!item.getUnlocalizedName().toLowerCase().contains("ingot") || item.getUnlocalizedName().toLowerCase().contains("mold")))
                entityPlayer.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.itemneedstobeingot"));
            
            if (te.hasMetal && entityPlayer.isSneaking())
            {
                dropItem(world, (double)te.xCoord, (double)te.yCoord, (double)te.zCoord, te.metal.copy());
                te.hasMetal = false;
                te.melting = -1;
                world.markTileEntityForDespawn(te);
                world.setBlockTileEntity(i, j, k, te);
            }
            if(te != null) world.setBlockTileEntity(i, j, k, te);
        }
        return true;
    }
    
    @Override
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer player)
    {
        TileEntitySmelter te = (TileEntitySmelter) world.getBlockTileEntity(i, j, k);
        TileEntityMolder me = null;
        if (world.getBlockMetadata(i, j, k) == 0 && world.getBlockTileEntity(i, j, k - 1) != null && world.getBlockTileEntity(i, j, k - 1) instanceof TileEntityMolder)
            me = (TileEntityMolder) world.getBlockTileEntity(i, j, k - 1);
        else if (world.getBlockMetadata(i, j, k) == 1 && world.getBlockTileEntity(i + 1, j, k) != null && world.getBlockTileEntity(i + 1, j, k) instanceof TileEntityMolder)
            me = (TileEntityMolder) world.getBlockTileEntity(i + 1, j, k);
        else if (world.getBlockMetadata(i, j, k) == 2 && world.getBlockTileEntity(i, j, k + 1) != null && world.getBlockTileEntity(i, j, k + 1) instanceof TileEntityMolder)
            me = (TileEntityMolder) world.getBlockTileEntity(i, j, k + 1);
        else if (world.getBlockMetadata(i, j, k) == 3 && world.getBlockTileEntity(i - 1, j, k) != null && world.getBlockTileEntity(i - 1, j, k) instanceof TileEntityMolder)
            me = (TileEntityMolder) world.getBlockTileEntity(i - 1, j, k);
        
        if (te != null && me != null && !world.isRemote)
        {
            if (te.hasMoltenMetal && isConnectedToMolder(world, i, j, k) && me != null && me.hasMold && !me.hasMoltenMetal && !me.hasJewelBase)
            {
                me.moltenMetal = te.moltenMetal;
                me.hasMoltenMetal = true;
                me.cooling = ConfigHandler.ingotCoolingTime;
                te.moltenMetal = new ItemStack(0, 0, 0);
                te.hasMoltenMetal = false;
                me.isDirty = true;
                world.markTileEntityForDespawn(te);
                world.setBlockTileEntity(i, j, k, te);
            }
            else if (te.hasMetal && te.melting > 0)
                player.addChatMessage(StatCollector.translateToLocalFormatted("chatmessage.Jewelrycraft.smelter.metalismelting", te.metal.getDisplayName()) + " (" + ((ConfigHandler.ingotMeltingTime - te.melting)*100/ConfigHandler.ingotMeltingTime) + "%)");
            else if (te.hasMoltenMetal && !isConnectedToMolder(world, i, j, k))
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.molderismissing"));
            else if (!me.hasMold && te.hasMoltenMetal)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.molderhasnomold"));
            else if (me.hasMoltenMetal && te.hasMoltenMetal)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.molderhasmoltenmetal"));
            else if (me.hasJewelBase && te.hasMoltenMetal)
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.modlerhasitem"));
            else 
                player.addChatMessage(StatCollector.translateToLocal("chatmessage.Jewelrycraft.smelter.empty"));
        }
        
    }
    
    public boolean isConnectedToMolder(World world, int i, int j, int k)
    {
        int blockMeta = world.getBlockMetadata(i, j, k);
        if (blockMeta == 0 && world.getBlockId(i, j, k - 1) == BlockList.molder.blockID)
            return true;
        else if (blockMeta == 1 && world.getBlockId(i + 1, j, k) == BlockList.molder.blockID)
            return true;
        else if (blockMeta == 2 && world.getBlockId(i, j, k + 1) == BlockList.molder.blockID)
            return true;
        else if (blockMeta == 3 && world.getBlockId(i - 1, j, k) == BlockList.molder.blockID)
            return true;
        return false;
    }
    
    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack par6ItemStack)
    {
        int rotation = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, rotation, 2);
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
        this.blockIcon = icon.registerIcon("jewelrycraft:smelter");
    }
    
}
