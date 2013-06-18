package mods.tinker.tconstruct.util.player;

import java.lang.ref.WeakReference;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.FMLCommonHandler;

public class ArmorExtended implements IInventory
{
    public ItemStack[] inventory = new ItemStack[7];
    public WeakReference<EntityPlayer> parent;
    
    public void init(EntityPlayer player)
    {
        parent = new WeakReference<EntityPlayer>(player);
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    public boolean isStackInSlot (int slot)
    {
        return inventory[slot] != null;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            //System.out.println("Took something from slot " + slot);
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
            EntityPlayer player = parent.get();
    		TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            recalculateHealth(player, stats);
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        //System.out.println("Changed slot " + slot + " on side " + FMLCommonHandler.instance().getEffectiveSide());
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
		EntityPlayer player = parent.get();
		TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
        recalculateHealth(player, stats);
    }

    @Override
    public String getInvName ()
    {
        return "";
    }

    @Override
    public boolean isInvNameLocalized ()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    @Override
    public void onInventoryChanged ()
    {
		EntityPlayer player = parent.get();
		TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
    	//recalculateSkills(player, stats);
        recalculateHealth(player, stats);
        
        if (inventory[2] != null && inventory[2].getItem() == TContent.knapsack)
        {
            
        }
    }
    
    /*public void recalculateSkills(EntityPlayer player, TPlayerStats stats)
    {
    	if (inventory[1] != null && inventory[1].getItem() == TContent.glove)
    	{
    		if (stats.skillList.size() < 1)
    		{
    			try
    			{
    				stats.skillList.add(SkillRegistry.skills.get("Wall Building").copy());
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	else
    	{
    		if (stats.skillList.size() > 0)
    		{
    			stats.skillList.remove(0);
    		}
    	}
    }*/
    
    public void recalculateHealth(EntityPlayer player, TPlayerStats stats)
    {
    	if (inventory[6] != null && inventory[6].getItem() == TContent.heartCanister)
        {
        	ItemStack stack = inventory[6];
        	int meta = stack.getItemDamage();
        	if (meta == 2)
        	{
        		int bonusHP = stack.stackSize * 2;
        		stats.bonusHealth = bonusHP;
        		player.maxHealth = 20 + bonusHP;
        	}
        }
        else if (parent != null && parent.get() != null)
        {
            parent.get().maxHealth = 20;
        }
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    @Override
    public boolean isStackValidForSlot (int slot, ItemStack itemstack)
    {
        return false;
    }
    
    /* Save/Load */
    public void saveToNBT (EntityPlayer entityplayer)
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

        tags.setTag("TConstruct.Inventory", tagList);
    }

    public void readFromNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = tags.getTagList("TConstruct.Inventory");
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
}
