package mods.tinker.armory.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class ToolrackLogic extends TileEntity
	implements IInventory
{
	ItemStack[] tools;
	
	public ToolrackLogic()
	{
		tools = new ItemStack[2];
	}

	@Override
	public int getSizeInventory() 
	{
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return tools[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) 
	{
		if (this.tools[slot] == null)
            return null;
        
        else
        {
            ItemStack stack;

            if (this.tools[slot].stackSize <= quantity)
            {
                stack = this.tools[slot];
                this.tools[slot] = null;
            }
            else
            {
                stack = this.tools[slot].splitStack(quantity);

                if (this.tools[slot].stackSize == 0)
                {
                    this.tools[slot] = null;
                }
            }

            this.onInventoryChanged();
            return stack;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) 
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) 
	{
		this.updateItem(slot, stack);

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
	}
	
	void updateItem(int slot, ItemStack stack)
    {
		this.tools[slot] = stack.copy();
		//System.out.println("Item name: "+tools[slot].getItemName());
        //float var3 = 45.0F;

        /*if (mod_ToolRack.reversedItems.contains(Integer.valueOf(var2.itemID)))
        {
            var3 = -45.0F;
        }
        else if (mod_ToolRack.straightItems.contains(Integer.valueOf(var2.itemID)))
        {
            var3 = 0.0F;
        }*/
        
        //this.rotations[var1] = var3;
    }
	
	public boolean canHoldItem(ItemStack stack)
    {
		return stack.getMaxStackSize() == 1;
        //return var1.getMaxStackSize() == 1 || mod_ToolRack.additionalTools.contains(Integer.valueOf(var1.itemID));
    }

    public boolean isItemInColumn(int var1)
    {
        return this.tools[var1] != null;
    }

    public ItemStack takeItemInColumn(int var1)
    {
        return this.tools[var1] != null ? this.decrStackSize(var1, 1) : null;
    }


	@Override
	public String getInvName() 
	{
		return "Toolrack";
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
	{
		return true;
	}

	@Override
	public void openChest() 
	{

	}

	@Override
	public void closeChest() 
	{

	}
	
	@Override
    public void readFromNBT(NBTTagCompound var1)
    {
        super.readFromNBT(var1);
        NBTTagList tagList = var1.getTagList("Items");
        this.tools = new ItemStack[this.getSizeInventory()];

        for (int iter = 0; iter < tagList.tagCount(); ++iter)
        {
            NBTTagCompound tagCompund = (NBTTagCompound)tagList.tagAt(iter);
            int slot = tagCompund.getByte("Slot");

            if (slot >= 0 && slot < this.tools.length)
            {
                this.updateItem(slot, ItemStack.loadItemStackFromNBT(tagCompund));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound var1)
    {
        super.writeToNBT(var1);
        NBTTagList tagList = new NBTTagList();

        for (byte iter = 0; iter < this.tools.length; ++iter)
        {
            if (this.tools[iter] != null)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", iter);
                this.tools[iter].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }

        var1.setTag("Items", tagList);
    }

    @Override
	public Packet getDescriptionPacket()
    {
    	NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }
    
    @Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
    	readFromNBT(pkt.customParam1);
	}

	@Override
	public boolean isInvNameLocalized ()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot (int i, ItemStack itemstack)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
