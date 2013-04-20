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
	
	public ItemStack[] armor = new ItemStack[7];
	
    @Override
    public int getSizeInventory ()
    {
        return armor.length;
    }
    
    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return null;
    }
    
    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        return null;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }
    
    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        
    }
    
    @Override
    public String getInvName ()
    {
        return null;
    }
    
    @Override
    public boolean isInvNameLocalized ()
    {
        return false;
    }
    
    @Override
    public int getInventoryStackLimit ()
    {
        return 0;
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
    
    @Override
    public void openChest ()
    {
        
    }
    
    @Override
    public void closeChest ()
    {
        
    }
    
    @Override
    public boolean isStackValidForSlot (int slot, ItemStack itemstack)
    {
        return false;
    }
}
