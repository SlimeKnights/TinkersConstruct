package tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tinker.common.IActiveLogic;
import tinker.common.InventoryLogic;
import tinker.tconstruct.container.FrypanContainer;
import cpw.mods.fml.common.registry.GameRegistry;

/* Slots
 * 0: Frying pan item
 * 1: Fuel
 * 2-9: Food
 */

public class FrypanLogic extends EquipLogic 
	implements IActiveLogic
{
	boolean active;
	public int fuel;
	public int fuelGague;
	public int progress;

	public FrypanLogic()
	{
		super(10);
		active = false;
	}

	@Override
	public String getInvName ()
	{
		return "crafters.frypan";
	}

	@Override
	public boolean getActive ()
	{
		return active;
	}

	@Override
	public void setActive (boolean flag)
	{
		active = flag;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/* Fuel gauge */
	public int gaugeProgressScaled (int scale)
	{
		return (progress * scale) / 1000;
	}

	public int gaugeFuelScaled (int scale)
	{
		if (fuelGague == 0)
		{
			fuelGague = fuel;
			if (fuelGague == 0)
			{
				fuelGague = 1000;
			}
		}
		return (fuel * scale) / fuelGague;
	}

	/* Item cooking */
	public void updateEntity ()
	{
		boolean burning = isBurning();
		boolean updateInventory = false;
		if (fuel <= 0 && canCook())
		{
			fuel = fuelGague = (int) (getItemBurnTime(inventory[1]) * 2.5);
			if (fuel > 0)
			{
				if (inventory[1].getItem().hasContainerItem()) //Fuel slot
				{
					inventory[1] = new ItemStack(inventory[1].getItem().getContainerItem());
				}
				else
				{
					inventory[1].stackSize--;
				}
				if (inventory[1].stackSize <= 0)
				{
					inventory[1] = null;
				}
				updateInventory = true;
			}
		}
		if (isBurning() && canCook())
		{
			progress++;
			if (progress >= 1000)
			{
				progress = 0;
				cookItems();
				updateInventory = true;
			}
		}
		else
		{
			progress = 0;
		}
		if (fuel > 0)
		{
			fuel--;
		}
		if (burning != isBurning())
		{
			setActive(isBurning());
			updateInventory = true;
		}
		if (updateInventory)
		{
			onInventoryChanged();
		}
	}

	public void cookItems ()
	{
		//System.out.println("Trying to cook");
		if (!canCook())
			return;

		//System.out.println("Can cook");
		for (int id = 2; id < 10; id++) //Check every slot
		{
			if (canCook())
			{
				ItemStack result = getResultFor(inventory[id]);
				if (result != null)
				{
					int ids = 2;
					boolean placed = false;
					while (ids < 10 && !placed) //Try to merge stacks first
					{
						if (inventory[ids] != null && inventory[ids].isItemEqual(result) && inventory[ids].stackSize < inventory[ids].getMaxStackSize())
						{
							if (inventory[ids].stackSize + result.stackSize <= inventory[ids].getMaxStackSize())
							{
								inventory[ids].stackSize += result.stackSize;
								placed = true;
							}
							else
							{
								int decrement = inventory[ids].getMaxStackSize() - inventory[ids].stackSize;
								inventory[ids].stackSize = inventory[ids].getMaxStackSize();
								result.stackSize -= decrement;
							}
						}
						ids++;
					}

					ids = 2;
					while (!placed && ids < 10) //Place remaining in slot
					{
						if (inventory[ids] == null)
						{
							inventory[ids] = result;
							placed = true;
						}
						ids++;
					}
					
					if (placed)
						decrStackSize(id, 1);
				}
			}
		}
	}

	public boolean canCook ()
	{
		for (int id = 2; id < 10; id++)
		{
			if (inventory[id] == null) //Nothing here!
				continue;

			ItemStack result = getResultFor(inventory[id]);
			if (result == null) //Doesn't cook into anything
				continue;

			for (int slotid = 2; slotid < 10; slotid++)
			{
				if (inventory[slotid] == null)
					return true;

				else if (inventory[slotid].isItemEqual(result) && inventory[slotid].stackSize + result.stackSize <= inventory[slotid].getMaxStackSize())
					return true;
			}
		}
		return false;
	}

	public boolean isBurning ()
	{
		return fuel > 0;
	}

	public ItemStack getResultFor (ItemStack stack)
	{
		ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(stack);
		if (result != null && result.getItem() instanceof ItemFood) //Only valid for food
			return result.copy();

		return null;
	}

	public static int getItemBurnTime (ItemStack stack)
	{
		if (stack == null)
		{
			return 0;
		}
		else
		{
			int itemID = stack.getItem().itemID;
			Item item = stack.getItem();

			if (stack.getItem() instanceof ItemBlock && Block.blocksList[itemID] != null)
			{
				Block block = Block.blocksList[itemID];

				if (block == Block.woodSingleSlab)
				{
					return 150;
				}
				
				if (block == Block.wood)
				{
					return 2400;
				}

				if (block.blockMaterial == Material.wood)
				{
					return 300;
				}
			}
			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemHoe && ((ItemHoe) item).func_77842_f().equals("WOOD"))
				return 200;
			if (itemID == Item.stick.itemID)
				return 100;
			if (itemID == Item.coal.itemID)
				return 800;
			if (itemID == Item.bucketLava.itemID)
				return 20000;
			if (itemID == Block.sapling.blockID)
				return 100;
			if (itemID == Item.blazeRod.itemID)
				return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}

	/* NBT */
	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		active = tags.getBoolean("Active");
		fuel = tags.getInteger("Fuel");
		fuelGague = tags.getInteger("FuelGague");
	}

	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setBoolean("Active", active);
		tags.setInteger("Fuel", fuel);
		tags.setInteger("FuelGague", fuelGague);
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new FrypanContainer(inventoryplayer, this);
	}
	
	/*@Override
	public boolean canDropInventorySlot(int slot)
	{
		if (slot == 0)
			return false;
		else
			return true;
	}*/
}
