package tinker.tconstruct.container;

import tinker.tconstruct.logic.SmelteryLogic;
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
	public int fuel = 0;

	public SmelteryContainer(InventoryPlayer inventoryplayer, SmelteryLogic smeltery)
	{
		logic = smeltery;
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
		
		if (smeltery.layers > 2)
		{
			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 3; x++)
					this.addSlotToContainer(new Slot(smeltery, 18 + x + y * 3, -34 + x * 22, 116 + y * 18));
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

	@Override
	public void detectAndSendChanges ()
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

	protected boolean mergeItemStack (ItemStack par1ItemStack, int par2, int par3, boolean par4)
	{
		boolean var5 = false;
		int var6 = par2;

		if (par4)
		{
			var6 = par3 - 1;
		}

		Slot var7;
		ItemStack var8;

		if (par1ItemStack.isStackable())
		{
			while (par1ItemStack.stackSize > 0 && (!par4 && var6 < par3 || par4 && var6 >= par2))
			{
				var7 = (Slot) this.inventorySlots.get(var6);
				var8 = var7.getStack();

				if (var8 != null && var8.itemID == par1ItemStack.itemID && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == var8.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, var8))
				{
					int var9 = var8.stackSize + par1ItemStack.stackSize;

					if (var9 <= par1ItemStack.getMaxStackSize())
					{
						par1ItemStack.stackSize = 0;
						var8.stackSize = var9;
						var7.onSlotChanged();
						var5 = true;
					}
					else if (var8.stackSize < par1ItemStack.getMaxStackSize())
					{
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - var8.stackSize;
						var8.stackSize = par1ItemStack.getMaxStackSize();
						var7.onSlotChanged();
						var5 = true;
					}
				}

				if (par4)
				{
					--var6;
				}
				else
				{
					++var6;
				}
			}
		}

		if (par1ItemStack.stackSize > 0)
		{
			if (par4)
			{
				var6 = par3 - 1;
			}
			else
			{
				var6 = par2;
			}

			while (!par4 && var6 < par3 || par4 && var6 >= par2)
			{
				var7 = (Slot) this.inventorySlots.get(var6);
				var8 = var7.getStack();

				if (var8 == null)
				{
					var7.putStack(par1ItemStack.copy());
					var7.onSlotChanged();
					par1ItemStack.stackSize = 0;
					var5 = true;
					break;
				}

				if (par4)
				{
					--var6;
				}
				else
				{
					++var6;
				}
			}
		}

		return var5;
	}
}
