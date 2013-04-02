package mods.tinker.tconstruct.logic;

import mods.tinker.common.InventoryLogic;
import mods.tinker.tconstruct.container.PartCrafterChestContainer;
import mods.tinker.tconstruct.container.PartCrafterContainer;
import mods.tinker.tconstruct.crafting.PatternBuilder;
import mods.tinker.tconstruct.items.Pattern;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PartCrafterLogic extends InventoryLogic
{
	boolean craftedTop;
	boolean craftedBottom;

	public PartCrafterLogic()
	{
		super(10);
		craftedTop = false;
		craftedBottom = false;
	}

	@Override
	public String getDefaultName ()
	{
		return "toolstation.parts";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			for (int zPos = z-1; zPos <= z+1; zPos++)
			{
				TileEntity tile = world.getBlockTileEntity(xPos, y, zPos);
				if (tile != null && tile instanceof PatternChestLogic)
					return new PartCrafterChestContainer(inventoryplayer, this, (PatternChestLogic)tile);
			}
		}
		return new PartCrafterContainer(inventoryplayer, this);
	}

	//Called when emptying a slot, not when another item is placed in it
	public ItemStack decrStackSize (int slotID, int quantity)
	{
		ItemStack returnStack = super.decrStackSize(slotID, quantity);
		tryBuildPart(slotID);
		return returnStack;
	}
	
	public void tryBuildPart(int slotID)
	{
		if (slotID == 4 || slotID == 5)
		{
			if (!craftedTop)
			{
				int value = PatternBuilder.instance.getPartValue(inventory[2]);
				Pattern item = (Pattern)inventory[0].getItem();
				int cost = item != null ? item.getPatternCost(inventory[0].getItemDamage()) : 0;
				if (value > 0 && cost > 0)
				{
					int decrease = cost / value;
					if (cost % value != 0)
						decrease++;
					super.decrStackSize(2, decrease); //Call super to avoid crafting again
				}
			}
			
			if (inventory[4] != null || inventory[5] != null)
				craftedTop = true;
			else
				craftedTop = false;
		}

		if (!craftedTop)
			buildTopPart();
		
		if (slotID == 6 || slotID == 7)
		{
			if (!craftedBottom)
			{
				int value = PatternBuilder.instance.getPartValue(inventory[3]);
				//int cost = ((Pattern)inventory[1].getItem()).getPatternCost(inventory[1].getItemDamage());
				Pattern item = (Pattern)inventory[1].getItem();
				int cost = item != null ? item.getPatternCost(inventory[1].getItemDamage()) : 0;
				if (value > 0 && cost > 0)
				{
					int decrease = cost / value;
					if (cost % value != 0)
						decrease++;
					super.decrStackSize(3, decrease); //Call super to avoid crafting again
				}
			}
			
			if (inventory[6] != null || inventory[7] != null)
				craftedBottom = true;
			else
				craftedBottom = false;
		}

		if (!craftedBottom)
			buildBottomPart();
	}

	//Called when a slot has something placed into it.
	public void setInventorySlotContents (int slot, ItemStack itemstack)
	{
		super.setInventorySlotContents(slot, itemstack);
		if ((slot == 0 || slot == 2) && !craftedTop)
			buildTopPart();
		if ((slot == 1 || slot == 3) && !craftedBottom)
			buildBottomPart();
	}

	void buildTopPart ()
	{
		ItemStack[] parts = PatternBuilder.instance.getToolPart(inventory[2], inventory[0], inventory[1]);
		if (parts != null)
		{
			inventory[4] = parts[0];
			inventory[5] = parts[1];
		}
		else
		{
			inventory[4] = inventory[5] = null;
		}
	}

	void buildBottomPart ()
	{
		ItemStack[] parts = PatternBuilder.instance.getToolPart(inventory[3], inventory[1], inventory[0]);
		if (parts != null)
		{
			inventory[6] = parts[0];
			inventory[7] = parts[1];
		}
		else
		{
			inventory[6] = inventory[7] = null;
		}
	}

	/* NBT */
	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		craftedTop = tags.getBoolean("CraftedTop");
		craftedBottom = tags.getBoolean("CraftedBottom");
	}

	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setBoolean("CraftedTop", craftedTop);
		tags.setBoolean("CraftedBottom", craftedBottom);
	}
}
