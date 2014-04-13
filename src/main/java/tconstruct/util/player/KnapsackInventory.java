package tconstruct.util.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;

public class KnapsackInventory implements IInventory
{
    public ItemStack[] inventory = new ItemStack[27];
    public WeakReference<EntityPlayer> parent;
    String playerID;
    int dimensionID;
    boolean globalKnapsack = true;

    public void init (EntityPlayer player)
    {
        parent = new WeakReference<EntityPlayer>(player);
    }

    public void init (EntityPlayer player, String playerID, int dimensionID, boolean global)
    {
        init(player);
        this.playerID = playerID;
        this.dimensionID = dimensionID;
        this.globalKnapsack = global;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return inventory[slot];
    }

    public boolean isStackInSlot (int slot)
    {
        return inventory[slot] != null;
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    public boolean canDropInventorySlot (int slot)
    {
        return true;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getInvName ()
    {
        return "tconstruct.knapsack";
    }

    @Override
    public boolean isInvNameLocalized ()
    {
        return false;
    }

    @Override
    public void onInventoryChanged ()
    {

    }

    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    /* Save/Load */
    /*public void saveToNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound invSlot;

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tags.setTag("TConstruct.Knapsack", tagList);
    }

    public void readFromNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = tags.getTagList("TConstruct.Knapsack");
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                this.inventory[j] = itemstack;
            }
        }
    }*/

    public void saveToNBT (EntityPlayer player)
    {
        NBTTagCompound baseTag = player.getEntityData();
        NBTTagCompound tags = baseTag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound invSlot;

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tags.setTag(getPlayerTagname(), tagList);
        if (!baseTag.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
            baseTag.setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tags);
    }

    public void readFromNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = tags.getTagList("TConstruct.Knapsack"); //Load old knapsack first
        if (tagList.tagCount() == 0)
        {
            tags = entityplayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            tagList = tags.getTagList(getPlayerTagname());
        }
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                this.inventory[j] = itemstack;
            }
        }
    }

    public void writeInventoryToStream (DataOutputStream os) throws IOException
    {
        for (int i = 0; i < 27; i++)
            Packet.writeItemStack(inventory[i], os);
    }

    public void readInventoryFromStream (DataInputStream is) throws IOException
    {
        for (int i = 0; i < 27; i++)
        {
            inventory[i] = Packet.readItemStack(is);
        }
    }

    private String getPlayerTagname ()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TConstruct.Knapsack.");

        //Dimensional awareness
        if (globalKnapsack)
            builder.append("Global");
        else
            builder.append(dimensionID);

        //Player clones
        if (playerID != null && !(playerID.equals("")))
            builder.append(playerID);

        return builder.toString();
    }

    public void dropItems ()
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                player.dropPlayerItemWithRandomChoice(this.inventory[i], true);
                this.inventory[i] = null;
            }
        }
    }

    public void unequipItems ()
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                dropItemEntity(player, inventory[i]);
                this.inventory[i] = null;
            }
        }
    }

    void dropItemEntity (Entity dropper, ItemStack dropStack)
    {
        EntityItem entityitem = new EntityItem(dropper.worldObj, dropper.posX, dropper.posY, dropper.posZ, dropStack);
        dropper.worldObj.spawnEntityInWorld(entityitem);
    }
}