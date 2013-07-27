package tconstruct.blocks.logic;

import tconstruct.library.blocks.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.World;

public class GolemCoreLogic extends InventoryLogic implements IInventory
{

    public GolemCoreLogic()
    {
        super(1);
    }

    public ItemStack getKey ()
    {
        return inventory[0];
    }

    public void setKey (ItemStack itemstack)
    {
        inventory[0] = itemstack;
        onInventoryChanged();
    }

    public void clear ()
    {
        inventory[0] = null;
    }

    public int getSizeInventory ()
    {
        return 1;
    }

    public ItemStack getStackInSlot (int slot)
    {
        return inventory[0];
    }

    public int getInventoryStackLimit ()
    {
        return 1;
    }

    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return true;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int i)
    {
        return null;
    }

    @Override
    public boolean isInvNameLocalized ()
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        return "golems.core";
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}
