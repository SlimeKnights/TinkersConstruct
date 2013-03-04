package tinker.tconstruct.container;

import java.util.ArrayList;
import java.util.List;

import tinker.tconstruct.logic.SmelteryLogic;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class SmelteryContainer extends Container
{
	public SmelteryLogic logic;
	public InventoryPlayer playerInv;
	public int fuel = 0;
	int slotRow;

	public SmelteryContainer(InventoryPlayer inventoryplayer, SmelteryLogic smeltery)
	{
		logic = smeltery;
		playerInv = inventoryplayer;
		if (smeltery.layers > 2)
		{
			slotRow = -1;
			updateRows(0);
		}
		else
		{
			if (smeltery.layers > 0)
			{
				for (int y = 0; y < 3; y++)
					for (int x = 0; x < 3; x++)
						this.addSlotToContainer(new Slot(smeltery, x + y * 3, -34 + x * 22, 8 + y * 18));
			}
			if (smeltery.layers > 1)
			{
				for (int y = 0; y < 3; y++)
					for (int x = 0; x < 3; x++)
						this.addSlotToContainer(new Slot(smeltery, 9 + x + y * 3, -34 + x * 22, 62 + y * 18));
			}
			

			/* Player inventory */
			for (int column = 0; column < 3; column++)
			{
				for (int row = 0; row < 9; row++)
				{
					this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 54 + row * 18, 84 + column * 18));
				}
			}

			for (int column = 0; column < 9; column++)
			{
				this.addSlotToContainer(new Slot(inventoryplayer, column, 54 + column * 18, 142));
			}
		}
	}
	
	public int updateRows(int invRow)
	{
		if (invRow != slotRow)
		{
			slotRow = invRow;
			this.inventorySlots.clear();
			this.inventoryItemStacks.clear();
			
			for (int y = 0; y < 8; y++)
				for (int x = 0; x < 3; x++)
					this.addSlotToContainer(new Slot(logic, x + y*3 + invRow*3, -34 + x * 22, 8 + y * 18));
			
			/* Player inventory */
			for (int column = 0; column < 3; column++)
			{
				for (int row = 0; row < 9; row++)
				{
					this.addSlotToContainer(new Slot(playerInv, row + column * 9 + 9, 54 + row * 18, 84 + column * 18));
				}
			}

			for (int column = 0; column < 9; column++)
			{
				this.addSlotToContainer(new Slot(playerInv, column, 54 + column * 18, 142));
			}
			return slotRow;
		}
		return -1;
	}

	public int scrollTo(float scrollPos)
	{
		float total = (logic.getSizeInventory() - 24) / 3;
		int rowPos = (int) (total * scrollPos);
		return updateRows(rowPos);
	}

	@Override
	public void detectAndSendChanges () //TODO: Sync with this
	{
		super.detectAndSendChanges();
		/*for (int i = 0; i < crafters.size(); i++)
		{
		    ICrafting icrafting = (ICrafting)crafters.get(i);
		    if (progress != logic.progress)
		    {
		        icrafting.sendProgressBarUpdate(this, 0, logic.progress);
		    }
		    if (fuel != logic.fuel)
		    {
		        icrafting.sendProgressBarUpdate(this, 1, logic.fuel);
		    }
		    if (fuelGague != logic.fuelGague)
		    {
		        icrafting.sendProgressBarUpdate(this, 2, logic.fuelGague);
		    }
		}

		progress = logic.progress;
		fuel = logic.fuel;
		fuelGague = logic.fuelGague;*/
	}

	public void updateProgressBar (int id, int value)
	{
		if (id == 0)
		{
			logic.fuelGague = value;
		}
		/* if (id == 1)
		 {
		     logic.fuel = value;
		 }*/
		/*if (id == 2)
		{
		    logic.fuelGague = value;
		}*/
	}

	@Override
	public boolean canInteractWith (EntityPlayer entityplayer)
	{
		return logic.isUseableByPlayer(entityplayer);
	}

	public ItemStack transferStackInSlot (EntityPlayer player, int slotID)
	{
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotID < logic.getSizeInventory())
			{
				if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false))
			{
				return null;
			}

			if (slotStack.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	/*protected boolean mergeItemStack (ItemStack inputStack, int par2, int par3, boolean flag)
	{
		boolean merged = false;
        int slotPos = par2;

        if (flag)
        {
            slotPos = par3 - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if (inputStack.isStackable())
        {
            while (inputStack.stackSize > 0 && (!flag && slotPos < par3 || flag && slotPos >= par2))
            {
                slot = (Slot)this.inventorySlots.get(slotPos);
                slotStack = slot.getStack();

                //System.out.println("Boom");
                if (slotStack != null && ItemStack.areItemStacksEqual(inputStack, slotStack) && !inputStack.getHasSubtypes())
                {
                	//System.out.println("DeyadaBoomDeyada");
                    int totalSize = slotStack.stackSize + inputStack.stackSize;

                    if (totalSize <= inputStack.getMaxStackSize())
                    {
                        inputStack.stackSize = 0;
                        slotStack.stackSize = totalSize;
                        slot.onSlotChanged();
                        merged = true;
                    }
                    else if (slotStack.stackSize < inputStack.getMaxStackSize())
                    {
                        inputStack.stackSize -= inputStack.getMaxStackSize() - slotStack.stackSize;
                        slotStack.stackSize = inputStack.getMaxStackSize();
                        slot.onSlotChanged();
                        merged = true;
                    }
                }

                //System.out.println("Heyo~");
                if (flag)
                {
                    --slotPos;
                }
                else
                {
                    ++slotPos;
                }
            }
        }

        if (inputStack.stackSize > 0)
        {
        	//System.out.println("Boom");
            if (flag)
            {
                slotPos = par3 - 1;
            }
            else
            {
                slotPos = par2;
            }

            while (!flag && slotPos < par3 || flag && slotPos >= par2)
            {
                slot = (Slot)this.inventorySlots.get(slotPos);
                slotStack = slot.getStack();

            	System.out.println("Boom");
                if (slotStack == null)
                {
                	System.out.println("Deya");
                    slot.putStack(inputStack.copy());
                    slot.onSlotChanged();
                    inputStack.stackSize = 0;
                    merged = true;
                    break;
                }

                if (flag)
                {
                    --slotPos;
                }
                else
                {
                    ++slotPos;
                }
            }
        }

        return merged;
	}*/
}
