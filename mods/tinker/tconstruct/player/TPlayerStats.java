package mods.tinker.tconstruct.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TPlayerStats implements IInventory
{
	public WeakReference<EntityPlayer> player;
	public int level;
	public int health;
	public int hunger;
	public boolean beginnerManual;
	public boolean materialManual;
	public boolean smelteryManual;
	
	public ItemStack[] inventory = new ItemStack[7];
	
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
            System.out.println("Took something from slot "+slot);
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
    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }
    
    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        //System.out.println("Changed slot "+slot);
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
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
    }
    
    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }
    
    public void openChest () {}
    public void closeChest () {}
    
    @Override
    public boolean isStackValidForSlot (int slot, ItemStack itemstack)
    {
        return false;
    }
}
