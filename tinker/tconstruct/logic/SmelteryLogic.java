package tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import tinker.common.CoordTuple;
import tinker.common.IActiveLogic;
import tinker.common.IFacingLogic;
import tinker.common.InventoryLogic;
import tinker.tconstruct.TContent;
import tinker.tconstruct.container.SmelteryContainer;
import tinker.tconstruct.crafting.Smeltery;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic 
	implements IActiveLogic, IFacingLogic
{
	public boolean validStructure;
	byte direction;
	int internalTemp;
	int maxTemp;
	int useTime;
	public int fuelGague;
	
	CoordTuple lavaTank;
	CoordTuple drain;
	public CoordTuple centerPos;
	
	public int[] activeTemps;
	public int[] meltingTemps;
	int tick;

	public SmelteryLogic()
	{
		super(9);
		activeTemps = new int[9];
		meltingTemps = new int[9];
		for (int i = 0; i < 9; i++)
		{
			activeTemps[i] = 20;
			meltingTemps[i] = 0;
		}
	}

	/* Misc */
	@Override
	public String getInvName ()
	{
		return "crafters.Smeltery";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new SmelteryContainer(inventoryplayer, this);
	}

	@Override
	public byte getDirection ()
	{
		return direction;
	}

	@Override
	public void setDirection (byte dir)
	{
		direction = dir;
	}

	@Override
	public boolean getActive ()
	{
		return validStructure;
	}

	@Override
	public void setActive (boolean flag)
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean canFaceVertical ()
	{
		return false;
	}

	public int getScaledFuelGague (int scale)
	{
		return (fuelGague * scale) / 52;
	}

	public int getInternalTemperature ()
	{
		return internalTemp;
	}

	public int getTempForSlot (int slot)
	{
		return activeTemps[slot];
	}

	/* Updating */
	public void updateEntity ()
	{
		//System.out.println("tick");
		tick++;
		if (tick % 4 == 0 && !worldObj.isRemote)
			heatItems();

		if (tick == 20)
		{
			tick = 0;
			checkValidPlacement();
			updateFuelGague();

			if (useTime > 0)
				useTime--;
			/*if (!worldObj.isRemote)
				matchInventoryToWorld();*/

			if (validStructure && useTime == 0)
			{
				TileEntity tank = worldObj.getBlockTileEntity(lavaTank.x, lavaTank.y, lavaTank.z);
				if (tank instanceof ILiquidTank)
				{
					LiquidStack liquid = ((ILiquidTank) tank).drain(10, true);
					if (liquid != null)
					{
						//System.out.println("Drained " + liquid.amount);
						useTime += liquid.amount;
					}
				}
			}
		}
	}

	void heatItems ()
	{
		for (int i = 0; i < 9; i++)
		{
			if (meltingTemps[i] > 20 && this.isStackInSlot(i))
			{
				if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
					activeTemps[i] += 1;
				else if (meltingTemps[i] >= activeTemps[i])
				{
					ItemStack result = getResultFor(inventory[i]);
					if (result != null)
					{
						inventory[i] = result;
						//setWorldToInventory();
					}
				}

			}

			else
				activeTemps[i] = 20;
		}
	}
	
	void updateTemperatures()
	{
		for (int i = 0; i < 9; i++)
		{
			meltingTemps[i] = Smeltery.instance.getSmeltingTemperature(inventory[i]);
		}
	}

	void updateFuelGague ()
	{
		if (lavaTank == null)
			return;

		TileEntity tank = worldObj.getBlockTileEntity(lavaTank.x, lavaTank.y, lavaTank.z);
		if (tank == null)
		{
			fuelGague = 0;
		}
		if (tank instanceof ILiquidTank)
		{
			LiquidStack liquid = ((ILiquidTank) tank).getLiquid();
			int capacity = ((ILiquidTank) tank).getCapacity();
			if (liquid != null)
				fuelGague = liquid.amount * 52 / capacity;
			else
				fuelGague = 0;
		}
	}

	public ItemStack getResultFor (ItemStack stack)
	{
		return Smeltery.instance.getSmeltingResult(stack);
	}

	/* Inventory */
	public int getInventoryStackLimit ()
	{
		return 1;
	}
	
	public void onInventoryChanged()
	{
		updateTemperatures();
		super.onInventoryChanged();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/*@Override
	public void setInventorySlotContents (int slot, ItemStack itemstack)
	{
		super.setInventorySlotContents(slot, itemstack);
		setWorldToInventory();
	}

	@Override
	public ItemStack decrStackSize (int slot, int quantity)
	{
		ItemStack stack = super.decrStackSize(slot, quantity);
		setWorldToInventory();
		return stack;
	}*/

	/* World-inventory matching */
	/*void matchInventoryToWorld ()
	{
		switch (getDirection())
		{
		case 2: // +z
			grabWorldBlocks(xCoord, yCoord, zCoord + 2);
			break;
		case 3: // -z
			grabWorldBlocks(xCoord, yCoord, zCoord - 2);
			break;
		case 4: // +x
			grabWorldBlocks(xCoord + 2, yCoord, zCoord);
			break;
		case 5: // -x
			grabWorldBlocks(xCoord - 2, yCoord, zCoord);
			break;
		}
	}*/

	/*void setWorldToInventory ()
	{
		switch (getDirection())
		{
		case 2: // +z
			setWorldToInventory(xCoord, yCoord, zCoord + 2);
			break;
		case 3: // -z
			setWorldToInventory(xCoord, yCoord, zCoord - 2);
			break;
		case 4: // +x
			setWorldToInventory(xCoord + 2, yCoord, zCoord);
			break;
		case 5: // -x
			setWorldToInventory(xCoord - 2, yCoord, zCoord);
			break;
		}
	}*/

	/*public void grabWorldBlocks (int x, int y, int z)
	{
		for (int xPos = 0; xPos <= 2; xPos++)
		{
			for (int zPos = 0; zPos <= 2; zPos++)
			{
				int bID = worldObj.getBlockId(xPos + x - 1, y, zPos + z - 1);
				int md = worldObj.getBlockMetadata(xPos + x - 1, y, zPos + z - 1);

				ItemStack stack = inventory[xPos + zPos * 3];
				if (stack == null)
				{
					if (bID == 0)
						inventory[xPos + zPos * 3] = null;
					else if (stack == null || (stack.itemID != bID && stack.getItemDamage() != md))
						inventory[xPos + zPos * 3] = new ItemStack(bID, 1, md);
				}
			}
		}
	}*/

	/*public void setWorldToInventory (int x, int y, int z)
	{
		for (int xPos = 0; xPos <= 2; xPos++)
		{
			for (int zPos = 0; zPos <= 2; zPos++)
			{
				ItemStack stack = inventory[xPos + zPos * 3];
				if (stack == null)
					worldObj.setBlockWithNotify(xPos + x - 1, y, zPos + z - 1, 0);

				else if (stack.getItem() instanceof ItemBlock && Block.blocksList[stack.itemID] != null)
				{
					worldObj.setBlockAndMetadataWithNotify(xPos + x - 1, y, zPos + z - 1, stack.itemID, stack.getItemDamage());
					meltingTemps[xPos + zPos * 3] = Smeltery.instance.getSmeltingTemperature(stack);
					if (meltingTemps[xPos + zPos * 3] < bottomTemps[xPos + zPos * 3])
						bottomTemps[xPos + zPos * 3] = meltingTemps[xPos + zPos * 3];
				}
			}
		}
	}*/

	public void checkValidPlacement ()
	{
		switch (getDirection())
		{
		case 2: // +z
			checkValidStructure(xCoord, yCoord, zCoord + 2);
			break;
		case 3: // -z
			checkValidStructure(xCoord, yCoord, zCoord - 2);
			break;
		case 4: // +x
			checkValidStructure(xCoord + 2, yCoord, zCoord);
			break;
		case 5: // -x
			checkValidStructure(xCoord - 2, yCoord, zCoord);
			break;
		}
	}

	public void checkValidStructure (int x, int y, int z)
	{
		int numBricks = 0;
		boolean hasLavaTank = false;

		//Check outer layer
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			int southID = worldObj.getBlockId(xPos, y, z - 2);
			int northID = worldObj.getBlockId(xPos, y, z + 2);
			if (southID == TContent.searedBrick.blockID)
				numBricks++;
			else if (southID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(xPos, y, z - 2);
				hasLavaTank = true;
			}

			if (northID == TContent.searedBrick.blockID)
				numBricks++;
			else if (northID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(xPos, y, z + 2 );
				hasLavaTank = true;
			}
		}
		for (int zPos = z - 1; zPos <= z + 1; zPos++)
		{
			int westID = worldObj.getBlockId(x - 2, y, zPos);
			int eastID = worldObj.getBlockId(x + 2, y, zPos);
			if (westID == TContent.searedBrick.blockID)
				numBricks++;
			else if (westID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple(x - 2, y, zPos);
				hasLavaTank = true;
			}

			if (eastID == TContent.searedBrick.blockID)
				numBricks++;
			else if (eastID == TContent.lavaTank.blockID)
			{
				lavaTank = new CoordTuple( x + 2, y, zPos );
				hasLavaTank = true;
			}
		}

		//Check floor
		for (int xPos = x - 1; xPos <= x + 1; xPos++)
		{
			for (int zPos = z - 1; zPos <= z + 1; zPos++)
			{
				if (worldObj.getBlockId(xPos, y - 1, zPos) == TContent.searedBrick.blockID)
					numBricks++;
			}
		}

		if (numBricks == 19 && hasLavaTank)
		{
			if (!validStructure)
			{
				centerPos = new CoordTuple(x, y, z);
				validStructure = true;
			}
			internalTemp = 550;
		}
		else
		{
			validStructure = false;
			internalTemp = 20;
		}
	}

	/* NBT */
	@Override
	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		direction = tags.getByte("Direction");
		useTime = tags.getInteger("UseTime");
		meltingTemps = tags.getIntArray("MeltingTemps");
		activeTemps = tags.getIntArray("ActiveTemps");
	}

	@Override
	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setByte("Direction", direction);
		tags.setInteger("UseTime", useTime);
		tags.setIntArray("MeltingTemps", meltingTemps);
		tags.setIntArray("ActiveTemps", activeTemps);
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
